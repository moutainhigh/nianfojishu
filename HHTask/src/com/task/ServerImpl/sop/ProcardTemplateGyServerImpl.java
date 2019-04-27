package com.task.ServerImpl.sop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Alignment;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.util.FileCopyUtils;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.sop.ManualOrderPlanServer;
import com.task.Server.sop.ProcardServer;
import com.task.Server.sop.ProcardTemplateGyServer;
import com.task.Server.sop.ProcardTemplateServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.Calendar;
import com.task.entity.Category;
import com.task.entity.ChartNOGzType;
import com.task.entity.ChartNOSC;
import com.task.entity.DeptNumber;
import com.task.entity.DeptNumberVo;
import com.task.entity.Goods;
import com.task.entity.GoodsStore;
import com.task.entity.Price;
import com.task.entity.ProcardBl;
import com.task.entity.Users;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.gongyi.GongyiGuichengAffix;
import com.task.entity.gongyi.gongxu.ProcessData;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.gzbj.ProcessGzstore;
import com.task.entity.sop.Banbenxuhao;
import com.task.entity.sop.DesignfeedbackNotice;
import com.task.entity.sop.DesignfeedbackNoticeFile;
import com.task.entity.sop.ManualOrderPlan;
import com.task.entity.sop.ManualOrderPlanDetail;
import com.task.entity.sop.Procard;
import com.task.entity.sop.ProcardAboutBanBenApply;
import com.task.entity.sop.ProcardBanBenJudge;
import com.task.entity.sop.ProcardProductRelation;
import com.task.entity.sop.ProcardReProduct;
import com.task.entity.sop.ProcardReProductFile;
import com.task.entity.sop.ProcardSbWaster;
import com.task.entity.sop.ProcardSbWasterxc;
import com.task.entity.sop.ProcardSbWg;
import com.task.entity.sop.ProcardSbWgDetail;
import com.task.entity.sop.ProcardSbWgLog;
import com.task.entity.sop.ProcardSbWw;
import com.task.entity.sop.ProcardSbWwDetail;
import com.task.entity.sop.ProcardTBanbenRelation;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcardTemplateAboutsb;
import com.task.entity.sop.ProcardTemplateAboutsbDetail;
import com.task.entity.sop.ProcardTemplateAboutsbcltype;
import com.task.entity.sop.ProcardTemplateBanBen;
import com.task.entity.sop.ProcardTemplateBanBenApply;
import com.task.entity.sop.ProcardTemplateBanBenJudges;
import com.task.entity.sop.ProcardTemplateBanBenJudgesFile;
import com.task.entity.sop.ProcardTemplateChangeLog;
import com.task.entity.sop.ProcardTemplatesbDifference;
import com.task.entity.sop.ProcardTemplatesbDifferenceDetail;
import com.task.entity.sop.ProcardWGCenter;
import com.task.entity.sop.ProcessAboutBanBenApply;
import com.task.entity.sop.ProcessAndWgProcardTem;
import com.task.entity.sop.ProcessChangeNotice;
import com.task.entity.sop.ProcessChangeNoticeFile;
import com.task.entity.sop.ProcessFuLiaoTemplate;
import com.task.entity.sop.ProcessInfor;
import com.task.entity.sop.ProcessInforReceiveLog;
import com.task.entity.sop.ProcessInforWWApply;
import com.task.entity.sop.ProcessInforWWApplyDetail;
import com.task.entity.sop.ProcessInforWWProcard;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ProcessTemplateFile;
import com.task.entity.sop.ProcessinforFuLiao;
import com.task.entity.sop.ProcessinforPeople;
import com.task.entity.sop.TechnicalchangeLog;
import com.task.entity.sop.TechnicalchangeLogDetail;
import com.task.entity.sop.WaigouOrder;
import com.task.entity.sop.WaigouPlan;
import com.task.entity.sop.WaigouPlanLock;
import com.task.entity.sop.WaigouPlanclApply;
import com.task.entity.sop.WaigouWaiweiPlan;
import com.task.entity.sop.ycl.PanelSize;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.CompanyInfo;
import com.task.entity.zgkh.AssessPersonnel;
import com.task.entity.zhuseroffer.NoPriceprocess;
import com.task.util.MKUtil;
import com.task.util.StationResource;
import com.task.util.Upload;
import com.task.util.Util;
import com.tast.entity.zhaobiao.ZhUser;

import ext.huawei.gdp.ecfeeback.bean.ECAFeedBackBean;
import ext.huawei.gdp.ecfeeback.bean.ResponseInfoBean;
import ext.huawei.gdp.ecfeeback.bean.SubmitECFeedBack;
import ext.huawei.gdp.ecfeeback.service.AutoFeedBackService;

public class ProcardTemplateGyServerImpl implements ProcardTemplateGyServer {
	private ProcardServer procardServer;
	private StringBuffer strbu = new StringBuffer();
	private ProcardTemplateServer procardTemplateServer;
	private final static String gxtzPath = "/upload/file/processTz";
	private float sumprice = 0f;
	private TotalDao totalDao;
	private Integer bomXuhao = 1;
	private Integer sbData = 0;
	private Integer thisminId = 0;
	private String jsbcmsg = "";
	private List<YuanclAndWaigj> aboutwgjList;
	private List<ProcardTemplate> aboutptList;
	private Map<String, Integer> banBenxuhaoMap;
	private ManualOrderPlanServer manualPlanServer;
	private List<Integer> hasbd = new ArrayList<Integer>();
	int sdnum = 0;
	private ResultSet gzType;

	public TotalDao getTotalDao() {
		return totalDao;
	}

	public void setTotalDao(TotalDao totalDao) {
		this.totalDao = totalDao;
	}

	public ProcardServer getProcardServer() {
		return procardServer;
	}

	public void setProcardServer(ProcardServer procardServer) {
		this.procardServer = procardServer;
	}

	public ProcardTemplateServer getProcardTemplateServer() {
		return procardTemplateServer;
	}

	public void setProcardTemplateServer(
			ProcardTemplateServer procardTemplateServer) {
		this.procardTemplateServer = procardTemplateServer;
	}

	public ManualOrderPlanServer getManualPlanServer() {
		return manualPlanServer;
	}

	public void setManualPlanServer(ManualOrderPlanServer manualPlanServer) {
		this.manualPlanServer = manualPlanServer;
	}

	@Override
	public Object[] findProcardTem(ProcardTemplate procardTemplate, int pageNo,
			int pageSize, String pageStatus) {
		// TODO Auto-generated method stub
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null);
		if (pageStatus != null && pageStatus.equals("ok")) {
			hql += " and id in (select min(id) from ProcardTemplate where bzStatus='已批准' and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除') group by markId) ";// 去除重复的数据
		} else {
			hql += " and id in (select min(id) from ProcardTemplate where (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')  group by markId) ";// 去除重复的数据
			Users user = Util.getLoginUser();
			if (procardTemplate.getBzStatus() == null
					|| procardTemplate.getBzStatus().length() == 0) {
				hql = hql + " and ((bzStatus ='待编制' and bianzhiId = "
						+ user.getId()
						+ ") or (bzStatus ='已编制' and jiaoduiId ="
						+ user.getId() + ") "
						+ "or (bzStatus ='已校对' and shenheId =" + user.getId()
						+ ") or (bzStatus ='已审核' and pizhunId =" + user.getId()
						+ ") or (bzStatus in ('已批准')))";
			} else if (procardTemplate.getBzStatus().equals("初始")
					|| procardTemplate.getBzStatus().equals("已批准")) {

			} else if (procardTemplate.getBzStatus().equals("待编制")) {
				hql = hql + " and bzStatus ='待编制' and bianzhiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已编制")) {
				hql = hql + " and bzStatus ='已编制' and jiaoduiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已校对")) {
				hql = hql + " and bzStatus ='已校对' and shenheId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已审核")) {
				hql = hql + " and bzStatus ='已审核' and pizhunId = "
						+ user.getId();
			}
		}
		hql += " and (banbenStatus is null or banbenStatus='' or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public Object[] findProcardTemForBz(ProcardTemplate procardTemplate,
			int pageNo, int pageSize, String type) {
		// TODO Auto-generated method stub
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null);
		// if (procardTemplate.getBzStatus() == null
		// || procardTemplate.getBzStatus().length() <= 0) {
		// procardTemplate.setBzStatus("待编制");
		// }
		// hql +=
		// " and id in (select min(id) from ProcardTemplate where banbenStatus is null or banbenStatus!='历史'  group by markId,procardStyle) ";//
		// 去除重复的数据
		Users user = Util.getLoginUser();
		if (user == null) {
			Object[] o = { null, 0 };
			return o;
		}
		String bzjdCountStr = getSpjdCount();
		Integer bzjdCount = Integer.parseInt(bzjdCountStr);
		if (bzjdCount == 2) {
			if (procardTemplate.getBzStatus() == null
					|| procardTemplate.getBzStatus().length() == 0) {
				hql = hql
						+ " and (bzStatus ='初始' or (bzStatus ='待编制' and bianzhiId = "
						+ user.getId() + ") or (bzStatus ='已编制' and pizhunId ="
						+ user.getId() + ")) and bzStatus !='已批准'";
			} else if (procardTemplate.getBzStatus().equals("初始")
					|| procardTemplate.getBzStatus().equals("已批准")) {

			} else if (procardTemplate.getBzStatus().equals("待编制")) {
				hql = hql + " and bzStatus ='待编制' and bianzhiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已编制")) {
				hql = hql + " and bzStatus ='已编制' and pizhunId = "
						+ user.getId();
			}
		} else if (bzjdCount == 3) {
			if (procardTemplate.getBzStatus() == null
					|| procardTemplate.getBzStatus().length() == 0) {
				hql = hql
						+ " and (bzStatus ='初始' or (bzStatus ='待编制' and bianzhiId = "
						+ user.getId()
						+ ") or (bzStatus ='已编制' and jiaoduiId ="
						+ user.getId() + ") "
						+ "or (bzStatus ='已校对' and pizhunId =" + user.getId()
						+ ")) and bzStatus !='已批准'";
			} else if (procardTemplate.getBzStatus().equals("初始")
					|| procardTemplate.getBzStatus().equals("已批准")) {

			} else if (procardTemplate.getBzStatus().equals("待编制")) {
				hql = hql + " and bzStatus ='待编制' and bianzhiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已编制")) {
				hql = hql + " and bzStatus ='已编制' and jiaoduiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已校对")) {
				hql = hql + " and bzStatus ='已校对' and pizhunId = "
						+ user.getId();
			}
		} else {
			if (procardTemplate.getBzStatus() == null
					|| procardTemplate.getBzStatus().length() == 0) {
				hql = hql
						+ " and ((bzStatus ='初始') or (bzStatus ='待编制' and bianzhiId = "
						+ user.getId()
						+ ") or (bzStatus ='已编制' and jiaoduiId ="
						+ user.getId() + ") "
						+ "or (bzStatus ='已校对' and shenheId =" + user.getId()
						+ ") or (bzStatus ='已审核' and pizhunId =" + user.getId()
						+ ")) and bzStatus !='已批准'";
			} else if (procardTemplate.getBzStatus().equals("初始")
					|| procardTemplate.getBzStatus().equals("已批准")) {

			} else if (procardTemplate.getBzStatus().equals("待编制")) {
				hql = hql + " and bzStatus ='待编制' and bianzhiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已编制")) {
				hql = hql + " and bzStatus ='已编制' and jiaoduiId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已校对")) {
				hql = hql + " and bzStatus ='已校对' and shenheId = "
						+ user.getId();
			} else if (procardTemplate.getBzStatus().equals("已审核")) {
				hql = hql + " and bzStatus ='已审核' and pizhunId = "
						+ user.getId();
			}
		}
		hql += " and (banbenStatus is null or banbenStatus='' or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')";
		hql = "from ProcardTemplate where id in(select rootId " + hql + ")";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);

		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public List<ProcardTemplate> findSonsForBz(Integer id,
			ProcardTemplate procardTemplate) {
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null, "markId");
		if (procardTemplate.getMarkId() != null
				&& procardTemplate.getMarkId().length() > 0) {
			hql += " and markId like '" + procardTemplate.getMarkId() + "%'";
		}

		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM编制节点数'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode == null) {
			valueCode = "4";
		}
		String addSql = "";
		if (valueCode.equals("2")) {
			addSql = " and (bzStatus='初始' or(bzStatus ='待编制' and bianzhiId = "
					+ user.getId() + ") or (bzStatus ='已编制' and pizhunId ="
					+ user.getId() + ")) and bzStatus!='已批准'";
		} else if (valueCode.equals("3")) {
			addSql = " and (bzStatus='初始' or(bzStatus ='待编制' and bianzhiId = "
					+ user.getId() + ") or (bzStatus ='已编制' and jiaoduiId ="
					+ user.getId() + ") "
					+ "or (bzStatus ='已校对' and pizhunId =" + user.getId()
					+ ")) and bzStatus!='已批准'";
		} else {
			addSql = " and (bzStatus='初始' or(bzStatus ='待编制' and bianzhiId = "
					+ user.getId() + ") or (bzStatus ='已编制' and jiaoduiId ="
					+ user.getId() + ") "
					+ "or (bzStatus ='已校对' and shenheId =" + user.getId()
					+ ") or (bzStatus ='已审核' and pizhunId =" + user.getId()
					+ ")) and bzStatus!='已批准'";
		}
		hql += " and rootId=? and (banbenStatus is null or banbenStatus='' or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')"
				+ addSql
				+ " and id in (select min(id) from ProcardTemplate where rootId=? and (bzStatus is null or bzStatus!='已批准') and( banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除') group by markId)"
				+ " order by bzStatus";

		List<ProcardTemplate> ptList = totalDao.query(hql, id, id);
		if (ptList != null && ptList.size() > 0) {
			for (ProcardTemplate pt : ptList) {
				ProcardTemplateBanBen ptbb = ProcardTemplateGyServerImpl
						.getPtbbBypt(totalDao, pt);
				if (ptbb != null) {
					pt.setPtbb(ptbb);
					pt.setSbmsg(ptbb.getRemark());
				}
			}
		}
		return ptList;
	}

	public static ProcardTemplateBanBen getPtbbBypt(TotalDao totalDao,
			ProcardTemplate pt) {
		// TODO Auto-generated method stub
		Integer banci = pt.getBanci();
		if (banci == null) {
			banci = 0;
		}
		ProcardTemplateBanBen ptbb = null;
		if (banci > 1) {
			banci--;
			// ptbb = (ProcardTemplateBanBen) totalDao
			// .getObjectByCondition(
			// "from ProcardTemplateBanBen where banci=? and  procardTemplateBanBenApply.processStatus in('关联并通知生产') and markId=? order by id desc",
			// banci, pt.getMarkId());
			// if(ptbb==null){
			ptbb = (ProcardTemplateBanBen) totalDao
					.getObjectByCondition(
							"from ProcardTemplateBanBen where banci=? and  procardTemplateBanBenApply.processStatus in('关联并通知生产','生产后续','上传佐证','关闭') and markId=? order by id desc",
							banci, pt.getMarkId());
			// }
		} else if (banci == 1) {
			// ptbb = (ProcardTemplateBanBen) totalDao
			// .getObjectByCondition(
			// "from ProcardTemplateBanBen where (banci is null or banci=0) and  procardTemplateBanBenApply.processStatus in('关联并通知生产') and markId=? order by id desc",
			// pt.getMarkId());
			// if(ptbb==null){
			ptbb = (ProcardTemplateBanBen) totalDao
					.getObjectByCondition(
							"from ProcardTemplateBanBen where (banci is null or banci=0) and  procardTemplateBanBenApply.processStatus in('关联并通知生产','生产后续','上传佐证','关闭') and markId=? order by id desc",
							pt.getMarkId());
			// }
		}
		if (ptbb != null) {
			return ptbb;
		} else if (pt.getProcardTemplate() != null) {
			return getPtbbBypt(totalDao, pt.getProcardTemplate());
		}
		return null;
	}

	@Override
	public Object[] findProcardTem2(ProcardTemplate procardTemplate,
			int pageNo, int pageSize, String status) {
		// TODO Auto-generated method stub
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		if (status != null && status.equals("sz")) {
			procardTemplate.setProductStyle("试制");
		} else {
			procardTemplate.setProductStyle("批产");
		}
		String hql = totalDao.criteriaQueries(procardTemplate, null, "markId");
		if (procardTemplate.getMarkId() != null
				&& procardTemplate.getMarkId().length() > 0
				&& !procardTemplate.getMarkId().contains(" and ")
				&& !procardTemplate.getMarkId().contains(" or ")) {
			hql += " and (markId like '%" + procardTemplate.getMarkId()
					+ "%' or  ywMarkId like '%" + procardTemplate.getMarkId()
					+ "%')";
		}
		hql += " and (banbenStatus is null or banbenStatus!='历史' ) and (dataStatus is null or dataStatus!='删除')";
		List<ProcardTemplate> list = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		for (int i = 0; i < list.size(); i++) {
			ProcardTemplate pt = (ProcardTemplate) list.get(i);
			ProcardTemplate totalCard = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where id=? and (dataStatus is null or dataStatus!='删除')",
							pt.getRootId());
			if (totalCard != null) {
				pt.setRootMarkId(totalCard.getMarkId());
				pt.setYwMarkId(totalCard.getYwMarkId());
			}
		}
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public Object[] findProcardTem3(ProcardTemplate procardTemplate,
			int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		if (procardTemplate == null) {
			procardTemplate = new ProcardTemplate();
		}
		procardTemplate.setBzStatus("已批准");
		String hql = totalDao.criteriaQueries(procardTemplate, null);
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public List getGyPeople(String pageStatus, String typename) {
		// TODO Auto-generated method stub

		String hql = "from AssessPersonnel a where  a.usersGroup.id in (select id from UsersGroup where status=? and groupName =?)";
		String groupName = null;
		if (typename.equals("bz")) {
			groupName = "编制";
		} else if (typename.equals("jd")) {
			groupName = "校对";
		} else if (typename.equals("sh")) {
			groupName = "审核";
		} else if (typename.equals("pz")) {
			groupName = "批准";
		} else {
			groupName = typename;
		}
		return totalDao.query(hql, pageStatus, groupName);
	}

	@Override
	public ProcardTemplate getProcardTemplateById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardTemplate) totalDao.getObjectById(ProcardTemplate.class,
				id);
	}

	public Procard getProcardById(Integer id) {
		// TODO Auto-generated method stub
		return (Procard) totalDao.getObjectById(Procard.class, id);
	}

	@Override
	public String batchApprovalBz(int[] checkboxs, String tag) {
		if (checkboxs != null && checkboxs.length > 0) {
			String msg = "";
			List<Integer> list = new ArrayList<Integer>();
			ProcardTemplate rootcard = null;
			for (int id : checkboxs) {
				if (rootcard == null) {
					rootcard = (ProcardTemplate) totalDao
							.getObjectByCondition(
									" from ProcardTemplate where id=(select rootId from ProcardTemplate where id=?)",
									id);
				}
				if (tag.equals("agree")) {
					String msg2 = submitProcard(id, 1);
					totalDao.clear();
					// if (!msg2.equals("true")) {
					// msg += msg2;
					// }
					try {
						Integer alertid = Integer.parseInt(msg2);
						if (!list.contains(alertid)) {
							list.add(alertid);
						}
						msg += msg2;
					} catch (Exception e) {
						// TODO: handle exception
						msg += e.getMessage();
					}
				} else if (tag.equals("back")) {
					String msg2 = backProcard(id);
					totalDao.clear();
					if (!msg2.equals("true")) {
						msg += msg2;
					}
				}
			}
			if (rootcard != null) {
				if (tag.equals("agree")) {
					Integer[] alertIds = new Integer[list.size()];
					for (int len = 0; len < list.size(); len++) {
						alertIds[len] = list.get(len);
					}
					AlertMessagesServerImpl.addAlertMessages("BOM编制提醒", "总成："
							+ rootcard.getMarkId() + "下有您需要编制或审批的零件请前往处理",
							alertIds,
							"procardTemplateGyAction_findSonsForBz.action?id="
									+ rootcard.getRootId(), true);
				}
			}
			if (msg.length() == 0) {
				return "true";

			}
			return msg;
		} else {
			return "请至少选择一项提交审批!";
		}
	}

	@Override
	public String submitProcard(Integer id, Integer type) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = getProcardTemplateById(id);
		Users user = Util.getLoginUser();
		String datetime = Util.getDateTime();
		String alertIdsb = null;
		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM编制节点数'";
		String hql2 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='成型图限制件号'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode == null) {
			valueCode = "4";
		}
		String cxtxzjhs = (String) totalDao.getObjectByCondition(hql2);
		List<String> cxtxzjhList = new ArrayList<String>();
		if (cxtxzjhs != null) {
			String[] cs = cxtxzjhs.split(";");
			if (cs != null && cs.length > 0) {
				for (String cxtxzjh : cs) {
					cxtxzjhList.add(cxtxzjh);
				}
			}
		}
		Map<Integer, ProcardTemplate> passRootProcardList = new HashMap<Integer, ProcardTemplate>();
		if (pt != null) {
			int msId = 0;
			String mess = "";
			// 初始数据，以提交人作为编制人
			if (pt.getBzStatus() == null || pt.getBzStatus().equals("初始")) {
				if (pt.getBianzhiId() == null) {
					pt.setBianzhiId(user.getId());
					pt.setBianzhiName(user.getName());
				}
				pt.setBzStatus("待编制");
			}
			String error = "";
			if (pt.getUnit() == null || pt.getUnit().equals("")) {
				error += pt.getMarkId() + "没有单位,提交失败!";
			}
			if (pt.getProName() == null || pt.getProName().equals("")) {
				error += pt.getMarkId() + "没有名称,提交失败!";
			}
			if (pt.getProcardStyle().equals("外购")) {
				if (pt.getWgType() == null || pt.getWgType().length() == 0) {
					YuanclAndWaigj yaw = (YuanclAndWaigj) totalDao
							.getObjectByCondition(
									" from YuanclAndWaigj where markId =? "
											+ "and (banbenStatus is null or banbenStatus <> '历史')",
									pt.getMarkId());
					if (yaw != null && yaw.getWgType() != null
							&& yaw.getWgType().length() > 0) {
						pt.setWgType(yaw.getWgType());
					} else {
						error += pt.getMarkId() + "外购件没有物料类别,提交失败!";
					}
				}
				if (pt.getQuanzi1() == null || pt.getQuanzi2() == null
						|| pt.getQuanzi1() == 0 || pt.getQuanzi2() == 0) {
					error += pt.getMarkId() + "用量异常(总成件号:" + pt.getRootMarkId()
							+ "),提交失败!";
				}
			} else {
				if (pt.getProcardStyle().equals("自制")
						&& (pt.getCorrCount() == null || pt.getCorrCount() == 0)) {
					error += pt.getMarkId() + "用量异常(总成件号:" + pt.getRootMarkId()
							+ "),提交失败!";
				}
			}
			if (error.length() > 0) {
				return error;
			}
			String tzmsg = "";
			boolean tzyz = true;// 图纸验证
			if (pt.getWgType() != null
					&& (pt.getWgType().equals("粉末") || pt.getWgType().equals(
							"标准件"))) {// 不需要验证图纸
				tzyz = false;
			}
			// if (pt.getWgType() != null
			// && (pt.getWgType().equals("胶袋类")
			// && !pt.getMarkId().startsWith("DKBA") && !pt
			// .getMarkId().startsWith("dkba"))) {// 不需要验证图纸
			// tzyz = false;
			// }
			// if (pt.getWgType() != null
			// && (pt.getWgType().equals("胶类(塑胶类)")
			// && !pt.getMarkId().startsWith("DKBA") && !pt
			// .getMarkId().startsWith("dkba"))) {// 不需要验证图纸
			// tzyz = false;
			// }
			if (pt.getTuhao() != null
					&& (pt.getTuhao().startsWith("DKBA0")
							|| pt.getTuhao().startsWith("dkba0")
							|| pt.getTuhao().startsWith("GB") || pt.getTuhao()
							.startsWith("gb"))) {// 华为技术规范类和国标件不需要验证图纸
				tzyz = false;
			}
			if (pt.getKgliao() != null
					&& (pt.getKgliao().equals("CS") || pt.getKgliao().equals(
							"cs"))) {// 客供料不需要图纸
				tzyz = false;
			}
			if (pt.getMarkId().startsWith("1.01")
					|| pt.getMarkId().startsWith("1.02")
					|| pt.getMarkId().startsWith("YT6.3")
					|| pt.getMarkId().startsWith("YT6.4")
					|| pt.getMarkId().startsWith("YT6.5")
					|| pt.getMarkId().startsWith("YT8.4")) {// 板材，胶条不需要图纸
				// YT6.3结构组装件
				// YT6.4电器组装件
				// YT6.5线材组装件
				// YT8.4电器子零件
				tzyz = false;
			}
			if (pt.getSpecification() != null
					&& (pt.getSpecification().startsWith("GB") || pt
							.getSpecification().startsWith("gb"))) {// 国标件不需要验证图纸
				tzyz = false;
			}
			Float tqCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2"
									+ " where (p1.markId=p2.markId or p1.markId=p2.ywMarkId) "
									+ "and p2.id=? and p2.productStyle='试制'",
							pt.getRootId());
			String bancisql = " and 1=1";
			if (pt.getBanci() == null || pt.getBanci() == 0) {
				bancisql = " and (banci is null or banci=0)";
			} else {
				bancisql = " and banci=" + pt.getBanci();
			}
			if (pt.getBzStatus() == null || pt.getBzStatus().equals("初始")) {
				if (pt.getBianzhiId() == null) {
					return "您未指派编制人,提交失败!";
				} else {
					Users bz = (Users) totalDao.getObjectById(Users.class, pt
							.getBianzhiId());
					if (bz.getOnWork().equals("离职")
							|| bz.getOnWork().equals("离职中")
							|| bz.getOnWork().equals("内退")
							|| bz.getOnWork().equals("退休")
							|| bz.getOnWork().equals("病休")) {
						return "编制人状态为:" + bz.getOnWork() + "不能被选中!";
					}
				}
				if (pt.getBianzhiDate() == null
						|| pt.getBianzhiDate().length() == 0) {
					return "您未选择编制时间,提交失败!";
				}
				pt.setBzStatus("待编制");
			} else if (pt.getBzStatus().equals("待编制")) {
				if (tzyz && (tqCount == null || tqCount == 0)) {
					// 图纸校验
					Float cptzCount = null;
					if (pt.getProductStyle().equals("批产")) {
						cptzCount = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcessTemplateFile where markId=? and processNO is null and glId is null"
												+ bancisql, pt.getMarkId());
					} else {
						cptzCount = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcessTemplateFile where  processNO is null and glId = ? "
												+ bancisql, pt.getId());
					}
					if (cptzCount == null || cptzCount == 0) {
						tzmsg = "对不起," + pt.getMarkId() + "请先上传产品图纸\\n";
					}
					if (!pt.getProcardStyle().equals("外购")
							&& cxtxzjhList != null && cxtxzjhList.size() > 0) {
						boolean needProcess = false;
						for (String cs : cxtxzjhList) {
							if (pt.getMarkId().startsWith(cs)) {
								needProcess = true;
								break;
							}
						}
						if (needProcess) {
							Float cxtzCount = null;
							if (pt.getProductStyle().equals("批产")) {
								cxtzCount = (Float) totalDao
										.getObjectByCondition(
												"select count(*) from ProcessTemplateFile where markId=? and type='成型图' and processNO is null and glId is null"
														+ bancisql, pt
														.getMarkId());
							} else {
								cxtzCount = (Float) totalDao
										.getObjectByCondition(
												"select count(*) from ProcessTemplateFile where  processNO is null  and type='成型图'  and glId = ? "
														+ bancisql, pt.getId());
							}
							if (cxtzCount == null || cxtzCount == 0) {
								tzmsg = "对不起," + pt.getMarkId() + "请先上传成型图\\n";
							}
						}
					}
				}
				Set<ProcessTemplate> processSet = pt.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					if (tzyz) {
						for (ProcessTemplate process : processSet) {
							if (process.getDataStatus() != null
									&& process.getDataStatus().equals("删除")) {
								continue;
							}
							if ((process.getGuifanstatus() == null || process
									.getGuifanstatus().equals("是"))
									&& (tqCount == null || tqCount == 0)) {
								Float tzCount = null;
								if (pt.getProductStyle().equals("批产")) {
									tzCount = (Float) totalDao
											.getObjectByCondition(
													"select count(*) from ProcessTemplateFile where markId=? and processNO=? and processName=?  and glId is null"
															+ bancisql, pt
															.getMarkId(),
													process.getProcessNO(),
													process.getProcessName());
								} else {
									tzCount = (Float) totalDao
											.getObjectByCondition(
													"select count(*) from ProcessTemplateFile where   processNO=? and glId = ?"
															+ bancisql, process
															.getProcessNO(),
													process.getId());
								}
								if (tzCount == null || tzCount == 0) {
									tzmsg += "对不起," + pt.getMarkId() + "的第"
											+ process.getProcessNO()
											+ "工序需要上传图纸!\\n";
								}
							}
						}
					}
				} else {
					if ((tqCount == null || tqCount == 0)
							&& (!pt.getProcardStyle().equals("外购") || (pt
									.getProcardStyle().equals("外购")
									&& pt.getNeedProcess() != null && pt
									.getNeedProcess().equals("yes")))) {
						return pt.getMarkId() + "零件下需要有工序,请先添加工序,提交失败!";
					}
				}
				if ((tqCount == null || tqCount == 0)
						&& !pt.getProcardStyle().equals("外购")) {// 非特权BOM检查是否含有未绑工序定的外购件
					List<String> markIdlist = totalDao
							.query(
									"select markId from ProcardTemplate where procardStyle ='外购' and procardTemplate.id=? and markId not in( select wgprocardMardkId from ProcessAndWgProcardTem where procardMarkId=? and "
											+ "  processName in(select processName from ProcessTemplate where procardTemplate.id=?)) and (dataStatus is null or dataStatus <> '删除') and (banbenStatus is null or banbenStatus <> '历史')",
									pt.getId(), pt.getMarkId(), pt.getId());
					if (markIdlist != null && markIdlist.size() > 0) {
						StringBuffer wbdsb = new StringBuffer();
						wbdsb.append(pt.getMarkId() + "下外购件:");
						for (int wbd = 0; wbd < markIdlist.size(); wbd++) {
							String wbdmarkId = markIdlist.get(wbd);
							if (wbd == 0) {
								wbdsb.append(wbdmarkId);
							} else {
								wbdsb.append("," + wbdmarkId);
							}
						}
						wbdsb.append("未绑定工序!");
						return wbdsb.toString();
					}

				}
				if (pt.getProcardStyle() == null
						|| pt.getProcardStyle().length() == 0
						|| (!pt.getProcardStyle().equals("外购")
								&& !pt.getProcardStyle().equals("自制") && !pt
								.getProcardStyle().equals("总成"))) {
					return pt.getMarkId() + "零件类型不合规定,提交失败!";
				}
				if (pt.getProcardStyle().equals("自制")) {
					if (pt.getCorrCount() == null || pt.getCorrCount() == 0) {
						return "自制件" + pt.getMarkId() + "用量有误!";
					}
				}
				if (pt.getProcardStyle().equals("外购")) {
					if (pt.getQuanzi1() == null || pt.getQuanzi1() == 0
							|| pt.getQuanzi2() == null || pt.getQuanzi2() == 0) {
						return "外购件" + pt.getMarkId() + "用量有误!";
					}
				}
				if (pt.getProcardStyle().equals("自制")
						|| pt.getProcardStyle().equals("总成")) {
					if (pt.getProcardTSet() == null
							|| pt.getProcardTSet().size() == 0) {
						return pt.getMarkId() + "零件类型为" + pt.getProcardStyle()
								+ "必须要有下层零件";
					} else {
						Float llCount = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcardTemplate where procardTemplate.id=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus !='删除')"
												+ " and procardStyle='外购' and (needProcess is null or needProcess !='yes') and (lingliaostatus is null or lingliaostatus!='否')",
										pt.getId());
						if (llCount != null && llCount > 0) {
							pt.setLingliaostatus("是");
						} else {
							pt.setLingliaostatus("否");
						}
					}
				}
				ProcessTemplate sgx = (ProcessTemplate) totalDao
						.getObjectByCondition(
								"from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus !='删除')"
										+ " order by processNO ", pt.getId());
				if (sgx != null && sgx.getProductStyle().equals("外委")) {
					List<String> xcmarkIdList = totalDao
							.query(
									"select distinct(wgprocardMardkId) from ProcessAndWgProcardTem where procardMarkId=? and processName=? and "
											+ " wgprocardMardkId in(select markId from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除') "
											+ " and (dataStatus is null or dataStatus!='历史'))",
									pt.getMarkId(), sgx.getProcessName(), pt
											.getId());
					if (xcmarkIdList == null || xcmarkIdList.size() == 0) {
						tzmsg += "对不起," + pt.getMarkId() + "的首工序"
								+ sgx.getProcessName() + "为外委工序需要先关联对应的下层零件!";
					}
				}
				if (pt.getClType() != null && pt.getClType().length() > 0
						&& !pt.getProcardStyle().equals("外购")) {
					StringBuffer lkg = new StringBuffer();
					if (pt.getThisLength() == null || pt.getThisLength() == 0) {
						lkg.append("长");
					}
					if (pt.getThisWidth() == null || pt.getThisWidth() == 0) {
						lkg.append("宽");
					}
					if (pt.getThisHight() == null || pt.getThisHight() == 0) {
						lkg.append("高");
					}
					if (lkg.length() > 0) {
						tzmsg += "对不起," + pt.getMarkId() + "含有材质需要填写长宽高，您未填写:"
								+ lkg.toString();
					}
				}

				if (tzmsg.length() > 0) {
					return tzmsg;
				}
				if (pt.getBianzhiId() == null
						|| !pt.getBianzhiId().equals(user.getId())) {
					return "对不起您不是编制人,提交失败!\\n";
				}
				if (!valueCode.equals("2")) {
					if (pt.getJiaoduiId() == null) {
						return "您未指派校对人,提交失败!\\n";
					} else {
						Users bz = (Users) totalDao.getObjectById(Users.class,
								pt.getJiaoduiId());
						if (bz.getOnWork().equals("离职")
								|| bz.getOnWork().equals("离职中")
								|| bz.getOnWork().equals("内退")
								|| bz.getOnWork().equals("退休")
								|| bz.getOnWork().equals("病休")) {
							return "校对人状态为:" + bz.getOnWork() + "不能被选中!";
						}
					}
					mess = "件号" + pt.getMarkId() + "需要您前往校对,请及时处理,谢谢!";
					msId = pt.getJiaoduiId();
				} else {
					if (pt.getPizhunId() == null) {
						return "您未指派批准人,提交失败!\\n";
					} else {
						Users bz = (Users) totalDao.getObjectById(Users.class,
								pt.getPizhunId());
						if (bz.getOnWork().equals("离职")
								|| bz.getOnWork().equals("离职中")
								|| bz.getOnWork().equals("内退")
								|| bz.getOnWork().equals("退休")
								|| bz.getOnWork().equals("病休")) {
							return "批准人状态为:" + bz.getOnWork() + "不能被选中!";
						}
					}
					mess = "件号" + pt.getMarkId() + "需要您前往批准,请及时处理,谢谢!";
					msId = pt.getJiaoduiId();
				}
				// if (pt.getJiaoduiDate() == null
				// || pt.getJiaoduiDate().length() == 0) {
				// return "您未选择校对时间,提交失败!";
				// }
				pt.setBianzhiDate(datetime);// 编制时间
				pt.setBzStatus("已编制");
			} else if (pt.getBzStatus().equals("已编制")) {
				if (valueCode != null && valueCode.equals("2")) {
					if (pt.getPizhunId() == null
							|| !pt.getPizhunId().equals(user.getId())) {
						return "对不起,您不是批准人,提交失败!";
					}
				} else {
					if (pt.getJiaoduiId() == null
							|| !pt.getJiaoduiId().equals(user.getId())) {
						return "对不起,您不是校对人,提交失败!";
					}
				}
				if (valueCode.equals("3")) {
					if (pt.getPizhunId() == null) {
						return "您未指派批准人,提交失败!";
					} else {
						Users bz = (Users) totalDao.getObjectById(Users.class,
								pt.getPizhunId());
						if (bz.getOnWork().equals("离职")
								|| bz.getOnWork().equals("离职中")
								|| bz.getOnWork().equals("内退")
								|| bz.getOnWork().equals("退休")
								|| bz.getOnWork().equals("病休")) {
							return "批准人状态为:" + bz.getOnWork() + "不能被选中!";
						}
					}
				} else if (valueCode.equals("4")) {
					if (pt.getShenheId() == null) {
						return "您未指派审核人,提交失败!";
					} else {
						Users bz = (Users) totalDao.getObjectById(Users.class,
								pt.getShenheId());
						if (bz.getOnWork().equals("离职")
								|| bz.getOnWork().equals("离职中")
								|| bz.getOnWork().equals("内退")
								|| bz.getOnWork().equals("退休")
								|| bz.getOnWork().equals("病休")) {
							return "审核人状态为:" + bz.getOnWork() + "不能被选中!";
						}
					}
				}
				pt.setJiaoduiDate(datetime);// 校对时间
				if (valueCode != null && valueCode.equals("2")) {
					pt.setBzStatus("已批准");
					// msId = pt.getPizhunId();
				} else if (valueCode != null && valueCode.equals("3")) {
					pt.setBzStatus("已校对");
					msId = pt.getPizhunId();
					mess = "件号" + pt.getMarkId() + "需要您前往批准,请及时处理,谢谢!";
				} else {
					pt.setBzStatus("已校对");
					msId = pt.getShenheId();
					mess = "件号" + pt.getMarkId() + "需要您前往审核,请及时处理,谢谢!";
				}
			} else if (pt.getBzStatus().equals("已校对")) {
				if (valueCode != null && valueCode.equals("3")) {
					if (pt.getPizhunId() == null
							|| !pt.getPizhunId().equals(user.getId())) {
						return "对不起您不是批准人,提交失败!";
					}
				} else {
					if (pt.getShenheId() == null
							|| !pt.getShenheId().equals(user.getId())) {
						return "对不起您不是审核人,提交失败!";
					}
				}
				if (pt.getPizhunId() == null) {
					return "您未指派批准人,提交失败!";
				} else {
					Users bz = (Users) totalDao.getObjectById(Users.class, pt
							.getPizhunId());
					if (bz.getOnWork().equals("离职")
							|| bz.getOnWork().equals("离职中")
							|| bz.getOnWork().equals("内退")
							|| bz.getOnWork().equals("退休")
							|| bz.getOnWork().equals("病休")) {
						return "批准人状态为:" + bz.getOnWork() + "不能被选中!";
					}
				}
				pt.setShenheDate(datetime);// 审核时间
				if (valueCode != null && valueCode.equals("3")) {
					pt.setBzStatus("已批准");
				} else {
					pt.setBzStatus("已审核");
					msId = pt.getPizhunId();
					mess = "件号" + pt.getMarkId() + "请您审批,请及时处理,谢谢!";
				}
			} else if (pt.getBzStatus().equals("已审核")) {
				if (pt.getPizhunId() == null
						|| !pt.getPizhunId().equals(user.getId())) {
					return "对不起您不是批准人,提交失败!";
				}
				pt.setBzStatus("已批准");
				pt.setPizhunDate(datetime);// 批准时间
				// 通知编制人审批通过
				msId = pt.getBianzhiId();
				mess = "件号" + pt.getMarkId() + "已经审批通过,请注意查看!";
			}
			List<ProcardTemplate> sameMarkIdList = null;
			if (pt.getProductStyle().equals("批产")) {
				sameMarkIdList = totalDao
						.query(
								"from ProcardTemplate where markId=?   and productStyle='批产' and id !=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
								pt.getMarkId(), pt.getId());

			} else {
				String banbansql = null;
				if (pt.getBanBenNumber() == null
						|| pt.getBanBenNumber().length() == 0) {
					banbansql = " and (banBenNumber is null or banBenNumber='')";
				} else {
					banbansql = " and  banBenNumber='" + pt.getBanBenNumber()
							+ "'";
				}
				sameMarkIdList = totalDao
						.query(
								"from ProcardTemplate where markId=? and id !=? and productStyle='试制' and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
										+ banbansql, pt.getMarkId(), pt.getId());
			}
			if (pt.getBzStatus().equals("已批准")) {
				Float noPassCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardTemplate where rootId=? and (bzStatus is null or bzStatus!='已批准') and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
								pt.getRootId());
				if (noPassCount == null || noPassCount == 0) {
					ProcardTemplate rootPt = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, pt
									.getRootId());
					if (rootPt != null) {
						passRootProcardList.put(pt.getRootId(), rootPt);
					}
				}
			}
			if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
				for (ProcardTemplate sameCard : sameMarkIdList) {
					sameCard.setProName(pt.getProName());
					sameCard.setUnit(pt.getUnit());
					sameCard.setCarStyle(pt.getCarStyle());
					sameCard.setSafeCount(pt.getSafeCount());
					sameCard.setLastCount(pt.getLastCount());
					sameCard.setLingliaostatus(pt.getLingliaostatus());
					sameCard.setLingliaoType(pt.getLingliaoType());
					sameCard.setStatus(pt.getStatus());
					sameCard.setNumb(pt.getNumb());
					sameCard.setPageTotal(pt.getPageTotal());
					sameCard.setFachuDate(pt.getFachuDate());
					sameCard.setBianzhiName(pt.getBianzhiName());// 编制人
					sameCard.setBianzhiId(pt.getBianzhiId());// 编制ID
					sameCard.setBianzhiDate(pt.getBianzhiDate());// 编制时间
					sameCard.setJiaoduiName(pt.getJiaoduiName());// 校对人
					sameCard.setJiaoduiId(pt.getJiaoduiId());// 校对ID
					sameCard.setJiaoduiDate(pt.getJiaoduiDate());// 校对时间
					sameCard.setShenheName(pt.getShenheName());// 审核人
					sameCard.setShenheId(pt.getShenheId());// 审核ID
					sameCard.setShenheDate(pt.getShenheDate());// 审核时间
					sameCard.setPizhunName(pt.getPizhunName());// 批准人
					sameCard.setPizhunId(pt.getPizhunId());// 批准ID
					sameCard.setPizhunDate(pt.getPizhunDate());// 批准时间
					sameCard.setBzStatus(pt.getBzStatus());
					if (sameCard.getProcardStyle().equals("自制")
							|| sameCard.getProcardStyle().equals("总成")) {
						if (sameCard.getProcardTSet() == null
								|| sameCard.getProcardTSet().size() == 0) {
							throw new RuntimeException(sameCard.getMarkId()
									+ "零件类型为" + sameCard.getProcardStyle()
									+ "必须要有下层零件");
						} else {
							Float llCount = (Float) totalDao
									.getObjectByCondition(
											"select count(*) from ProcardTemplate where procardTemplate.id=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus !='删除')"
													+ " and procardStyle='外购件' and (needProcess is null or needProcess !='yes') and (lingliaostatus is null or lingliaostatus!='否')",
											sameCard.getId());
							if (llCount != null && llCount > 0) {
								sameCard.setLingliaostatus("是");
							} else {
								sameCard.setLingliaostatus("否");
							}
						}
						if (sameCard.getProcardStyle().equals("自制")
								&& (sameCard.getCorrCount() == null || sameCard
										.getCorrCount() == 0)) {
							throw new RuntimeException(sameCard.getMarkId()
									+ "用量异常(总成件号:" + sameCard.getRootMarkId()
									+ "),提交失败!");
						}
					} else {
						if (sameCard.getQuanzi1() == null
								|| sameCard.getQuanzi2() == null
								|| sameCard.getQuanzi1() == 0
								|| sameCard.getQuanzi2() == 0) {
							throw new RuntimeException(sameCard.getMarkId()
									+ "用量异常(总成件号:" + sameCard.getRootMarkId()
									+ "),提交失败!");
						}
					}
					totalDao.update(sameCard);
					if (sameCard.getBzStatus().equals("已批准")) {
						ProcardTemplate rootPt = passRootProcardList
								.get(sameCard.getRootId());
						if (rootPt == null) {
							Float noPassCount = (Float) totalDao
									.getObjectByCondition(
											"select count(*) from ProcardTemplate where rootId=? and (bzStatus is null or bzStatus!='已批准') and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
											sameCard.getRootId());
							if (noPassCount == null || noPassCount == 0) {
								rootPt = (ProcardTemplate) totalDao
										.getObjectById(ProcardTemplate.class,
												sameCard.getRootId());
								if (rootPt != null) {
									passRootProcardList.put(sameCard
											.getRootId(), rootPt);
								}
							}
						}
					}
				}
			}
			boolean bool = totalDao.update(pt);
			if (bool) {
				if (type == 0) {
					AlertMessagesServerImpl.addAlertMessages("BOM编制提醒", mess,
							new Integer[] { msId },
							"procardTemplateGyAction_findSonsForBz.action?id="
									+ pt.getRootId(), true);
				}

				if (pt.getBzStatus().equals("已批准")) {
					findGongxu(pt);
					// 将图纸加盖印章
					// String icon_fileRealPath = ServletActionContext
					// .getServletContext().getRealPath(
					// "/upload/file/yz/icon_ytwrq.png");
					//
					// // 查询 ProcardTemplate的图纸,得到文件的实际路径，并判断是否存在
					// String addSql2 = null;
					// if (pt.getProductStyle().equals("批产")) {
					// addSql2 =
					// " and (productStyle is null or productStyle !='试制') ";
					// } else {
					// addSql2 = " and productStyle ='试制' ";
					// }
					// List<ProcessTemplateFile> procardFileList = totalDao
					// .query(
					// "from ProcessTemplateFile where processNO is null  and status='默认' and markId=?"
					// + addSql2 + " order by processNO",
					// pt.getMarkId());
					// String realPath =
					// ServletActionContext.getServletContext()
					// .getRealPath("/upload/file/processTz");
					// for (ProcessTemplateFile procardFile : procardFileList) {
					// String path = realPath + "/" + procardFile.getMonth()
					// + "/" + procardFile.getFileName();
					// File file = new File(path);
					// if (file.exists()) {
					// Util.markImageByIcon(icon_fileRealPath, path, path);
					// }
					// }
					// List<ProcessTemplateFile> processFileList = totalDao
					// .query(
					// "from ProcessTemplateFile where processNO in (select processNO from ProcessTemplate where procardTemplate.id=?) "
					// + addSql2
					// + " and status='默认' and markId=? order by processNO",
					// id, pt.getMarkId());
					// // 查询 ProcessTemplate的图纸,得到文件的实际路径，并判断是否存在
					// for (ProcessTemplateFile processFile : processFileList) {
					// String path = realPath + "/" + processFile.getMonth()
					// + "/" + processFile.getFileName();
					// File file = new File(path);
					// if (file.exists()) {
					// Util.markImageByIcon(icon_fileRealPath, path, path);
					// }
					// }
					// 查询图纸并替换加章图纸
					String addSql = "";
					if (pt.getBanci() == null || pt.getBanci() == 0) {
						addSql += " and (banci is null or banci =0)";
					} else {
						addSql += " and banci is not null and banci="
								+ pt.getBanci();
					}
					List<ProcessTemplateFile> processFileList = totalDao
							.query(
									"from ProcessTemplateFile where markId=? and fileName2 is not null and fileName2!='' "
											+ addSql, pt.getMarkId());
					if (processFileList != null && processFileList.size() > 0) {
						for (ProcessTemplateFile processFile : processFileList) {
							if (processFile.getFileName2().startsWith("jz")) {
								String fileName1 = processFile.getFileName();
								processFile.setFileName(processFile
										.getFileName2());
								processFile.setFileName2(fileName1);
								totalDao.update(processFile);
							}
						}
					}
					if (pt.getProcardStyle().equals("外购")) {
						String sql = "";
						if (pt.getBanBenNumber() == null
								|| pt.getBanBenNumber().length() == 0) {
							sql = " and (banbenhao is null or banbenhao ='')";
						} else {
							sql = " and banbenhao='" + pt.getBanBenNumber()
									+ "'";
						}
						List<YuanclAndWaigj> wgjList = totalDao
								.query(
										"from YuanclAndWaigj where markId=? and (status is null or status='' or status='使用')"
												+ " and (banbenStatus is null or banbenStatus !='历史' )"
												+ sql, pt.getMarkId());
						if (wgjList != null && wgjList.size() > 0) {
							for (YuanclAndWaigj wgj : wgjList) {
								wgj.setStatus("已确认");
								totalDao.update(wgj);
							}
						}
					}
					Set<Integer> keySet = passRootProcardList.keySet();
					if (keySet != null && keySet.size() > 0) {
						for (Integer rootId : keySet) {
							ProcardTemplate rootPt = passRootProcardList
									.get(rootId);
							// 查询是否有未申请审批的订单
							List<Integer> tzIds = totalDao
									.query(
											"select distinct documentaryPeopleId from OrderManager where id in"
													+ "(select orderManager.id from ProductManager where status='计划完善' and (pieceNumber=? or pieceNumber=? or ywMarkId=? or ywMarkId=?))",
											rootPt.getMarkId(), rootPt
													.getYwMarkId() == null ? ""
													: rootPt.getYwMarkId(),
											rootPt.getMarkId(), rootPt
													.getYwMarkId() == null ? ""
													: rootPt.getYwMarkId());
							if (tzIds != null && tzIds.size() > 0) {
								Integer[] alertIds = new Integer[tzIds.size()];
								for (int len = 0; len < tzIds.size(); len++) {
									alertIds[len] = tzIds.get(len);
								}
								AlertMessagesServerImpl
										.addAlertMessages(
												"订单提交审批提醒",
												"总成为:"
														+ rootPt.getMarkId()
														+ "("
														+ rootPt.getYwMarkId()
														+ ")的BOM已编制完成,您有其未提交审批的订单可前往处理!",
												alertIds,
												"orderManager_queryOrderManagerByCondition.action?flag=dj",
												true, "yes");
							}
						}
					}
				}
				// 将编制、校对、审核、批准人员和时间覆盖在图纸上
				if (type == 1) {
					return msId + "";
				}
				return bool + "";
			}
		}
		return "没有找到目标零件,提交失败!";
	}

	public void findGongxu(ProcardTemplate procardTemplate) {
		String hql = "from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus <> '删除')";
		List<ProcessTemplate> processTemplateList = totalDao.query(hql,
				procardTemplate.getId());
		String processNO = "";// 工序号
		String processName = "";// 工序名称
		for (ProcessTemplate processTemplate : processTemplateList) {
			if ("外委".equals(processTemplate.getProductStyle())) {
				processNO += ";" + processTemplate.getProcessNO();
				processName += ";" + processTemplate.getProcessName();
			} else {
				processNO += "_";
				processName += "_";
			}
		}
		String[] processNOStr = processNO.split("_");
		String[] processNameStr = processName.split("_");
		for (int i = 0; i < processNOStr.length; i++) {
			if (processNOStr[i] != null && !"".equals(processNOStr[i])) {
				NoPriceprocess noPriceprocess = new NoPriceprocess();
				noPriceprocess.setMarkId(procardTemplate.getMarkId());
				noPriceprocess.setYwmarkId(procardTemplate.getYwMarkId());
				noPriceprocess.setRootMarkId(procardTemplate.getRootMarkId());
				noPriceprocess.setProcessName(processNameStr[i].substring(1,
						processNameStr[i].length()));
				noPriceprocess.setProcessNO(processNOStr[i].substring(1,
						processNOStr[i].length()));
				noPriceprocess.setProcessId(procardTemplate.getId());
				String hql_noPriceprocess = "from NoPriceprocess where markId =? and processName = ? and processNO=?";
				NoPriceprocess noPriceprocess_old = (NoPriceprocess) totalDao
						.getObjectByCondition(hql_noPriceprocess,
								noPriceprocess.getMarkId(), noPriceprocess
										.getProcessName(), noPriceprocess
										.getProcessNO());

				String hql_price = "from Price where partNumber=? and gongxunum=? and processNames=? and wwType='工序外委'";
				Price price = (Price) totalDao.getObjectByCondition(hql_price,
						noPriceprocess.getMarkId(), noPriceprocess
								.getProcessName(), noPriceprocess
								.getProcessNO());
				if (price == null && noPriceprocess_old == null) {
					totalDao.save(noPriceprocess);
				}
			}
		}
	}

	@Override
	public String backProcard(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = getProcardTemplateById(id);
		Users user = Util.getLoginUser();
		String spjdString = getSpjdCount();
		Integer sojdCount = Integer.parseInt(spjdString);
		if (pt != null) {
			if (pt.getBzStatus().equals("初始") || pt.getBzStatus().equals("待编制")) {
				return "true";
			}
			if (pt.getBzStatus().equals("已编制")) {
				if (sojdCount == 2) {
					if (pt.getPizhunId() == null
							|| !pt.getPizhunId().equals(user.getId())) {
						return "对不起您不是" + pt.getMarkId() + "的批准人,打回失败!";
					}
				} else {
					if (pt.getJiaoduiId() == null
							|| !pt.getJiaoduiId().equals(user.getId())) {
						return "对不起您不是" + pt.getMarkId() + "的校对人,打回失败!";
					}
				}

			} else if (pt.getBzStatus().equals("已校对")) {
				if (sojdCount == 3) {
					if (pt.getPizhunId() == null
							|| !pt.getPizhunId().equals(user.getId())) {
						return "对不起您不是" + pt.getMarkId() + "的批准人,打回失败!";
					}
				} else {
					if (pt.getShenheId() == null
							|| !pt.getShenheId().equals(user.getId())) {
						return "对不起您不是" + pt.getMarkId() + "的审核人,打回失败!";
					}
				}
			} else if (pt.getBzStatus().equals("已审核")) {
				if (pt.getPizhunId() == null
						|| !pt.getPizhunId().equals(user.getId())) {
					return "对不起您不是" + pt.getMarkId() + "的批准人,打回失败!";
				}
			}
			pt.setBzStatus("待编制");
			String banbensql = null;
			if (pt.getBanBenNumber() == null
					|| pt.getBanBenNumber().length() == 0) {
				banbensql = " and (banBenNumber is null or banBenNumber ='')";
			} else {
				banbensql = " and banBenNumber='" + pt.getBanBenNumber() + "'";
			}

			List<ProcardTemplate> sameMarkIdList = totalDao
					.query(
							"from ProcardTemplate where markId=? and id !=? and productStyle=? "
									+ " and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') "
									+ banbensql, pt.getMarkId(), pt.getId(), pt
									.getProductStyle());
			if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
				for (ProcardTemplate sameCard : sameMarkIdList) {
					sameCard.setProName(pt.getProName());
					sameCard.setUnit(pt.getUnit());
					sameCard.setCarStyle(pt.getCarStyle());
					sameCard.setSafeCount(pt.getSafeCount());
					sameCard.setLastCount(pt.getLastCount());
					sameCard.setLingliaostatus(pt.getLingliaostatus());
					sameCard.setLingliaoType(pt.getLingliaoType());
					sameCard.setStatus(pt.getStatus());
					sameCard.setNumb(pt.getNumb());
					sameCard.setPageTotal(pt.getPageTotal());
					sameCard.setFachuDate(pt.getFachuDate());
					sameCard.setBianzhiName(pt.getBianzhiName());// 编制人
					sameCard.setBianzhiId(pt.getBianzhiId());// 编制ID
					sameCard.setBianzhiDate(pt.getBianzhiDate());// 编制时间
					sameCard.setJiaoduiName(pt.getJiaoduiName());// 校对人
					sameCard.setJiaoduiId(pt.getJiaoduiId());// 校对ID
					sameCard.setJiaoduiDate(pt.getJiaoduiDate());// 校对时间
					sameCard.setShenheName(pt.getShenheName());// 审核人
					sameCard.setShenheId(pt.getShenheId());// 审核ID
					sameCard.setShenheDate(pt.getShenheDate());// 审核时间
					sameCard.setPizhunName(pt.getPizhunName());// 批准人
					sameCard.setPizhunId(pt.getPizhunId());// 批准ID
					sameCard.setPizhunDate(pt.getPizhunDate());// 批准时间
					sameCard.setBzStatus(pt.getBzStatus());
					totalDao.update(sameCard);
				}
			}
			return totalDao.update(pt) + "";
		}
		return "没有找到目标零件,打回失败!";
	}

	@Override
	public Integer findMaxbelongLayer(Integer rootId) {
		// TODO Auto-generated method stub
		if (rootId != null && rootId > 0) {
			String hql = "select belongLayer from ProcardTemplate where rootId=? and procardstyle in ('总成','自制','外购') and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by belongLayer desc";
			Object obj = totalDao.getObjectByCondition(hql, rootId);
			return Integer.parseInt(obj.toString());
		}
		return null;
	}

	@Override
	public List<ProcardTemplate> findProByBel(Integer fatherId,
			Integer maxBelongLayer) {
		// TODO Auto-generated method stub
		if (fatherId != null && fatherId > 0) {
			String hql = "from ProcardTemplate where fatherId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')";
			if (maxBelongLayer != null && maxBelongLayer == 1) {
				hql = "from ProcardTemplate where id=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')";

			}
			return totalDao.query(hql, fatherId);
		}
		return null;
	}

	@Override
	public Object[] findProcardByRunCard(Integer procarId) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = getProcardTemplateById(procarId);
		if (pt != null) {
			List sonList = new ArrayList();
			Set<ProcardTemplate> sonSet = pt.getProcardTSet();
			if (sonSet != null && sonSet.size() > 0) {
				sonList.addAll(sonSet);
			}
			List processList = new ArrayList();
			Set<ProcessTemplate> processSet = pt.getProcessTemplate();
			if (processSet != null && processSet.size() > 0) {
				processList.addAll(processSet);
			}
			return new Object[] { pt, sonList, processList };
		}
		return null;
	}

	@Override
	public boolean copyTzXinxi() {
		// TODO Auto-generated method stub
		boolean b = true;
		List<GongyiGuichengAffix> gygfList = totalDao
				.query("from GongyiGuichengAffix");
		for (GongyiGuichengAffix gg : gygfList) {
			ProcessTemplateFile file = new ProcessTemplateFile();
			String markId = (String) totalDao.getObjectByCondition(
					"select jianNumb from GongyiGuicheng where id=?", gg
							.getGongyiGuichengId());
			if (gg.getProcessDataId() != null) {
				ProcessData pd = (ProcessData) totalDao.getObjectById(
						ProcessData.class, gg.getProcessDataId());
				if (pd != null) {
					file.setProcessNO(pd.getGongxuNo());
					file.setProcessName(pd.getGongxuName());
				}
			}
			file.setMarkId(markId);
			if (gg.getAffixType() != null && gg.getAffixType().equals("fujian")) {
				file.setType("附件");
			} else if (gg.getAffixType() != null
					&& gg.getAffixType().equals("shipin")) {
				file.setType("视频文件");
			} else {
				file.setType("工艺规范");
			}
			if (gg.getUrl() != null) {
				String[] paths = gg.getUrl().split("/");
				if (paths.length > 1) {
					file.setMonth(paths[paths.length - 2]);
					file.setFileName(paths[paths.length - 1]);
				}
			}
			file.setOldfileName(gg.getFileName());
			file.setStatus("默认");
			b = totalDao.save(file);
		}
		return b;
	}

	@Override
	public List findCardTemplateTz(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (pt != null) {
			String addSql1 = "";
			if (pt.getBanci() == null || pt.getBanci() == 0) {
				addSql1 = " and (banci is null or banci =0)";
			} else {
				addSql1 = " and banci =" + pt.getBanci();
			}
			String addSql2 = "";
			if (pt.getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and productStyle ='试制' and glId=" + pt.getId();
			}
			String canChange = "no";
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准图纸删除'";
			String sc = (String) totalDao.getObjectByCondition(hql1);
			if (sc != null && sc.equals("是")) {
				canChange = "yes";
			} else {
				if (pt.getBzStatus() == null || !pt.getBzStatus().equals("已批准")) {
					canChange = "yes";
				} else {
					Float tqCount = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2"
											+ " where (p1.markId=p2.markId or p1.markId=p2.ywMarkId)"
											+ " and p2.id=? and p2.productStyle='试制'",
									pt.getRootId());
					if (tqCount != null && tqCount > 0) {
						canChange = "yes";
					}
				}
			}
			List<ProcessTemplateFile> ptFileList = totalDao.query(
					"from ProcessTemplateFile where processNO is null and markId=? "
							+ addSql1 + addSql2 + " order by oldfileName", pt
							.getMarkId());
			if (ptFileList != null && ptFileList.size() > 0) {
				for (ProcessTemplateFile pf : ptFileList) {
					pf.setCanChange(canChange);
				}
			}
			return ptFileList;
		} else {
			return null;
		}
	}

	public Map<String, String> findPicbyMarkId(Integer id) {
		Map<String, String> tzwzMap = new HashMap<String, String>();
		if (id != null) {
			ProcardTemplate pt = (ProcardTemplate) totalDao.get(
					ProcardTemplate.class, id);
			if (pt != null) {
				List<ProcessTemplateFile> list = null;
				String banciSql = null;
				if (pt.getBanci() == null || pt.getBanci() == 0) {
					banciSql = " and (banci is null or banci=0)";
				} else {
					banciSql = " and banci=" + pt.getBanci();
				}
				if (pt.getProductStyle().equals("批产")) {
					list = totalDao.query(
							"from ProcessTemplateFile where markId=? and processNO is null "
									+ banciSql, pt.getMarkId());
				} else {
					list = totalDao.query(
							"from ProcessTemplateFile where markId=? and glId=? and processNO is null "
									+ banciSql, pt.getMarkId(), pt.getId());
				}
				if (list != null && list.size() > 0) {
					for (ProcessTemplateFile file : list) {
						String wz = file.getMonth() + "/" + file.getFileName();
						String name = file.getOldfileName();
						if (file.getProcessName() != null) {
							file.setOldfileName("(" + file.getProcessName()
									+ ")" + file.getOldfileName());
						}
						tzwzMap.put(wz, name);
					}
				}
				Set<ProcessTemplate> processSet = pt.getProcessTemplate();
				StringBuffer sb = new StringBuffer();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplate processT : processSet) {
						if (sb.length() == 0) {
							sb.append(processT.getProcessNO());
						} else {
							sb.append("," + processT.getProcessNO());
						}
					}
					List<ProcessTemplateFile> list1 = null;
					if (pt.getProductStyle().equals("批产")) {
						list1 = totalDao
								.query(
										"from ProcessTemplateFile where markId=? and processNO in("
												+ sb.toString()
												+ ") "
												+ banciSql
												+ "and productStyle='批产' and (status is null or status!='历史')",
										pt.getMarkId());
					} else {
						list1 = totalDao
								.query(
										"from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
												+ sb.toString()
												+ ") )"
												+ banciSql
												+ "and productStyle='试制' and (status is null or status!='历史')",
										pt.getMarkId(), pt.getId());
					}

					if (list1 != null && list1.size() > 0) {
						for (ProcessTemplateFile file : list1) {
							String wz = file.getMonth() + "/"
									+ file.getFileName();
							String name = file.getOldfileName();
							if (file.getProcessName() != null) {
								file.setOldfileName("(" + file.getProcessName()
										+ ")" + file.getOldfileName());
							}
							tzwzMap.put(wz, name);
						}
					}
					Set<ProcardTemplate> sonptSet = pt.getProcardTSet();
					if (sonptSet != null && sonptSet.size() > 0) {
						for (ProcardTemplate son : sonptSet) {
							List<ProcessTemplateFile> list2 = findSonTz(son);
							if (list2 != null && list2.size() > 0) {
								for (ProcessTemplateFile file : list2) {
									String wz = file.getMonth() + "/"
											+ file.getFileName();
									String name = file.getOldfileName();
									if (file.getProcessName() != null) {
										file.setOldfileName("("
												+ file.getProcessName() + ")"
												+ file.getOldfileName());
									}
									tzwzMap.put(wz, name);
								}
							}
						}
					}
				}
			}
		}
		return tzwzMap;
	}

	public Map<String, String> findPICforProduct(Procard procard) {
		Map<String, String> tzwzMap = new HashMap<String, String>();
		String addsql = " and ((procardStyle !='外购' and id in(select procard.id from ProcessInfor where procard.id is not null and processName!='组装' and (dataStatus is null or dataStatus !='删除')))"
				+ " or (procardStyle ='外购' and needProcess ='yes'))";
		String hql = "from  Procard  where rootId= ? " + addsql;
		List<Procard> pdList = totalDao.query(hql, procard.getId());
		List<ProcessTemplateFile> list1 = new ArrayList<ProcessTemplateFile>();
		StringBuffer sb = new StringBuffer();
		String zcMarkid = (String) totalDao.getObjectByCondition(
				"select markId from Procard where id=?", procard.getRootId());
		for (Procard pd : pdList) {
			if (pd.getMarkId().equals("DKBA8.016.1605")) {
				System.out.println(pd.getMarkId());
			}
			String banciSql = null;
			List<ProcessInfor> processList = totalDao
					.query(
							"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除')",
							pd.getId());
			if (pd.getBanci() == null || pd.getBanci() == 0) {

				banciSql = " and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + pd.getBanci();
			}
			Integer ptId = pd.getProcardTemplateId() == null ? 0 : pd
					.getProcardTemplateId();
			boolean backpt = false;
			List<ProcessTemplateFile> listptfile = null;
			if (ptId != null) {
				ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
						ProcardTemplate.class, ptId);
				if (pt != null) {
					backpt = true;
					if (pt.getProductStyle().equals("试制")) {
						listptfile = totalDao
								.query(
										"from ProcessTemplateFile where ((processNO is not null and"
												+ " glId in(select id from ProcessTemplate where   procardTemplate.id=?  ) )"
												+ " or (processNO is null and glId =? )) and  type not in('3D模型','3D文件','成型图')"
												+ banciSql
												+ " order by addTime desc",
										ptId, ptId);
					} else {
						listptfile = totalDao
								.query(
										"from ProcessTemplateFile where markId=? and  type not in('3D模型','3D文件','成型图')  "
												+ banciSql
												+ "and productStyle='批产' "
												+ " order by addTime desc", pd
												.getMarkId());
					}
				}
			}
			if (!backpt) {
				if (pd.getProductStyle().equals("批产")) {
					listptfile = totalDao
							.query(
									"from ProcessTemplateFile where markId=? and  type not in('3D模型','3D文件','成型图')  "
											+ banciSql
											+ "and productStyle='批产'  order by addTime desc",
									pd.getMarkId());
				} else {
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=? and"
											+ " (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and "
											+ "rootId in( select id from ProcardTemplate where procardStyle='总成' and markId=? and "
											+ "(banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') )"
											+ " order by addTime desc", procard
											.getMarkId(), zcMarkid);
					if (pt.getProductStyle().equals("批产")) {
						listptfile = totalDao
								.query(
										"from ProcessTemplateFile where markId=? and  type not in('3D模型','3D文件','成型图')  "
												+ banciSql
												+ "and productStyle='批产' "
												+ " order by addTime desc", pd
												.getMarkId());
					} else {
						listptfile = totalDao
								.query(
										"from ProcessTemplateFile where ((processNO is not null and"
												+ " glId in(select id from ProcessTemplate where   procardTemplate.id=?  ) )"
												+ " or (processNO is null and glId =? )) and  type not in('3D模型','3D文件','成型图')"
												+ banciSql
												+ " order by addTime desc",
										ptId, ptId);

					}

				}
			}
			List<ProcessTemplateFile> listptfile2 = new ArrayList<ProcessTemplateFile>();
			if (listptfile != null && listptfile.size() > 0) {
				for (ProcessTemplateFile file : listptfile) {
					if (file.getProcessNO() != null) {
						boolean hadgx = false;
						if (processList != null && processList.size() > 0) {
							for (ProcessInfor process : processList) {
								if (process.getProcessNO().equals(
										file.getProcessNO())
										&& process.getProcessName().equals(
												file.getProcessName())) {
									hadgx = true;
								}
							}
						}
						if (!hadgx) {
							continue;
						}
					}
					listptfile2.add(file);
				}
			}
			list1.addAll(listptfile2);
		}
		if (list1 != null && list1.size() > 0) {
			List<String> oldFilenameList = new ArrayList<String>();
			for (ProcessTemplateFile file : list1) {
				String ppfileName = null;
				ppfileName = file.getOldfileName().replaceAll(".PDF", ".jpeg");
				ppfileName = file.getOldfileName().replaceAll(".pdf", ".jpeg");
				ppfileName = file.getOldfileName().replaceAll(".jpg", ".jpeg");
				ppfileName = file.getOldfileName().replaceAll(".png", ".jpeg");
				if (oldFilenameList.contains(ppfileName)) {
					continue;
				} else {
					oldFilenameList.add(ppfileName);
				}
				String wz = file.getMonth() + "/" + file.getFileName();
				String name = file.getOldfileName();
				if (file.getProcessName() != null) {
					file.setOldfileName("(" + file.getProcessName() + ")"
							+ file.getOldfileName());
				}
				tzwzMap.put(wz, name);
			}
			return tzwzMap;
		} else {
			return null;
		}
	}

	private List<ProcessTemplateFile> findSonTz(ProcardTemplate procardTemplate) {
		// TODO Auto-generated method stub
		List<ProcessTemplateFile> list1 = null;
		String banciSql = null;
		if (procardTemplate.getBanci() == null
				|| procardTemplate.getBanci() == 0) {
			banciSql = " and (banci is null or banci=0)";
		} else {
			banciSql = " and banci=" + procardTemplate.getBanci();
		}
		if (procardTemplate.getProductStyle().equals("批产")) {
			list1 = totalDao
					.query(
							"from ProcessTemplateFile where markId=? and processNO is null "
									+ banciSql
									+ "and productStyle='批产' and (status is null or status!='历史')",
							procardTemplate.getMarkId());
		} else {
			list1 = totalDao
					.query(
							"from ProcessTemplateFile where markId=? and processNO is null and glId =? "
									+ banciSql
									+ "and productStyle='试制' and (status is null or status!='历史')",
							procardTemplate.getMarkId(), procardTemplate
									.getId());
		}
		List<ProcessTemplateFile> list2 = null;
		Set<ProcessTemplate> processSet = procardTemplate.getProcessTemplate();
		StringBuffer sb = new StringBuffer();
		if (processSet != null && processSet.size() > 0) {
			for (ProcessTemplate processT : processSet) {
				if (sb.length() == 0) {
					sb.append(processT.getProcessNO());
				} else {
					sb.append("," + processT.getProcessNO());
				}
			}

			if (procardTemplate.getProductStyle().equals("批产")) {
				list2 = totalDao
						.query(
								"from ProcessTemplateFile where markId=? and processNO in("
										+ sb.toString()
										+ ") "
										+ banciSql
										+ "and productStyle='批产' and (status is null or status!='历史')",
								procardTemplate.getMarkId());
				list1.addAll(list2);
			} else {
				list2 = totalDao
						.query(
								"from ProcessTemplateFile where markId=? and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
										+ sb.toString()
										+ ") )"
										+ banciSql
										+ "and productStyle='试制' and (status is null or status!='历史')",
								procardTemplate.getMarkId(), procardTemplate
										.getId());
				list1.addAll(list2);
			}

		}

		Set<ProcardTemplate> sonptSet = procardTemplate.getProcardTSet();
		if (sonptSet != null && sonptSet.size() > 0) {
			for (ProcardTemplate son : sonptSet) {
				List<ProcessTemplateFile> list3 = findSonTz(son);
				if (list3 != null && list3.size() > 0) {
					list1.addAll(list3);
				}
			}
		}
		return list1;
	}

	/**
	 * 
	 *总成件号查询所有图纸
	 */
	public Map<String, String> findAllByRootId(Integer id, String status) {
		if (id != null) {
			Map<String, String> tzwzMap = new HashMap<String, String>();
			String addsql = "";
			// String typesql = " and  type not in('3D模型','3D文件')";
			if ("waigou".equals(status)) {
				addsql = " and (procardStyle ='外购' and (needProcess is null or needProcess !='yes'))";
			} else if ("zizhi".equals(status)) {
				addsql = " and (procardStyle !='外购' or (procardStyle ='外购' and needProcess ='yes'))";
			} else if ("all".equals(status)) {
				// typesql = " and 1=1";
			}
			String hql = "from  ProcardTemplate  where rootId= ? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus !='删除')"
					+ addsql;
			List<ProcardTemplate> ptList = totalDao.query(hql, id);
			List<ProcessTemplateFile> list1 = new ArrayList<ProcessTemplateFile>();
			StringBuffer sb = new StringBuffer();
			for (ProcardTemplate pt : ptList) {
				Set<ProcessTemplate> processSet = pt.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplate processT : processSet) {
						if (sb.length() == 0) {
							sb.append(processT.getProcessNO());
						} else {
							sb.append("," + processT.getProcessNO());
						}
					}
				}
				String sbtoSting = sb.toString();
				if (sbtoSting.length() == 0) {
					sbtoSting = "-1";
				}
				String banciSql = null;
				if (pt.getBanci() == null || pt.getBanci() == 0) {
					banciSql = " and (banci is null or banci=0)";
				} else {
					banciSql = " and banci=" + pt.getBanci();
				}
				if (pt.getProductStyle().equals("批产")) {
					list1
							.addAll(totalDao
									.query(
											"from ProcessTemplateFile where markId=? "
													// + typesql
													+ " and (processNO is null or processNO in("
													+ sbtoSting + ")) "
													+ banciSql
													+ "and productStyle='批产' ",
											pt.getMarkId()));
				} else {
					list1
							.addAll(totalDao
									.query(
											"from ProcessTemplateFile where markId=? "
													// + typesql
													+ "  and processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id=? and processNO in("
													+ sbtoSting + ") )"
													+ banciSql
													+ "and productStyle='试制' ",
											pt.getMarkId(), pt.getId()));
					list1.addAll(totalDao.query(
							"from ProcessTemplateFile where markId=? "
									// + typesql
									+ "  and processNO is  null and glId =?"
									+ banciSql + "and productStyle='试制'", pt
									.getMarkId(), pt.getId()));

				}
			}
			if (list1 != null && list1.size() > 0) {
				for (ProcessTemplateFile file : list1) {
					String wz = file.getMonth() + "/" + file.getFileName();
					String name = file.getOldfileName();
					if (file.getProcessName() != null) {
						file.setOldfileName("(" + file.getProcessName() + ")"
								+ file.getOldfileName());
					}
					tzwzMap.put(wz, name);
				}
				return tzwzMap;
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public String saveProcessTemplateFile(
			ProcessTemplateFile processTemplateFile, Integer id,
			String ytRadio, String tag) {
		// TODO Auto-generated method stub
		processTemplateFile.setStatus("默认");
		ProcardTemplate procard = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);

		// //全尺寸测量章
		// if("总成".equals(procard.getProcardStyle())&&
		// "试制".equals(procard.getProductStyle())){
		// String realFilePath = "/upload/file/processTz/"
		// + Util.getDateTime("yyyy-MM");
		// String path = ServletActionContext.getServletContext().getRealPath(
		// realFilePath);
		// String icon_fileRealPath =
		// ServletActionContext.getServletContext().getRealPath("/upload/file/yz/icon_cl.png");
		// // 生成加章文件
		// Util.markPDFByIcon(icon_fileRealPath, path + "/"
		// + processTemplateFile.getFileName2(), path + "/" +
		// processTemplateFile.getFileName2(), 1.7f, 1.14f, 2.5f, 9f);
		// }
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (procard != null) {
			String bltzhql = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准零件补漏图纸'";
			String bltz = (String) totalDao.getObjectByCondition(bltzhql);
			if (bltz == null || !bltz.equals("是")) {
				Float tqCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardTemplatePrivilege p1,ProcardTemplate p2"
										+ " where (p1.markId=p2.markId or p1.markId=p2.ywMarkId) "
										+ "and p2.id=? and p2.productStyle='试制'",
								procard.getRootId());

				if ((tqCount == null || tqCount == 0)
						&& procard.getBzStatus() != null
						&& (procard.getBzStatus().equals("已批准") || procard
								.getBzStatus().equals("设变发起中"))) {
					return "此零件编制状态为" + procard.getBzStatus() + "不允许上传图纸!";
				}
			}
			if(procard.getBzStatus() != null
					&& (procard.getBzStatus().equals("设变发起中"))){
				return "此零件编制状态为" + procard.getBzStatus() + "不允许上传图纸!";
			}
			Integer banci = procard.getBanci();
			if (banci == null) {
				banci = 0;
			}
			if (procard.getBzStatus() != null
					&& procard.getBzStatus().equals("已批准")) {
				// 直接加章
				if (processTemplateFile.getFileName2() != null) {
					processTemplateFile.setFileName(processTemplateFile
							.getFileName2());
					processTemplateFile.setFileName2(processTemplateFile
							.getFileName());
				}
			}
			String banbenSql = null;
			if (procard.getBanBenNumber() == null
					|| procard.getBanBenNumber().equals("")) {
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			} else {
				banbenSql = " and banBenNumber='" + procard.getBanBenNumber()
						+ "'";
			}
			String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM图纸名称唯一'";
			String tzwy = (String) totalDao.getObjectByCondition(hql1);
			String fileName = processTemplateFile.getOldfileName();
			String type = processTemplateFile.getOldfileName().substring(
					fileName.lastIndexOf("."), fileName.length());
			if (tzwy == null || !tzwy.equals("否")) {// 删除原图纸
				// 覆盖之前存在的
				String sqlhadtz = "from ProcessTemplateFile where markId=? and productStyle=? and markId='"
						+ procard.getMarkId()
						+ "' and type='"
						+ processTemplateFile.getType() + "'";
				if (procard.getBanci() == null || procard.getBanci() == 0) {
					sqlhadtz += " and (banci is null or banci =0)";
				} else {
					sqlhadtz += " and banci =" + procard.getBanci();
				}
				if (type.equalsIgnoreCase(".jpeg")) {
					String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
					String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
					sqlhadtz += " and (oldfileName='" + fileName1
							+ "' or oldfileName ='" + fileName
							+ "' or oldfileName ='" + fileName2 + "')";
				} else if (type.equalsIgnoreCase(".jpg")) {
					String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
					String fileName2 = fileName.replaceAll(".jpg", ".PDF");
					sqlhadtz += " and (oldfileName='" + fileName1
							+ "' or oldfileName ='" + fileName
							+ "' or oldfileName ='" + fileName2 + "')";
				} else if (type.equalsIgnoreCase(".PDF")) {
					fileName = fileName.replaceAll(".pdf", ".PDF");
					String fileName1 = fileName.replaceAll(".PDF", ".jpg");
					String fileName2 = fileName.replaceAll(".PDF", ".jpeg");
					sqlhadtz += " and (oldfileName='" + fileName1
							+ "' or oldfileName ='" + fileName
							+ "' or oldfileName ='" + fileName2 + "')";
				} else {
					sqlhadtz += " and oldfileName='" + fileName + "'";
				}
				if (procard.getProductStyle().equals("试制")) {
					sqlhadtz += " and ((glId in(select id from ProcardTemplate where  markId='"
							+ procard.getMarkId()
							+ "'"
							+ banbenSql
							+ " and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')) "
							+ " and processNO is null) or (processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id in "
							+ "(select id from ProcardTemplate where markId='"
							+ procard.getMarkId()
							+ "' "
							+ banbenSql
							+ "and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')))))";
				} else {
					sqlhadtz += " and glId is null";
				}

				List<ProcessTemplateFile> hadFileList = totalDao.query(
						sqlhadtz, procard.getMarkId(), procard
								.getProductStyle());
				if (hadFileList != null && hadFileList.size() > 0) {
					if (procard.getBzStatus() != null
							&& procard.getBzStatus().equals("已批准")) {
						return "此零件编制状态为已批准不允许覆盖图纸!";
					}
					int logn = 0;
					for (ProcessTemplateFile hadfile : hadFileList) {
						if (logn == 0) {
							ProcardTemplateChangeLog.addchangeLog(totalDao,
									procard, hadfile, "图纸", "删除", user, Util
											.getDateTime());
						}
						logn++;
						totalDao.delete(hadfile);
					}
				}
			}
			// 获取所有同版本的试制领件
			List<ProcardTemplate> szptList = null;
			if (procard.getProductStyle().equals("试制")) {
				szptList = totalDao
						.query(
								" from ProcardTemplate where markId=? and productStyle='试制' and id!=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
										+ banbenSql, procard.getMarkId(),
								procard.getId());
			}
			String hql2 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='零件图纸自动下发工序'";
			String xftz = (String) totalDao.getObjectByCondition(hql2);

			// if (ytRadio != null && ytRadio.equals("yes")) {
			if (xftz != null && xftz.equals("是")
					&& !processTemplateFile.getType().equals("3D模型")) {
				Set<ProcessTemplate> processSet = procard.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplate process : processSet) {
						if (process.getDataStatus() != null
								&& process.getDataStatus().equals("删除")) {
							continue;
						}
						ProcessTemplateFile newfile = new ProcessTemplateFile();
						BeanUtils.copyProperties(processTemplateFile, newfile,
								new String[] {});
						newfile.setProductStyle(procard.getProductStyle());
						newfile.setProcessName(process.getProcessName());
						newfile.setProcessNO(process.getProcessNO());
						newfile.setMarkId(procard.getMarkId());
						newfile.setBanBenNumber(procard.getBanBenNumber());
						newfile.setBanci(banci);
						newfile.setUserName(user.getName());// 上传人姓名
						newfile.setUserCode(user.getCode());// 上传人工号
						newfile.setAddTime(nowtime);// 上传时间
						// if ("sop".equals(tag)) {
						// newfile.setGlId(process.getId());
						// }
						if (procard.getProductStyle().equals("试制")) {
							newfile.setGlId(process.getId());
							newfile.setProductStyle("试制");
						} else {
							newfile.setGlId(null);
							newfile.setProductStyle("批产");
						}
						totalDao.save(newfile);
					}
				}
				processTemplateFile.setProductStyle(procard.getProductStyle());
				processTemplateFile.setProcessName(procard.getProName());
				processTemplateFile.setProcessNO(null);
				processTemplateFile.setMarkId(procard.getMarkId());
				processTemplateFile.setBanci(banci);
				processTemplateFile.setUserName(user.getName());// 上传人姓名
				processTemplateFile.setUserCode(user.getCode());// 上传人工号
				processTemplateFile.setAddTime(nowtime);// 上传时间
				if (procard.getProductStyle().equals("试制")) {
					processTemplateFile.setGlId(procard.getId());
					processTemplateFile.setProductStyle("试制");
				} else {
					processTemplateFile.setGlId(null);
					processTemplateFile.setProductStyle("批产");
				}
				// if ("sop".equals(tag)) {
				// processTemplateFile.setGlId(procard.getId());
				// }
				ProcardTemplateChangeLog.addchangeLog(totalDao, procard,
						processTemplateFile, "图纸", "添加", user, nowtime);
				totalDao.save(processTemplateFile);
				// 同步同版本试制图纸
				if (szptList != null && szptList.size() > 0) {
					for (ProcardTemplate szpt : szptList) {
						if (xftz != null
								&& xftz.equals("是")
								&& !processTemplateFile.getType()
										.equals("3D模型")) {
							Set<ProcessTemplate> processSet2 = szpt
									.getProcessTemplate();
							if (processSet2 != null && processSet2.size() > 0) {
								for (ProcessTemplate szprocess : processSet2) {
									if (szprocess.getDataStatus() != null
											&& szprocess.getDataStatus()
													.equals("删除")) {
										continue;
									}
									ProcessTemplateFile newfile = new ProcessTemplateFile();
									BeanUtils.copyProperties(
											processTemplateFile, newfile,
											new String[] {});
									newfile.setProductStyle(szpt
											.getProductStyle());
									newfile.setProcessName(szprocess
											.getProcessName());
									newfile.setProcessNO(szprocess
											.getProcessNO());
									newfile.setMarkId(szpt.getMarkId());
									newfile.setBanBenNumber(szpt
											.getBanBenNumber());
									newfile.setBanci(szpt.getBanci());
									newfile.setUserName(user.getName());// 上传人姓名
									newfile.setUserCode(user.getCode());// 上传人工号
									newfile.setAddTime(nowtime);// 上传时间
									// if ("sop".equals(tag)) {
									// newfile.setGlId(process.getId());
									// }
									if (procard.getProductStyle().equals("试制")) {
										newfile.setGlId(szprocess.getId());
										newfile.setProductStyle("试制");
									}
									totalDao.save(newfile);
								}
							}
						}
						// 同步试制零件图纸
						ProcessTemplateFile newfile3 = new ProcessTemplateFile();
						BeanUtils.copyProperties(processTemplateFile, newfile3,
								new String[] {});
						newfile3.setProductStyle(szpt.getProductStyle());
						newfile3.setProcessName(szpt.getProName());
						newfile3.setProcessNO(null);
						newfile3.setMarkId(szpt.getMarkId());
						newfile3.setBanBenNumber(szpt.getBanBenNumber());
						newfile3.setBanci(szpt.getBanci());
						newfile3.setUserName(user.getName());// 上传人姓名
						newfile3.setUserCode(user.getCode());// 上传人工号
						newfile3.setAddTime(nowtime);// 上传时间
						newfile3.setGlId(szpt.getId());
						newfile3.setProductStyle("试制");
						// if ("sop".equals(tag)) {
						// processTemplateFile.setGlId(procard.getId());
						// }
						totalDao.save(newfile3);

					}
				}
				// if (procard.getProductStyle().equals("试制")) {
				// // 生成同版本批产图纸
				// ProcardTemplate pcpt = (ProcardTemplate) totalDao
				// .getObjectByCondition(
				// " from ProcardTemplate where markId=? and productStyle='试制' and id!=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
				// + banbenSql, procard.getMarkId(),
				// procard.getId());
				// if (pcpt != null) {
				// Set<ProcessTemplate> processSet2 = pcpt
				// .getProcessTemplate();
				// if (processSet2 != null && processSet2.size() > 0) {
				// for (ProcessTemplate pcprocess : processSet2) {
				// if (pcprocess.getDataStatus() != null
				// && pcprocess.getDataStatus().equals(
				// "删除")) {
				// continue;
				// }
				// ProcessTemplateFile newfile = new ProcessTemplateFile();
				// BeanUtils.copyProperties(processTemplateFile,
				// newfile, new String[] {});
				// newfile.setProductStyle(pcpt.getProductStyle());
				// newfile.setProcessName(pcprocess
				// .getProcessName());
				// newfile.setProcessNO(pcprocess.getProcessNO());
				// newfile.setMarkId(procard.getMarkId());
				// newfile.setBanci(pcpt.getBanci());
				// newfile.setUserName(user.getName());// 上传人姓名
				// newfile.setUserCode(user.getCode());// 上传人工号
				// newfile.setAddTime(nowtime);// 上传时间
				// newfile.setProductStyle("批产");
				// newfile.setGlId(null);
				// totalDao.save(newfile);
				// }
				// }
				// ProcessTemplateFile newfile2 = new ProcessTemplateFile();
				// BeanUtils.copyProperties(processTemplateFile, newfile2,
				// new String[] {});
				// newfile2.setProductStyle(pcpt.getProductStyle());
				// newfile2.setProcessName(pcpt.getProName());
				// newfile2.setProcessNO(null);
				// newfile2.setMarkId(pcpt.getMarkId());
				// newfile2.setBanci(pcpt.getBanci());
				// newfile2.setUserName(user.getName());// 上传人姓名
				// newfile2.setUserCode(user.getCode());// 上传人工号
				// newfile2.setAddTime(nowtime);// 上传时间
				// newfile2.setGlId(null);
				// newfile2.setProductStyle("批产");
				// // if ("sop".equals(tag)) {
				// // processTemplateFile.setGlId(procard.getId());
				// // }
				// totalDao.save(newfile2);
				// }
				// }

				return "true";
				// } else {
				// return "该零件下没有工序，不能上传原图!";
			} else {
				processTemplateFile.setProductStyle(procard.getProductStyle());
				processTemplateFile.setUserName(user.getName());// 上传人姓名
				processTemplateFile.setUserCode(user.getCode());// 上传人工号
				processTemplateFile.setAddTime(nowtime);// 上传时间
				processTemplateFile.setProcessName(procard.getProName());
				processTemplateFile.setProcessNO(null);
				processTemplateFile.setMarkId(procard.getMarkId());
				processTemplateFile.setBanci(banci);
				if (procard.getProductStyle().equals("试制")) {
					processTemplateFile.setGlId(procard.getId());
					processTemplateFile.setProductStyle("试制");
				} else {
					processTemplateFile.setGlId(null);
					processTemplateFile.setProductStyle("批产");
				}
				ProcardTemplateChangeLog.addchangeLog(totalDao, procard,
						processTemplateFile, "图纸", "添加", user, nowtime);
				totalDao.save(processTemplateFile);
				// 同步同版本试制图纸
				if (szptList != null && szptList.size() > 0) {
					for (ProcardTemplate szpt : szptList) {
						ProcessTemplateFile newfile2 = new ProcessTemplateFile();
						BeanUtils.copyProperties(processTemplateFile, newfile2,
								new String[] {});
						newfile2.setProductStyle(szpt.getProductStyle());
						newfile2.setProcessName(szpt.getProName());
						newfile2.setProcessNO(null);
						newfile2.setMarkId(szpt.getMarkId());
						newfile2.setBanci(szpt.getBanci());
						newfile2.setUserName(user.getName());// 上传人姓名
						newfile2.setUserCode(user.getCode());// 上传人工号
						newfile2.setAddTime(nowtime);// 上传时间
						newfile2.setGlId(szpt.getId());
						newfile2.setProductStyle("试制");
						// if ("sop".equals(tag)) {
						// processTemplateFile.setGlId(procard.getId());
						// }
						totalDao.save(newfile2);
					}
				}
				// if (procard.getProductStyle().equals("试制")) {
				// // 生成同版本批产图纸
				// ProcardTemplate pcpt = (ProcardTemplate) totalDao
				// .getObjectByCondition(
				// " from ProcardTemplate where markId=? and productStyle='试制' and id!=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')"
				// + banbenSql, procard.getMarkId(),
				// procard.getId());
				// if (pcpt != null) {
				// ProcessTemplateFile newfile2 = new ProcessTemplateFile();
				// BeanUtils.copyProperties(processTemplateFile, newfile2,
				// new String[] {});
				// newfile2.setProductStyle(pcpt.getProductStyle());
				// newfile2.setProcessName(pcpt.getProName());
				// newfile2.setProcessNO(null);
				// newfile2.setMarkId(pcpt.getMarkId());
				// newfile2.setBanci(pcpt.getBanci());
				// newfile2.setUserName(user.getName());// 上传人姓名
				// newfile2.setUserCode(user.getCode());// 上传人工号
				// newfile2.setAddTime(nowtime);// 上传时间
				// newfile2.setGlId(null);
				// newfile2.setProductStyle("批产");
				// totalDao.save(newfile2);
				// }
				// }
				return "true";
			}
		}
		return "没有找到目标零件!";
	}

	@Override
	public Map<Integer, Object> findYuanclAndWaigjsByCondition(
			YuanclAndWaigj yclAndWgj, int pageNo, int pageSize, String type) {

		String hql = null;
		hql = totalDao.criteriaQueries(yclAndWgj, null);
		if (type == null || !type.equals("wgj2")) {
			hql += " and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用')";
		} else {
			// 用于物料替换，替换的时候允许替换为历史版本，所以现在打开显示历史版本的外购件库的数据
		}
		hql += " and (status is null or status!='禁用')";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public List<ProcardTemplate> checkSelf(Integer id) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> list = totalDao
				.query(
						"from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus !='删除')",
						id);
		if (list != null && list.size() > 0) {
			List<ProcardTemplate> errList = new ArrayList<ProcardTemplate>();
			for (ProcardTemplate pt : list) {
				if (pt.getBanbenStatus() != null
						&& !pt.getBanbenStatus().equals("默认")) {
					continue;
				}
				if (pt.getProcardStyle() != null) {
					Integer count = 0;
					StringBuffer msg = new StringBuffer();
					if (pt.getProcardStyle().equals("总成")) {
						// 1.非单交件总成下必须有卡片
						if (pt.getDanjiaojian() == null
								|| !pt.getDanjiaojian().equals("单交件")) {
							Set<ProcardTemplate> sonCard = pt.getProcardTSet();
							if (sonCard == null || sonCard.size() == 0) {
								count++;
								msg = msg.append(count + ".非单交件总成下必须有零件！");
							}
						}
						// 2.总成下面必须要有工序
						Set<ProcessTemplate> processSet = pt
								.getProcessTemplate();
						if (processSet == null || processSet.size() == 0) {
							if (count > 0) {
								msg.append("\\n");
							}
							count++;
							msg.append(count + ".总成下面必须要有工序！");
						} else {
							// 3.工序节拍
							// 错误计数
							Integer processCount = 0;// 总计数
							Integer opCount = 0;// 操作节拍计数
							Integer sbCount = 0;// 设备节拍计数
							Integer zbjpCount = 0;// 准备节拍计数
							Integer zbCount = 0;// 准备次数计数
							Integer tzCount = 0;// 图纸计数
							// 错误工序
							StringBuffer opMsg = new StringBuffer();// 操作节拍错误工序
							StringBuffer sbMsg = new StringBuffer();// 设备节拍错误工序
							StringBuffer zbjpMsg = new StringBuffer();// 准备节拍错误工序
							StringBuffer zbMsg = new StringBuffer();// 准备次数错误工序
							StringBuffer tzMsg = new StringBuffer();// 图纸错误工序
							for (ProcessTemplate process : processSet) {
								if (process.getOpcaozuojiepai() == null) {// 操作节拍
									processCount++;
									opCount++;
									if (opCount > 1) {
										opMsg.append("、");
									}
									opMsg.append(process.getProcessNO());
								}
								if (process.getShebeiId() != null
										&& process.getOpshebeijiepai() == null) {// 设备节拍
									processCount++;
									sbCount++;
									if (sbCount > 1) {
										sbMsg.append("、");
									}
									sbMsg.append(process.getProcessNO());
								}
								if (process.getGzzhunbeijiepai() == null) {// 准备节拍
									processCount++;
									zbjpCount++;
									if (zbjpCount > 1) {
										zbjpMsg.append("、");
									}
									zbjpMsg.append(process.getProcessNO());
								}
								if (process.getOpcaozuojiepai() == null) {// 准备次数
									processCount++;
									zbCount++;
									if (zbCount > 1) {
										zbMsg.append("、");
									}
									zbMsg.append(process.getProcessNO());
								}
								if (process.getGuifanstatus() != null
										&& process.getGuifanstatus()
												.equals("是")) {
									// 需要工艺规范
									String addSql2 = "";
									if (pt.getProductStyle().equals("批产")) {
										addSql2 = " and productStyle='批产' ";
										if (pt.getBanci() == null
												|| pt.getBanci() == 0) {
											addSql2 += " and (banci is null or banci=0)";
										} else {
											addSql2 += "and banci ="
													+ pt.getBanci();
										}
									} else {
										addSql2 = " and productStyle ='试制' and glId="
												+ process.getId();
									}
									Float tzCount2 = (Float) totalDao
											.getObjectByCondition(
													"select count(*) from ProcessTemplateFile where markId=? and processNO=?"
															+ addSql2, pt
															.getMarkId(),
													process.getProcessNO());
									if (tzCount2 == null || tzCount2 == 0) {
										processCount++;
										tzCount++;
										if (tzCount > 1) {
											tzMsg.append("、");
										}
										tzMsg.append(process.getProcessNO());
									}
								}
							}
							if (processCount > 0) {
								count++;
								if (count > 0) {
									msg.append("\\n" + count + ".其下");
								}
								if (opCount > 0) {
									msg.append(opMsg.toString() + "号工序没有操作节拍!");
								}
								if (sbCount > 0) {
									msg.append(sbMsg.toString() + "号工序没有设备节拍!");
								}
								if (zbjpCount > 0) {
									msg.append(zbjpMsg.toString()
											+ "号工序没有准备节拍!");
								}
								if (zbCount > 0) {
									msg.append(zbMsg.toString() + "号工序没有准备次数!");
								}
								if (tzCount > 0) {
									msg.append(tzMsg.toString()
											+ "号工序需要规范验证,却没有图纸!");
								}
							}
						}
						// 4.总成必须要最大批次量
						if (pt.getMaxCount() == null
								|| pt.getMaxCount().equals(0)) {
							if (count > 0) {
								msg.append("\\n");
							}
							count++;
							msg.append(count + ".总成必须要最大批次量（正数）！");
						}
						if (count > 0) {
							pt.setCheckMsg(msg.toString());
							errList.add(pt);
						}
					} else if (pt.getProcardStyle().equals("自制")) {
						// 1.组合下必须有卡片
						Set<ProcardTemplate> sonCard = pt.getProcardTSet();
						if (sonCard == null || sonCard.size() == 0) {
							count++;
							msg.append(count + ".自制下必须有零件！");
						}
						// 2.组合下必须要有工序
						Set<ProcessTemplate> processSet = pt
								.getProcessTemplate();
						if (processSet == null || processSet.size() == 0) {
							if (count > 0) {
								msg.append("\\n");
							}
							count++;
							msg.append(count + ".自制下面必须要有工序！");
						} else {
							// 3.工序节拍
							// 错误计数
							Integer processCount = 0;// 总计数
							Integer opCount = 0;// 操作节拍计数
							Integer sbCount = 0;// 设备节拍计数
							Integer zbjpCount = 0;// 准备节拍计数
							Integer zbCount = 0;// 准备次数计数
							Integer tzCount = 0;// 图纸计数
							// 错误工序
							StringBuffer opMsg = new StringBuffer();// 操作节拍错误工序
							StringBuffer sbMsg = new StringBuffer();// 设备节拍错误工序
							StringBuffer zbjpMsg = new StringBuffer();// 准备节拍错误工序
							StringBuffer zbMsg = new StringBuffer();// 准备次数错误工序
							StringBuffer tzMsg = new StringBuffer();// 图纸错误工序
							for (ProcessTemplate process : processSet) {
								if (process.getOpcaozuojiepai() == null) {// 操作节拍
									processCount++;
									opCount++;
									if (opCount > 1) {
										opMsg.append("、");
									}
									opMsg.append(process.getProcessNO());
								}
								if (process.getShebeiId() != null
										&& process.getOpshebeijiepai() == null) {// 设备节拍
									processCount++;
									sbCount++;
									if (sbCount > 1) {
										sbMsg.append("、");
									}
									sbMsg.append(process.getProcessNO());
								}
								if (process.getGzzhunbeijiepai() == null) {// 准备节拍
									processCount++;
									zbjpCount++;
									if (zbjpCount > 1) {
										zbjpMsg.append("、");
									}
									zbjpMsg.append(process.getProcessNO());
								}
								if (process.getGzzhunbeicishu() == null) {// 准备次数
									processCount++;
									zbCount++;
									if (zbCount > 1) {
										zbMsg.append("、");
									}
									zbMsg.append(process.getProcessNO());
								}
								if (process.getGuifanstatus() != null
										&& process.getGuifanstatus()
												.equals("是")) {
									// 需要工艺规范
									String addSql2 = "";
									if (pt.getProductStyle().equals("批产")) {
										addSql2 = " and productStyle='批产' ";
										if (pt.getBanci() == null
												|| pt.getBanci() == 0) {
											addSql2 += " and (banci is null or banci=0)";
										} else {
											addSql2 += "and banci ="
													+ pt.getBanci();
										}
									} else {
										addSql2 = " and productStyle ='试制' and glId="
												+ process.getId();
									}
									Float tzCount2 = (Float) totalDao
											.getObjectByCondition(
													"select count(*) from ProcessTemplateFile where markId=? and processNO=?"
															+ addSql2, pt
															.getMarkId(),
													process.getProcessNO());
									if (tzCount2 == null || tzCount2 == 0) {
										processCount++;
										tzCount++;
										if (tzCount > 1) {
											tzMsg.append("、");
										}
										tzMsg.append(process.getProcessNO());
									}
								}
							}
							if (processCount > 0) {
								count++;
								if (count > 0) {
									msg.append("\\n" + count + ".其下");
								}
								if (opCount > 0) {
									msg.append(opMsg.toString() + "号工序没有操作节拍!");
								}
								if (sbCount > 0) {
									msg.append(sbMsg.toString() + "号工序没有设备节拍!");
								}
								if (zbjpCount > 0) {
									msg.append(zbjpMsg.toString()
											+ "号工序没有准备节拍!");
								}
								if (zbCount > 0) {
									msg.append(zbMsg.toString() + "号工序没有准备次数!");
								}
								if (tzCount > 0) {
									msg.append(tzMsg.toString()
											+ "号工序需要规范验证却没有图纸!");
								}
							}
							// 3.组合必须有对上层比例
							if (pt.getCorrCount() == null
									|| pt.getCorrCount() <= 0) {
								if (count > 0) {
									msg.append("\\n");
								}
								count++;
								msg.append(count + ".自制必须要有对上层比例（正数）！");
							}
							// // 4.领料的组合含有原材料时必须有原材料权值比例
							// if ((pt.getLingliaostatus() == null || !pt
							// .getLingliaostatus().equals("否"))
							// && pt.getTrademark() != null
							// && pt.getTrademark().length() > 0
							// && (pt.getQuanzi1() == null
							// || pt.getQuanzi1() <= 0
							// || pt.getQuanzi2() == null || pt
							// .getQuanzi2() <= 0)) {
							// if (count > 0) {
							// msg.append("\\n");
							// }
							// count++;
							// msg.append(count
							// + ".领料的自制含有原材料时必须有原材料权值比例(正数)！");
							// }
						}
						if (count > 0) {
							pt.setCheckMsg(msg.toString());
							errList.add(pt);
						}
						// } else if (pt.getProcardStyle().equals("自制")) {
						// // 1.自制件下必须要有工序
						// Set<ProcessTemplate> processSet = pt
						// .getProcessTemplate();
						// if (processSet == null || processSet.size() == 0) {
						// if (count > 0) {
						// msg.append("\\n");
						// }
						// count++;
						// msg.append(count + ".自制件下面必须要有工序！");
						// } else {
						// // 3.工序节拍
						// // 错误计数
						// Integer processCount = 0;// 总计数
						// Integer opCount = 0;// 操作节拍计数
						// Integer sbCount = 0;// 设备节拍计数
						// Integer zbjpCount = 0;// 准备节拍计数
						// Integer zbCount = 0;// 准备次数计数
						// // 错误工序
						// StringBuffer opMsg = new StringBuffer();// 操作节拍错误工序
						// StringBuffer sbMsg = new StringBuffer();// 设备节拍错误工序
						// StringBuffer zbjpMsg = new StringBuffer();// 准备节拍错误工序
						// StringBuffer zbMsg = new StringBuffer();// 准备次数错误工序
						//
						// for (ProcessTemplate process : processSet) {
						// if (process.getOpcaozuojiepai() == null) {// 操作节拍
						// processCount++;
						// opCount++;
						// if (opCount > 1) {
						// opMsg.append("、");
						// }
						// opMsg.append(process.getProcessNO());
						// }
						// if (process.getShebeiId() != null
						// && process.getOpshebeijiepai() == null) {// 设备节拍
						// processCount++;
						// sbCount++;
						// if (sbCount > 1) {
						// sbMsg.append("、");
						// }
						// sbMsg.append(process.getProcessNO());
						// }
						// if (process.getGzzhunbeijiepai() == null) {// 准备节拍
						// processCount++;
						// zbjpCount++;
						// if (zbjpCount > 1) {
						// zbjpMsg.append("、");
						// }
						// zbjpMsg.append(process.getProcessNO());
						// }
						// if (process.getOpcaozuojiepai() == null) {// 准备次数
						// processCount++;
						// zbCount++;
						// if (zbCount > 1) {
						// zbMsg.append("、");
						// }
						// zbMsg.append(process.getProcessNO());
						// }
						// }
						// if (processCount > 0) {
						// count++;
						// if (count > 0) {
						// msg.append("\\n" + count + ".其下");
						// }
						// if (opCount > 0) {
						// msg.append(opMsg.toString() + "号工序没有操作节拍!");
						// }
						// if (sbCount > 0) {
						// msg.append(sbMsg.toString() + "号工序没有设备节拍!");
						// }
						// if (zbjpCount > 0) {
						// msg.append(zbjpMsg.toString()
						// + "号工序没有准备节拍!");
						// }
						// if (zbCount > 0) {
						// msg.append(zbMsg.toString() + "号工序没有准备次数!");
						// }
						// }
						// // 3.组合必须有对上层比例
						// if (pt.getCorrCount() == null
						// || pt.getCorrCount() <= 0) {
						// if (count > 0) {
						// msg.append("\\n");
						// }
						// count++;
						// msg.append(count + ".组合必须要有对上层比例（正数）！");
						// }
						// // 4.领料的组合含有原材料时必须有原材料权值比例
						// if ((pt.getLingliaostatus() == null || !pt
						// .getLingliaostatus().equals("否"))
						// && pt.getTrademark() != null
						// && pt.getTrademark().length() > 0
						// && (pt.getQuanzi1() == null
						// || pt.getQuanzi1() <= 0
						// || pt.getQuanzi2() == null || pt
						// .getQuanzi2() <= 0)) {
						// if (count > 0) {
						// msg.append("\\n");
						// }
						// count++;
						// msg.append(count
						// + ".领料的组合含有原材料时必须有原材料权值比例(正数)！");
						// }
						// if (count > 0) {
						// pt.setCheckMsg(msg.toString());
						// errList.add(pt);
						// }
						// }
						// // 2.组合必须有对上层比例
						// if (pt.getCorrCount() == null || pt.getCorrCount() <=
						// 0) {
						// if (count > 0) {
						// msg.append("\\n");
						// }
						// count++;
						// msg.append(count + ".自制件必须要有对上层比例（正数）！");
						// }
						// // 3.领料的自制件必须要有原材料和原材料权值比例（正数）
						// if ((pt.getLingliaostatus() == null || !pt
						// .getLingliaostatus().equals("否"))
						// && (pt.getTrademark() == null
						// || pt.getTrademark().length() == 0
						// || pt.getQuanzi1() == null
						// || pt.getQuanzi1() <= 0
						// || pt.getQuanzi2() == null || pt
						// .getQuanzi2() <= 0)) {
						// if (count > 0) {
						// msg.append("\\n");
						// }
						// count++;
						// msg.append(count + ".领料的自制件必须要有原材料和原材料权值比例（正数）！");
						// }
						// if (count > 0) {
						// pt.setCheckMsg(msg.toString());
						// errList.add(pt);
						// }
					} else if (pt.getProcardStyle().equals("外购")) {
						// 1.外购件必须有对上次的比例
						if (pt.getQuanzi1() == null || pt.getQuanzi1() <= 0
								|| pt.getQuanzi2() == null
								|| pt.getQuanzi2() <= 0) {
							count++;
							msg.append(count + ".外购件必须有对上次的比例(正数)！");
						}
						if (pt.getNeedProcess() != null
								&& pt.getNeedProcess().equals("yes")) {// 半成品需要工序
							Set<ProcessTemplate> processSet = pt
									.getProcessTemplate();
							if (processSet == null || processSet.size() == 0) {
								if (count > 0) {
									msg.append("\\n");
								}
								count++;
								msg.append(count + ".半成品下面必须要有工序！");
							} else {
								// 3.工序节拍
								// 错误计数
								Integer processCount = 0;// 总计数
								Integer opCount = 0;// 操作节拍计数
								Integer sbCount = 0;// 设备节拍计数
								Integer zbjpCount = 0;// 准备节拍计数
								Integer zbCount = 0;// 准备次数计数
								Integer tzCount = 0;// 图纸计数
								// 错误工序
								StringBuffer opMsg = new StringBuffer();// 操作节拍错误工序
								StringBuffer sbMsg = new StringBuffer();// 设备节拍错误工序
								StringBuffer zbjpMsg = new StringBuffer();// 准备节拍错误工序
								StringBuffer zbMsg = new StringBuffer();// 准备次数错误工序
								StringBuffer tzMsg = new StringBuffer();// 图纸错误工序
								for (ProcessTemplate process : processSet) {
									if (process.getOpcaozuojiepai() == null) {// 操作节拍
										processCount++;
										opCount++;
										if (opCount > 1) {
											opMsg.append("、");
										}
										opMsg.append(process.getProcessNO());
									}
									if (process.getShebeiId() != null
											&& process.getOpshebeijiepai() == null) {// 设备节拍
										processCount++;
										sbCount++;
										if (sbCount > 1) {
											sbMsg.append("、");
										}
										sbMsg.append(process.getProcessNO());
									}
									if (process.getGzzhunbeijiepai() == null) {// 准备节拍
										processCount++;
										zbjpCount++;
										if (zbjpCount > 1) {
											zbjpMsg.append("、");
										}
										zbjpMsg.append(process.getProcessNO());
									}
									if (process.getGzzhunbeicishu() == null) {// 准备次数
										processCount++;
										zbCount++;
										if (zbCount > 1) {
											zbMsg.append("、");
										}
										zbMsg.append(process.getProcessNO());
									}
									if (process.getGuifanstatus() != null
											&& process.getGuifanstatus()
													.equals("是")) {
										// 需要工艺规范
										String addSql2 = "";
										if (pt.getProductStyle().equals("批产")) {
											addSql2 = " and productStyle='批产' ";
											if (pt.getBanci() == null
													|| pt.getBanci() == 0) {
												addSql2 += " and (banci is null or banci=0)";
											} else {
												addSql2 += "and banci ="
														+ pt.getBanci();
											}
										} else {
											addSql2 = " and productStyle ='试制' and glId="
													+ process.getId();
										}
										Float tzCount2 = (Float) totalDao
												.getObjectByCondition(
														"select count(*) from ProcessTemplateFile where markId=? and processNO=?"
																+ addSql2, pt
																.getMarkId(),
														process.getProcessNO());
										if (tzCount2 == null || tzCount2 == 0) {
											processCount++;
											tzCount++;
											if (tzCount > 1) {
												tzMsg.append("、");
											}
											tzMsg
													.append(process
															.getProcessNO());
										}
									}
								}
								if (processCount > 0) {
									count++;
									if (count > 0) {
										msg.append("\\n" + count + ".其下");
									}
									if (opCount > 0) {
										msg.append(opMsg.toString()
												+ "号工序没有操作节拍!");
									}
									if (sbCount > 0) {
										msg.append(sbMsg.toString()
												+ "号工序没有设备节拍!");
									}
									if (zbjpCount > 0) {
										msg.append(zbjpMsg.toString()
												+ "号工序没有准备节拍!");
									}
									if (zbCount > 0) {
										msg.append(zbMsg.toString()
												+ "号工序没有准备次数!");
									}
									if (tzCount > 0) {
										msg.append(tzMsg.toString()
												+ "号工序需要规范验证却没有图纸!");
									}
								}
							}
						} else {// 外购件产品图纸检验
							// 需要工艺规范
							String addSql2 = "";
							if (pt.getProductStyle().equals("批产")) {
								addSql2 = " and productStyle='批产' ";
								if (pt.getBanci() == null || pt.getBanci() == 0) {
									addSql2 += " and (banci is null or banci=0)";
								} else {
									addSql2 += "and banci =" + pt.getBanci();
								}
							} else {
								addSql2 = " and productStyle ='试制' and glId="
										+ pt.getId();
							}
							Float tzCount2 = (Float) totalDao
									.getObjectByCondition(
											"select count(*) from ProcessTemplateFile where markId=? and processNO is null"
													+ addSql2, pt.getMarkId());
							if (tzCount2 == null || tzCount2 == 0) {
								count++;
								msg.append("外购件没有产品图纸!");

							}
						}
						if (count > 0) {
							pt.setCheckMsg(msg.toString());
							errList.add(pt);
						}

					}
				} else {
					pt.setCheckMsg("没有零件类型");
					errList.add(pt);
				}
			}
			return errList;
		}
		return null;
	}

	@Override
	public List<ProcessTemplate> getProcessTemplateByProcardId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao
				.query(
						"from ProcessTemplate where procardTemplate.id=? order by processNO",
						id);
	}

	@Override
	public List findProcessGongyiGuifan(Integer id) {
		// TODO Auto-generated method stub
		ProcessTemplate process = (ProcessTemplate) totalDao.getObjectById(
				ProcessTemplate.class, id);
		if (process != null) {
			String addSql2 = "";
			if (process.getProcardTemplate().getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and productStyle ='试制' ";
			}
			List<ProcessTemplateFile> list = totalDao
					.query(
							" from ProcessTemplateFile  where processNO=(select processNO from ProcessTemplate where id=?) "
									+ "and processName=(select processName from ProcessTemplate where id=?) and markId=(select procardTemplate.markId from ProcessTemplate where id=?)"
									+ addSql2 + " and status='默认'", id, id, id);
			// List<String> list2 = new ArrayList<String>();
			// if (list != null && list.size() > 0) {
			// for (ProcessTemplateFile file : list) {
			// String url = gxtzPath + "/" + file.getMonth() + "/"
			// + file.getFileName();
			// list2.add(url);
			// }
			// }
			// if (list2.size() > 0) {
			// return list2;
			// }
			return list;
		} else {
			return null;
		}
	}

	@Override
	public List findProcardGongyiGuifan(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (pt != null) {
			String addSql2 = "";
			if (pt.getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and productStyle ='试制' ";
			}
			List<ProcessTemplateFile> list = totalDao.query(
					" from ProcessTemplateFile  where processNO is null  "
							+ "and markId=? and status='默认'" + addSql2, pt
							.getMarkId());
			List<String> list2 = new ArrayList<String>();
			if (list != null && list.size() > 0) {
				for (ProcessTemplateFile file : list) {
					String url = gxtzPath + "/" + file.getMonth() + "/"
							+ file.getFileName();
					list2.add(url);
				}
			}
			if (list2.size() > 0) {
				return list2;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String daoRuBomK3(File bomTree, String bomTreeFileName) {
		// TODO Auto-generated method stub
		// 上传到服务器
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + bomTreeFileName);
		try {
			FileCopyUtils.copy(bomTree, file2);

			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			jxl.Workbook rwb = null;
			try {
				rwb = Workbook.getWorkbook(is);// 创建workBook
			} catch (Exception e) {
				// TODO: handle exception
				return "文件无法识别!";
			}

			Sheet rs = rwb.getSheet(0);// 获得第一张Sheet表
			int excelcolumns = rs.getRows();// 获得总行数
			if (excelcolumns > 4) {
				// 获取材质列表
				String hql_wgType = "select name from Category where type = '物料' and id not in (select fatherId from Category where fatherId  is not NULL)";
				List<String> umList = totalDao.query(hql_wgType);
				StringBuffer sb = new StringBuffer();
				if (umList != null && umList.size() > 0) {
					for (String um : umList) {
						sb.append("," + um);
					}
					sb.append(",");
				}
				String umStr = sb.toString();
				Cell[] cellsAll = rs.getRow(1);
				String ywMarkId = cellsAll[5].getContents();// 对外件号
				ywMarkId = ywMarkId.replaceAll(" ", "");
				ywMarkId = ywMarkId.replaceAll("	", "");
				// ywMarkId = ywMarkId.replaceAll("（", "(");
				// ywMarkId = ywMarkId.replaceAll("）", ")");
				// ywMarkId = ywMarkId.replaceAll("x", "X");
				String createUser = cellsAll[6].getContents();// 创建人
				createUser = createUser.replaceAll(" ", "");
				createUser = createUser.replaceAll("	", "");
				String createTime = cellsAll[7].getContents();// 创建时间
				createTime = createTime.replaceAll(" ", "");
				createTime = createTime.replaceAll("	", "");
				String lastUser = null;// 最后修改人
				try {
					lastUser = cellsAll[8].getContents();// 最后修改人
					lastUser = lastUser.replaceAll(" ", "");
					lastUser = lastUser.replaceAll("	", "");
				} catch (Exception e) {
					// TODO: handle exception
				}
				List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
				boolean isbegin = false;
				int base = 2;
				// key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2：编号
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				String jymsg = "";
				Map<String, String> markIdAndProcardStyleMap = new HashMap<String, String>();
				for (int i = base; i < excelcolumns; i++) {
					ProcardTemplate pt = new ProcardTemplate();
					Cell[] cells = rs.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String bString = "";// 层数
					try {
						bString = cells[0].getContents();// 版本
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					bString = bString.replaceAll(" ", "");
					bString = bString.replaceAll("	", "");
					bString = bString.replaceAll("\\.", "");
					if (bString.length() == 0) {
						if (isbegin) {
							break;
						} else {
							base++;
							continue;
						}
					}
					if (bString.equals("层次")) {
						isbegin = true;
						base++;
						continue;
					}
					Integer belongLayer = null;
					try {
						belongLayer = Integer.parseInt(bString);
					} catch (Exception e) {
						// TODO: handle exception
						jymsg += "第" + (i + 1) + "行错误，层级标记有误!<br/>";
						continue;
					}

					// String zcwl = cells[1].getContents();// 子层物料代码
					// zcwl = zcwl.replaceAll(" ", "");
					// zcwl = zcwl.replaceAll("	", "");
					String proName = cells[1].getContents();// 名称
					proName = proName.replaceAll(" ", "");
					proName = proName.replaceAll("	", "");
					// proName = proName.replaceAll("（", "(");
					// proName = proName.replaceAll("）", ")");
					String type = cells[2].getContents();// 规格
					type = type.replaceAll(" ", "");
					type = type.replaceAll("	", "");
					type = type.replaceAll("（", "(");
					type = type.replaceAll("）", ")");
					type = type.replaceAll("x", "X");
					String procardStyle = cells[3].getContents();// 零件类型
					procardStyle = procardStyle.replaceAll(" ", "");
					procardStyle = procardStyle.replaceAll("	", "");
					String markId = cells[4].getContents();// 件号
					markId = markId.replaceAll(" ", "");
					markId = markId.replaceAll("	", "");
					markId = markId.replaceAll("_", "-");
					markId = markId.replaceAll("dkba", "DKBA");
					// markId = markId.replaceAll("（", "(");
					// markId = markId.replaceAll("）", ")");
					// markId = markId.replaceAll("x", "X");
					if (markId.length() == 0) {
						// markId="test"+i;
						jymsg += "第" + (i + 1) + "行错误,件号不能为空!<br/>";
					}
					String tuhao = cells[5].getContents();// 件号
					tuhao = tuhao.replaceAll(" ", "");
					tuhao = tuhao.replaceAll("	", "");
					tuhao = tuhao.replaceAll("", "");
					// tuhao = tuhao.replaceAll("（", "(");
					// tuhao = tuhao.replaceAll("）", ")");
					// tuhao = tuhao.replaceAll("x", "X");
					String unit = cells[6].getContents();// 单位
					unit = unit.replaceAll(" ", "");
					unit = unit.replaceAll("	", "");
					String xiaohao = cells[7].getContents();// 消耗量
					String banben = "";
					try {
						banben = cells[8].getContents();// 版本
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					banben = banben.replaceAll(" ", "");
					banben = banben.replaceAll("	", "");
					for (int n = 10; n < 14; n++) {// 去掉回车和空格
						markId = markId
								.replaceAll(String.valueOf((char) n), "");
						proName = proName.replaceAll(String.valueOf((char) n),
								"");
						tuhao = tuhao.replaceAll(String.valueOf((char) n), "");
						if (banben != null && banben.length() > 0) {
							banben = banben.replaceAll(
									String.valueOf((char) n), "");
						}
						type = type.replaceAll(String.valueOf((char) n), "");
					}
					String bizhong = "";
					try {
						bizhong = cells[9].getContents();// 比重
					} catch (Exception e) {
						e.printStackTrace();
					}
					bizhong = bizhong.replaceAll(" ", "");
					bizhong = bizhong.replaceAll("	", "");
					String biaochu = "";
					try {
						biaochu = cells[10].getContents();// 表处
						biaochu = biaochu.replaceAll("（", "(");
						biaochu = biaochu.replaceAll("）", ")");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					biaochu = biaochu.replaceAll(" ", "");
					biaochu = biaochu.replaceAll("	", "");
					String clType = "";
					try {
						clType = cells[11].getContents();// 材料类别
					} catch (Exception e) {
						// e.printStackTrace();
					}
					clType = clType.replaceAll(" ", "");
					clType = clType.replaceAll("	", "");
					String loadMarkId = "";
					try {
						loadMarkId = cells[12].getContents();// 初始总成
						// loadMarkId = loadMarkId.replaceAll("（", "(");
						// loadMarkId = loadMarkId.replaceAll("）", ")");
						// loadMarkId = loadMarkId.replaceAll("x", "X");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					loadMarkId = loadMarkId.replaceAll("_", "-");
					String kgliao = null;// 供料属性
					try {
						kgliao = cells[13].getContents();
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String wgType = null;// 外购件类型
					try {
						if (markId.endsWith("cs") || markId.endsWith("CS")) {
							wgType = "CS";
						} else {
							wgType = cells[14].getContents();
							wgType = wgType.replaceAll(" ", "");
							wgType = wgType.replaceAll("	", "");
						}
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String importance = null;// 重要性
					try {
						importance = cells[15].getContents();
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String sunhaostr = null;// 重要性
					Float sunhao = null;
					try {
						sunhaostr = cells[16].getContents();
						sunhao = Float.parseFloat(sunhaostr);
					} catch (Exception e) {
						// e.printStackTrace();
					}
					if (markId.startsWith("DKBA0")
							|| markId.startsWith("dkba0")) {
						banben = null;
					}

					// 技术规范号查找大类
					List<String> sameMax = bmmap.get(markId + loadMarkId);
					if (sameMax != null) {
						proName = sameMax.get(3);
						if (sameMax.get(1).length() == 5) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%05d", Integer
							// .parseInt(sameMax.get(2)));
							// } else {
							pt.setTuhao(markId + "-" + loadMarkId);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d", Integer
							// .parseInt(sameMax.get(2)));
							// } else {
							pt.setTuhao(markId + "-" + loadMarkId);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}

					} else {
						CodeTranslation codeTranslation = (CodeTranslation) totalDao
								.getObjectByCondition(
										"from CodeTranslation where keyCode=? and type='技术规范'",
										markId);
						String jsType = null;
						if (codeTranslation != null) {
							jsType = codeTranslation.getValueCode();
						}
						if (jsType != null && jsType.length() > 0) {
							String hasMarkId = (String) totalDao
									.getObjectByCondition("select markId from YuanclAndWaigj where (tuhao ='"
											+ markId
											+ "-"
											+ type
											+ "' or tuhao ='"
											+ markId
											+ type
											+ "'"
											+ " or (tuhao like '"
											+ markId
											+ "-%' and specification ='"
											+ type
											+ "'))");
							if (hasMarkId != null && hasMarkId.length() > 0) {
								pt.setTuhao(markId + "-" + loadMarkId);
								markId = hasMarkId;
							} else {
								proName = codeTranslation.getValueName();
								if (jsType.length() == 4) {
									jsType = jsType + ".";
								}
								// 先去原材料外购件库里查询
								String dlType = "外购件";
								// if (hasMarkId == null) {
								hasMarkId = (String) totalDao
										.getObjectByCondition("select markId from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and (tuhao ='"
												+ markId
												+ "-"
												+ type
												+ "' or tuhao ='"
												+ markId
												+ type
												+ "'"
												+ " or (tuhao like '"
												+ markId
												+ "-%' and specification ='"
												+ type + "'))");
								// }
								if (hasMarkId != null && hasMarkId.length() > 0) {
									pt.setShowSize(type);
									markId = hasMarkId;
									dlType = "自制";
								} else {
									// 需要新增
									Integer maxInteger = 0;
									// 先遍历map
									for (String key : bmmap.keySet()) {
										List<String> samjsType = bmmap.get(key);
										if (samjsType != null
												&& samjsType.get(1).equals(
														jsType)) {
											Integer nomax = Integer
													.parseInt(samjsType.get(2));
											pt.setProcardStyle("外购");
											if (maxInteger < nomax) {
												maxInteger = nomax;
											}
										}
									}
									if (maxInteger == 0) {
										String maxNo1 = (String) totalDao
												.getObjectByCondition("select max(markId) from YuanclAndWaigj where markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null) {
											// maxNo1 = (String) totalDao
											// .getObjectByCondition("select max(trademark) from YuanclAndWaigj where clClass='原材料' and tuhao like'"
											// + jsType + "%'");
											// if (hasMarkId != null) {
											// dlType = "原材料";
											// }
											// } else {
											dlType = "外购件";
										}
										String maxNo2 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplate where  markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");// 因为当零件类型为待定时原材料外购件库里没有办法查询
										String maxNo3 = (String) totalDao
												.getObjectByCondition("select max(chartNO) from ChartNOSC where  chartNO like'"
														+ jsType
														+ "%' and chartNO not like '"
														+ jsType + "%.%'");
										String maxNo4 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplatesb where sbApplyId "
														+ "in(select id from ProcardTemplateBanBenApply where processStatus not in('设变发起','分发项目组','项目主管初评','资料更新','关联并通知生产','生产后续','关闭','取消'))"
														+ " and markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%' ");
										if (maxNo1 != null
												&& maxNo1.length() > 0) {
											String[] maxStrs = maxNo1
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo2 != null
												&& maxNo2.length() > 0) {
											String[] maxStrs = maxNo2
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo3 != null
												&& maxNo3.length() > 0) {
											String[] maxStrs = maxNo3
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo4 != null
												&& maxNo4.length() > 0) {
											String[] maxStrs = maxNo4
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									maxInteger++;
									List<String> list = new ArrayList<String>();
									if ((dlType == null || dlType.equals(""))
											&& (procardStyle == null || procardStyle
													.equals(""))) {
										procardStyle = "待定";
										list.add("待定");
									} else {
										list.add(dlType);
									}
									list.add(jsType);
									list.add(maxInteger + "");
									list.add(proName);
									bmmap.put(markId + loadMarkId, list);
									if (jsType.length() == 5) {
										markId = jsType
												+ String.format("%05d",
														maxInteger);

									} else if (jsType.length() == 6) {
										markId = jsType
												+ String.format("%04d",
														maxInteger);
									}

									pt.setShowSize(loadMarkId);
								}
								// totalDao.clear();
							}
							// 存方到编码库里
							ChartNOSC chartnosc = (ChartNOSC) totalDao
									.getObjectByCondition(
											" from ChartNOSC where chartNO  =? ",
											markId);
							if (chartnosc == null && isChartNOGzType(markId)) {
								// 同步添加到编码库里
								chartnosc = new ChartNOSC();
								chartnosc.setChartNO(markId);
								chartnosc.setChartcode(Util
										.Formatnumber(markId));
								chartnosc.setIsuse("YES");
								chartnosc.setRemak("BOM导入");
								chartnosc.setSjsqUsers(user.getName());
								totalDao.save(chartnosc);
							}
						}

					}
					// if (type != null) {
					// if ((!markId.equals("DKBA0.480.0128")
					// && !markId.equals("DKBA0.480.0251")
					// && !markId.equals("DKBA0.480.0236")
					// && !markId.equals("DKBA0.480.0345") && !markId
					// .equals("DKBA0.480.1381"))) {
					// if (loadMarkId.contains("Al")
					// && !markId.endsWith("-AL")) {
					// markId += "-AL";
					// loadMarkId = "";
					// } else if ((loadMarkId.contains("stainless") ||
					// loadMarkId
					// .contains("Stainless"))
					// && !markId.endsWith("-S")) {
					// markId += "-S";
					// loadMarkId = "";
					// } else if (loadMarkId.contains("zinc")
					// && loadMarkId.contains("yellow")
					// && !markId.endsWith("-ZC")) {
					// markId += "-ZC";
					// loadMarkId = "";
					// }
					// }
					// }
					loadMarkId = loadMarkId.replaceAll(" ", "");
					loadMarkId = loadMarkId.replaceAll("	", "");
					Float corrCount = null;
					try {
						corrCount = Float.parseFloat(xiaohao);
					} catch (Exception e) {
						jymsg += "第" + (i + 1) + "行,用量填写有误或未填写。填写值:" + xiaohao
								+ "<br/>";
					}
					pt.setBelongLayer(belongLayer);
					pt.setProName(proName);
					pt.setSpecification(type);
					if (procardStyle != null && procardStyle.length() > 0
							&& !procardStyle.equals("总成")
							&& !procardStyle.equals("自制")
							&& !procardStyle.equals("外购")
							&& !procardStyle.equals("半成品")) {
						jymsg += "第" + (i + 1) + "行错误，零件类型:" + procardStyle
								+ "不在系统承认范围<br/>";
					}
					if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, procardStyle);
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(procardStyle)) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + procardStyle + "]类型,:["
									+ procardStyle0 + "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
					pt.setProcardStyle(procardStyle);
					pt.setMarkId(markId);
					pt.setBanBenNumber(banben);
					pt.setUnit(unit);
					pt.setCorrCount(corrCount);
					pt.setTuhao(tuhao);
					if (bizhong != null && bizhong.length() > 0) {
						try {
							pt.setBili(Float.parseFloat(bizhong.toString()));
						} catch (Exception e) {
							// TODO: handle exception
							jymsg += "第" + (i + 1) + "行错误，比重不是数值类型<br/>";
						}
					}
					pt.setClType(clType);
					pt.setBiaochu(biaochu);
					pt.setBzStatus("待编制");
					pt.setBianzhiName(user.getName());
					pt.setBianzhiId(user.getId());
					pt.setProductStyle("批产");
					pt.setCardXiaoHao(1f);
					pt.setLoadMarkId(loadMarkId);
					pt.setXuhao(0f);
					pt.setKgliao(kgliao);
					pt.setImportance(importance);
					if (sunhao != null && sunhao > 0) {
						pt.setSunhao(sunhao);
						pt.setSunhaoType(0);
					}
					pt.setDaoruDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date()));
					if (pt.getProcardStyle().equals("原材料")) {
						pt.setProcardStyle("外购");
					}
					if (pt.getProcardStyle().equals("外购")
							|| pt.getProcardStyle().equals("半成品")) {
						String addSql = null;
						if (banben == null || banben.length() == 0) {
							addSql = " and (banbenhao is null or banbenhao='')";
						} else {
							addSql = " and banbenhao='" + banben + "'";
						}
						// 库中查询现有类型
						String haswgType = (String) totalDao
								.getObjectByCondition(
										"select wgType from YuanclAndWaigj where  productStyle='批产' and (status is null or status!='禁用') and markId=?  "
												+ addSql, markId);
						if (haswgType != null && haswgType.length() > 0) {
							if (wgType != null && wgType.length() > 0) {
								if (!haswgType.equals(wgType)) {
									jymsg += "第" + (i + 1) + "行错误，物料类别:"
											+ wgType + "与现有物料类别:" + haswgType
											+ "不符<br/>";
								}
							} else {
								wgType = haswgType;
							}
						} else {
							if (wgType == null || wgType.length() == 0) {
								jymsg += "第" + (i + 1) + "行错误，该外购件未填写物料类别<br/>";
							} else if (umStr == null || umStr.length() == 0
									|| !umStr.contains("," + wgType + ",")) {
								jymsg += "第" + (i + 1)
										+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
							}
						}
					}
					pt.setWgType(wgType);
					// 生产节拍 单班时长
					if ((pt.getProcardStyle().equals("外购") || pt
							.getProcardStyle().equals("半成品"))
							&& wgType != null && wgType.length() > 0) {
						Category category = (Category) totalDao
								.getObjectByCondition(
										" from Category where name = ? and id not in (select fatherId from Category where fatherId  is not NULL) and avgDeliveryTime is not null order by id desc",
										wgType);// 这里要预防物料类别名称相同
						if (category != null) {
							pt.setAvgProductionTakt(category
									.getAvgProductionTakt());
							pt
									.setAvgDeliveryTime(category
											.getAvgDeliveryTime());
							pt.setCgperiod(pt.getAvgProductionTakt()
									* pt.getAvgDeliveryTime());
						} else {
							jymsg += "第" + (i + 1) + "行错误，" + wgType
									+ "不在物料类别库中或未填配送时长<br/>";
						}
					}
					ptList.add(pt);
				}
				Integer sbCount = ptList.size();
				if (ptList.size() > 0) {
					ProcardTemplate totalPt = ptList.get(0);
					totalPt.setYwMarkId(ywMarkId);
					totalPt.setAddDateTime(createTime);
					totalPt.setCreateDate(createTime);
					totalPt.setCreateUserName(createUser);
					totalPt.setLastUser(lastUser);
					totalPt.setProcardStyle("总成");
					totalPt.setCorrCount(1f);
					totalPt.setRootMarkId(totalPt.getMarkId());
					totalPt.setMaxCount(1000f);
					ProcardTemplate same = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=? and procardStyle='总成' and productStyle='批产' and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
									totalPt.getMarkId());
					if (same != null) {
						jymsg += "该总成件号已存在,导入失败!<br/>";
					}
					// if (jymsg.length() > 0) {
					// return jymsg;
					// }
					totalDao.save(totalPt);
					totalPt.setRootId(totalPt.getId());
					String msg = "";
					if (ptList.size() > 1) {
						// 获取关联ProcardTemplate,关联外购件库数据
						StringBuffer markIdsb = new StringBuffer();
						for (ProcardTemplate nowpt : ptList) {
							if (markIdsb.length() == 0) {
								markIdsb.append("'" + nowpt.getMarkId() + "'");
							} else {
								markIdsb.append(",'" + nowpt.getMarkId() + "'");
							}
						}
						if (markIdsb.length() > 0) {
							aboutptList = totalDao
									.query(" from ProcardTemplate where id in("
											+ "select min(id) from ProcardTemplate where productStyle='批产' and  (dataStatus is null or dataStatus !='删除') and markId in("
											// +
											// "select min(id) from ProcardTemplate where productStyle='批产' and markId in("
											+ markIdsb.toString()
											+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId");
							aboutwgjList = totalDao
									.query("from YuanclAndWaigj where productStyle='批产' and markId in("
											+ markIdsb.toString()
											+ ") and (banbenStatus is null or banbenStatus !='历史' ) order by markId");
						}

						// try {
						msg += addProcardTemplateTreeK3(totalPt, ptList,
								totalPt.getId(), 0, totalPt.getMarkId(),
								ywMarkId, base, 1, user);
						// } catch (Exception e) {
						// // TODO: handle exception
						// e.printStackTrace();
						// }

					}
					if (jymsg.length() > 0 || msg.length() > 0) {
						String msg0 = jsbcmsg;
						jsbcmsg = "";
						throw new RuntimeException(jymsg + msg + msg0);
					} else {
						for (ProcardTemplate hasHistory : ptList) {
							hasHistory.setAddcode(user.getCode());
							hasHistory.setAdduser(user.getName());
							hasHistory.setAddtime(nowTime);
							// if(hasHistory.getMarkId().equals("DKBA6.154.2464")){
							// System.out.println(hasHistory.getMarkId());
							// for(ProcardTemplate
							// sonProcard:hasHistory.getProcardTSet()){
							// System.out.println("markId:"+sonProcard.getMarkId()+",kgliao:"+sonProcard.getKgliao());
							//									
							// }
							// }
							if (hasHistory.getHistoryId() != null) {
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplate> historySonList = new ArrayList<ProcardTemplate>();
									try {
										historySonList = (List<ProcardTemplate>) totalDao
												.query(
														"from ProcardTemplate where procardTemplate.id=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
														hasHistory
																.getHistoryId());
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplate> nowSonSet = hasHistory
												.getProcardTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplate nowSon : nowSonSet) {
												hadMarkIds.add(nowSon
														.getMarkId());
											}
										}
										for (ProcardTemplate historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcard(hasHistory,
															historySon, "批产");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							} else {
								if (hasHistory.getId() != null) {
									if (hasHistory.getBanci() == null) {
										hasHistory.setBanci(0);
									}
									if (hasHistory.getBanbenStatus() == null) {
										hasHistory.setBanbenStatus("默认");
									}
									if (hasHistory.getBzStatus() == null
											|| hasHistory.getBzStatus().equals(
													"初始")
											|| hasHistory.getBzStatus().equals(
													"待编制")) {
										hasHistory.setBzStatus("待编制");
										hasHistory.setBianzhiId(user.getId());
										hasHistory.setBianzhiName(user
												.getName());
										totalDao.update(hasHistory);
									}
								}
							}
						}
						if (jsbcmsg.length() > 0) {
							String msg0 = jsbcmsg;
							jsbcmsg = "";
							return "导入成功,识别了" + sbCount + "条数据!<br/>"
									+ "板材粉末未计算信息:" + msg0;
						}
						jsbcmsg = "";
						return "导入成功,识别了" + sbCount + "条数据!";
					}
				}
				if (jymsg.length() > 0) {
					return jymsg;
				}
			}
		} catch (IOException e) {
			return "文件异常,可尝试将文件另存为2003版本的Excel文件后再重试!";
		}
		return "导入失败,未读取到有效数据,请核实模板格式是否有误!";
	}

	public String addProcardTemplateTreeK3(ProcardTemplate fatherPt,
			List<ProcardTemplate> ptList, Integer rootId, Integer i,
			String zcMarkId, String ywmarkId, int base, int bomtype, Users user) {
		String msg0 = "";
		String msg = "";
		i++;
		Set<ProcardTemplate> sonSet = fatherPt.getProcardTSet();
		if (sonSet == null) {
			sonSet = new HashSet<ProcardTemplate>();
		}
		List<String> markIdList = new ArrayList<String>();
		Float xh = 0f;
		String time = Util.getDateTime();
		if (i == 0 && markIdList.size() == 0) {
			markIdList = totalDao.query(
					"select markId from ProcardTemplate where procardTemplate.id=? "
							+ "and (dataStatus is null or dataStatus!='删除')",
					fatherPt.getId());
		}
		for (int j = i; j < ptList.size(); j++) {
			ProcardTemplate sonPt = ptList.get(j);
			// if(fatherPt.getMarkId().equals("DKBA6.157.2367")
			// &&sonPt.getMarkId().equals("DKBA8.099.8399")){
			// System.out.println(i);
			//				
			// }
			// System.out.println(fatherPt.getMarkId()+"-----"+sonPt.getMarkId());
			// System.out.println(fatherPt.getMarkId());
			// 判断是否为原材料
			YuanclAndWaigj ycl = null;
			try {
				if (sonPt.getBelongLayer() == null) {
					msg += "第" + (j + 1 + base) + "行错误,无阶层用量<br/>";
					continue;
				}
			} catch (Exception e) {
				msg += "第" + (i + base) + "行和第" + (j + 1 + base) + "出错" + e;
			}

			if (sonPt.getBelongLayer().equals(fatherPt.getBelongLayer() + 1)) {
				if (fatherPt.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
					msg += "第" + (i + base) + "行和第" + (j + 1 + base)
							+ "行错误,子零件件号不能和父零件件号相同!<br/>";
				}
				if (markIdList.contains(sonPt.getMarkId())) {
					msg += "第" + (j + 1 + base) + "行错误,同一父零件下不能有相同件号:"
							+ sonPt.getMarkId() + "的零件!<br/>";
				}
				if (sonPt.getProcardStyle().equals("原材料")) {
					sonPt.setProcardStyle("外购");
				}
				if (sonPt.getProcardStyle().equals("半成品")) {
					sonPt.setProcardStyle("外购");
					sonPt.setNeedProcess("yes");
				}
				if (sonPt.getProcardStyle() != null
						&& sonPt.getProcardStyle().equals("外购")) {
					// 如果是外购件就去外购件原材料库里去查原来有没有，如果没有就添加一条记录到外购件原材料库
					if (sonPt.getKgliao() == null
							|| sonPt.getKgliao().length() == 0) {
						if (sonPt.getMarkId().endsWith("cs")
								|| sonPt.getMarkId().endsWith("CS")) {
							sonPt.setKgliao("CS");
						} else {
							sonPt.setKgliao("TK");
						}
					} else if ("TK".equalsIgnoreCase(sonPt.getKgliao())) {// 导入时没有默认自购
						sonPt.setKgliao("TK");
					} else if ("CS".equalsIgnoreCase(sonPt.getKgliao())) {
						sonPt.setKgliao("CS");
					} else if ("TK AVL".equalsIgnoreCase(sonPt.getKgliao())) {
						sonPt.setKgliao("TK AVL");
					} else if ("TK Price".equalsIgnoreCase(sonPt.getKgliao())) {
						sonPt.setKgliao("TK Price");
					} else {
						msg += "第"
								+ (j + 1 + base)
								+ "行"
								+ sonPt.getMarkId()
								+ "--此件号的供料属性不在系统定义中（TK,CS,TK AVL,TK Price），请先处理!<br/>";
					}
				}
				// 版本比对
				msg += compareBanben(sonPt, aboutptList, ywmarkId, user,
						(j + 1 + base));
				if (sonPt.getProcardStyle() != null
						&& sonPt.getProcardStyle().equals("外购")) {
					if (sonPt.getKgliao() == null
							|| sonPt.getKgliao().length() == 0) {
						if (sonPt.getMarkId().endsWith("cs")
								|| sonPt.getMarkId().endsWith("CS")) {
							sonPt.setKgliao("CS");
						} else {
							sonPt.setKgliao("TK");
						}
					}
				}
				sonPt.setYwMarkId(ywmarkId);
				if (sonPt.getProcardStyle() != null
						&& sonPt.getProcardStyle().equals("待定")) {
					if (aboutwgjList != null && aboutwgjList.size() > 0) {
						for (YuanclAndWaigj aboutwgj : aboutwgjList) {
							if (sonPt.getMarkId().equals(aboutwgj.getMarkId())) {
								sonPt.setProcardStyle("外购");
								break;
							}
						}
					}

				}
				// 设置总成件号
				sonPt.setRootMarkId(zcMarkId);
				if (sonPt.getProcardStyle() != null
						&& sonPt.getProcardStyle().equals("外购")) {
					// 如果是外购件就去外购件原材料库里去查原来有没有，如果没有就添加一条记录到外购件原材料库
					// 此版本和供料属性可用
					YuanclAndWaigj pcky = null;// 批产可用版本
					YuanclAndWaigj wgjTem = null;// 同供料属性批产可用版本
					YuanclAndWaigj wgjTem2 = null;// 同供料属性可用版本
					YuanclAndWaigj wgjTemotherkgliao = null;// 不同供料属性可用版本
					YuanclAndWaigj wgjTemotherkgliao2 = null;// 不同供料属性可用版本
					YuanclAndWaigj wgjhistory = null;// 同供料属性不可用版本
					YuanclAndWaigj wgjhistorytherkgliao = null;// 不同供料属性不可用版本
					YuanclAndWaigj wgjTemerror = null;// 除开以上六种只同件号
					if (aboutwgjList != null && aboutwgjList.size() > 0) {
						boolean checkMarkId = false;
						for (YuanclAndWaigj aboutwgj : aboutwgjList) {
							if (sonPt.getMarkId().equalsIgnoreCase(
									aboutwgj.getMarkId())) {
								checkMarkId = true;
								if ((aboutwgj.getBanbenStatus() == null || !aboutwgj
										.getBanbenStatus().equals("历史"))
										&& !"禁用".equals(aboutwgj.getStatus())) {
									if (sonPt.getUnit() != null
											&& sonPt.getUnit().length() > 0) {
										if (!Util.isEquals(sonPt.getUnit(),
												aboutwgj.getUnit())) {
											msg += "第" + (j + 1 + base)
													+ "行,外购件"
													+ aboutwgj.getMarkId()
													+ "的单位为:" + sonPt.getUnit()
													+ "与现有的单位:"
													+ aboutwgj.getUnit()
													+ "的单位不一致!<br/>";
										}
									}
									if (aboutwgj.getProductStyle().equals("批产")) {
										pcky = aboutwgj;
									}
									if (Util.isEquals(aboutwgj.getBanbenhao(),
											sonPt.getBanBenNumber())) {
										if (Util.isEquals(aboutwgj.getKgliao(),
												sonPt.getKgliao())) {
											if (aboutwgj.getProductStyle()
													.equals("试制")) {
												wgjTem2 = aboutwgj;
											} else {
												wgjTem = aboutwgj;
											}
										} else {
											if (aboutwgj.getProductStyle()
													.equals("试制")) {
												wgjTemotherkgliao2 = aboutwgj;
											} else {
												wgjTemotherkgliao = aboutwgj;
											}
											continue;// 为了执行wgjTemerror =
											// aboutwgj;
										}
									}
								} else {
									if (Util.isEquals(aboutwgj.getBanbenhao(),
											sonPt.getBanBenNumber())) {
										if (Util.isEquals(aboutwgj.getKgliao(),
												sonPt.getKgliao())) {
											if (aboutwgj.getStatus().equals(
													"禁用")) {
												msg += "第"
														+ (j + 1 + base)
														+ "行,供料属性为："
														+ aboutwgj.getKgliao()
														+ "的外购件"
														+ aboutwgj.getMarkId()
														+ "的"
														+ aboutwgj
																.getSpecification()
														+ "规格的"
														+ aboutwgj
																.getBanbenhao()
														+ "版本已禁用!<br/>";
											}
											wgjhistory = aboutwgj;
											continue;// 为了执行wgjTemerror =
											// aboutwgj;
										} else {
											wgjhistorytherkgliao = aboutwgj;
											continue;// 为了执行wgjTemerror =
											// aboutwgj;
										}
									}
								}
								wgjTemerror = aboutwgj;
							} else if (checkMarkId) {
								break;
							}
						}
					}
					if (pcky != null) {
						if ("试制".equals(sonPt.getProductStyle())) {
							if (Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pcky.getBanbenhao(),
									banBenxuhaoMap) < 0) {
								msg += "第" + (j + 1 + base) + "行"
										+ sonPt.getMarkId()
										+ "--此件号版本低于系统外购件库中版本(系统版本为:"
										+ wgjTemerror.getBanbenhao()
										+ "，导入版本为:" + sonPt.getBanBenNumber()
										+ ")，请先处理!<br/>";
							} else if (Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pcky.getBanbenhao(),
									banBenxuhaoMap) > 0) {// 如果试制的版本号比较高
								if (wgjTem2 != null) {
									// 有试制的同版本同供料属性数据
									sonPt.setProName(pcky.getName());
									sonPt.setSpecification(pcky
											.getSpecification());
									sonPt.setTuhao(pcky.getTuhao());// 图号
									sonPt.setUnit(pcky.getUnit());// BOM用的单位
									sonPt.setTuhao(pcky.getTuhao());// 图号
									sonPt.setWgType(pcky.getWgType());
									sonPt.setClType(pcky.getCaizhi());
									sonPt.setBili(pcky.getBili());
								} else {
									boolean addnew = false;
									if (wgjTem2 == null) {
										addnew = true;
									} else if (Banbenxuhao.comparebanben(sonPt
											.getBanBenNumber(), wgjTem2
											.getBanbenhao(), banBenxuhaoMap) > 0) {
										addnew = true;
									}
									if (addnew) {
										wgjTem = new YuanclAndWaigj();
										wgjTem.setMarkId(sonPt.getMarkId());// 件号
										wgjTem.setName(pcky.getName());// 名称
										wgjTem.setSpecification(pcky
												.getSpecification());// 规格
										wgjTem.setUnit(pcky.getUnit());// BOM用的单位
										wgjTem.setCkUnit(pcky.getCkUnit());// 仓库用的单位
										wgjTem.setBanbenhao(sonPt
												.getBanBenNumber());// 版本号
										if (ywmarkId == null
												|| ywmarkId.length() == 0) {
											wgjTem.setZcMarkId(zcMarkId);// 总成号
										} else {
											wgjTem.setZcMarkId(ywmarkId);// 总成号
										}
										wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
										wgjTem.setCaizhi(pcky.getCaizhi());// 材质类型
										wgjTem.setTuhao(pcky.getTuhao());// 图号
										wgjTem.setWgType(pcky.getWgType());
										wgjTem.setKgliao(sonPt.getKgliao());
										wgjTem.setProductStyle(sonPt
												.getProductStyle());
										wgjTem.setPricestatus("新增");
										wgjTem.setBanbenStatus("默认");
										wgjTem.setAddTime(time);
										wgjTem.setAddUserCode(user.getCode());
										wgjTem.setAddUserName(user.getName());
										wgjTem.setImportance(sonPt
												.getImportance());
										wgjTem.setBili(pcky.getBili());
										wgjTem.setCgperiod(sonPt.getCgperiod());
										wgjTem.setAvgProductionTakt(sonPt
												.getAvgProductionTakt());// 生产节拍(s)
										wgjTem.setAvgDeliveryTime(sonPt
												.getAvgDeliveryTime());// 配送时长(d)
										if (wgjTem.getWgType() == null) {
											msg += "第" + (j + 1 + base) + "行"
													+ sonPt.getMarkId()
													+ "--此外购件没有物料类别，请先处理!<br/>";
										} else {
											totalDao.save(wgjTem);
										}
										sonPt.setProName(pcky.getName());
										sonPt.setSpecification(pcky
												.getSpecification());
										sonPt.setTuhao(pcky.getTuhao());// 图号
										sonPt.setUnit(pcky.getUnit());// BOM用的单位
										sonPt.setTuhao(pcky.getTuhao());// 图号
										sonPt.setWgType(pcky.getWgType());
										sonPt.setClType(pcky.getCaizhi());
										sonPt.setBili(pcky.getBili());
										aboutwgjList.add(wgjTem);
									}
								}

							} else {// 版本号一致
								sonPt.setProName(pcky.getName());
								sonPt.setSpecification(pcky.getSpecification());
								sonPt.setTuhao(pcky.getTuhao());// 图号
								sonPt.setUnit(pcky.getUnit());// BOM用的单位
								sonPt.setTuhao(pcky.getTuhao());// 图号
								sonPt.setWgType(pcky.getWgType());
								sonPt.setClType(pcky.getCaizhi());
								sonPt.setBili(pcky.getBili());
							}
						} else {
							if (Banbenxuhao.comparebanben(sonPt
									.getBanBenNumber(), pcky.getBanbenhao(),
									banBenxuhaoMap) != 0) {
								if (!"试制".equals(sonPt.getProductStyle())) {
									msg += "第" + (j + 1 + base) + "行"
											+ sonPt.getMarkId()
											+ "--此件号与系统外购件库中版本不一致(系统版本为:"
											+ wgjTemerror.getBanbenhao()
											+ "，导入版本为:"
											+ sonPt.getBanBenNumber()
											+ ")，请先处理!<br/>";
								}

							} else {// 资料复制
								sonPt.setTuhao(pcky.getTuhao());// 图号以库中为标准
								sonPt.setProName(pcky.getName());
								sonPt.setUnit(pcky.getUnit());
								sonPt.setSpecification(pcky.getSpecification());
								sonPt.setLoadMarkId(pcky.getZcMarkId());
								sonPt.setImportance(pcky.getImportance());
								sonPt.setWgType(pcky.getWgType());
							}
						}
					} else {
						if (wgjTem == null) {// 不存在可用、同版本、同供料属性批产外购件
							if (wgjTem2 != null) {// 存在可用同版本同供料属性试制外购件
								sonPt.setTuhao(wgjTem2.getTuhao());// 图号以库中为标准
								sonPt.setProName(wgjTem2.getName());
								sonPt.setUnit(wgjTem2.getUnit());
								sonPt.setSpecification(wgjTem2
										.getSpecification());
								sonPt.setLoadMarkId(wgjTem2.getZcMarkId());
								sonPt.setImportance(wgjTem2.getImportance());
								sonPt.setWgType(wgjTem2.getWgType());
							} else if (wgjhistory != null) {// 存在不可用、同版本、同供料属性
								msg += "外购件" + wgjhistory.getMarkId() + "的"
										+ wgjhistory.getSpecification() + "规格的"
										+ wgjhistory.getBanbenhao()
										+ "版本已禁用!<br/>";
							} else if (wgjTemotherkgliao != null) {// 存在可用、同版本、不同供料属性批产外购件则需要添加新供料属性
								wgjTem = new YuanclAndWaigj();
								wgjTem.setMarkId(wgjTemotherkgliao.getMarkId());// 件号
								wgjTem.setName(wgjTemotherkgliao.getName());// 名称
								wgjTem.setSpecification(wgjTemotherkgliao
										.getSpecification());// 规格
								wgjTem.setUnit(wgjTemotherkgliao.getUnit());// BOM用的单位
								wgjTem.setCkUnit(wgjTemotherkgliao.getCkUnit());// 仓库用的单位
								wgjTem.setBanbenhao(sonPt.getBanBenNumber());// 版本号
								if (ywmarkId == null || ywmarkId.length() == 0) {
									wgjTem.setZcMarkId(zcMarkId);// 总成号
								} else {
									wgjTem.setZcMarkId(ywmarkId);// 总成号
								}
								wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
								wgjTem.setCaizhi(wgjTemotherkgliao.getCaizhi());// 材质类型
								wgjTem.setTuhao(wgjTemotherkgliao.getTuhao());// 图号
								wgjTem.setWgType(wgjTemotherkgliao.getWgType());
								wgjTem.setKgliao(sonPt.getKgliao());
								wgjTem.setProductStyle(sonPt.getProductStyle());
								wgjTem.setPricestatus("新增");
								wgjTem.setBanbenStatus("默认");
								wgjTem.setBili(wgjTemotherkgliao.getBili());
								wgjTem.setAddTime(time);
								wgjTem.setAddUserCode(user.getCode());
								wgjTem.setAddUserName(user.getName());
								wgjTem.setImportance(sonPt.getImportance());
								wgjTem.setAreakg(wgjTemotherkgliao.getAreakg());// 每公斤使用面积
								wgjTem.setDensity(wgjTemotherkgliao
										.getDensity());// 密度
								wgjTem.setMinkc(wgjTemotherkgliao.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
								wgjTem.setCgcount(wgjTemotherkgliao
										.getCgcount());// 安全库存采购的采购量
								wgjTem.setCgperiod(wgjTemotherkgliao
										.getCgperiod());
								wgjTem.setAvgProductionTakt(sonPt
										.getAvgProductionTakt());// 生产节拍(s)
								wgjTem.setAvgDeliveryTime(sonPt
										.getAvgDeliveryTime());// 配送时长(d)
								if (wgjTem.getWgType() == null) {
									msg += "第" + (j + 1 + base) + "行"
											+ sonPt.getMarkId()
											+ "--此外购件没有物料类别，请先处理!<br/>";
								} else {
									totalDao.save(wgjTem);
								}
								if (sonPt.getProductStyle().equals("批产")) {// 生成批产外购数据时将试制设置为历史
									String wgjbanbenSql = null;
									if (wgjTem.getBanbenhao() == null
											|| wgjTem.getBanbenhao().equals("")) {
										wgjbanbenSql = " and (banbenhao is null or banbenhao='')";
									} else {
										wgjbanbenSql = " and banbenhao='"
												+ wgjTem.getBanbenhao() + "'";
									}
									List<YuanclAndWaigj> hisszwgjList = totalDao
											.query(
													"from YuanclAndWaigj where productStyle='试制'and markId=? and kgliao=? and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用') "
															+ wgjbanbenSql,
													wgjTem.getMarkId(), wgjTem
															.getKgliao());
									if (hisszwgjList != null
											&& hisszwgjList.size() > 0) {
										for (YuanclAndWaigj hisszwgj : hisszwgjList) {
											hisszwgj.setBanbenStatus("历史");
											totalDao.update(hisszwgj);
										}
									}
								}
								sonPt.setProName(wgjTemotherkgliao.getName());
								sonPt.setSpecification(wgjTemotherkgliao
										.getSpecification());
								sonPt.setTuhao(wgjTemotherkgliao.getTuhao());// 图号
								sonPt.setUnit(wgjTemotherkgliao.getUnit());// BOM用的单位
								sonPt.setTuhao(wgjTemotherkgliao.getTuhao());// 图号
								sonPt.setWgType(wgjTemotherkgliao.getWgType());
								sonPt.setClType(wgjTemotherkgliao.getCaizhi());
								sonPt.setBili(wgjTemotherkgliao.getBili());
								aboutwgjList.add(wgjTem);
							} else if (wgjTemotherkgliao2 != null) {// 存在可用、同版本、不同供料属性试制外购件则需要添加新供料属性
								wgjTem = new YuanclAndWaigj();
								wgjTem
										.setMarkId(wgjTemotherkgliao2
												.getMarkId());// 件号
								wgjTem.setName(wgjTemotherkgliao2.getName());// 名称
								wgjTem.setSpecification(wgjTemotherkgliao2
										.getSpecification());// 规格
								wgjTem.setUnit(wgjTemotherkgliao2.getUnit());// BOM用的单位
								wgjTem
										.setCkUnit(wgjTemotherkgliao2
												.getCkUnit());// 仓库用的单位
								wgjTem.setBanbenhao(sonPt.getBanBenNumber());// 版本号
								if (ywmarkId == null || ywmarkId.length() == 0) {
									wgjTem.setZcMarkId(zcMarkId);// 总成号
								} else {
									wgjTem.setZcMarkId(ywmarkId);// 总成号
								}
								wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
								wgjTem
										.setCaizhi(wgjTemotherkgliao2
												.getCaizhi());// 材质类型
								wgjTem.setTuhao(wgjTemotherkgliao2.getTuhao());// 图号
								wgjTem
										.setWgType(wgjTemotherkgliao2
												.getWgType());
								wgjTem.setKgliao(sonPt.getKgliao());
								wgjTem.setProductStyle(sonPt.getProductStyle());
								wgjTem.setPricestatus("新增");
								wgjTem.setBanbenStatus("默认");
								wgjTem.setBili(wgjTemotherkgliao2.getBili());
								wgjTem.setAddTime(time);
								wgjTem.setAddUserCode(user.getCode());
								wgjTem.setAddUserName(user.getName());
								wgjTem.setImportance(sonPt.getImportance());
								wgjTem
										.setAreakg(wgjTemotherkgliao2
												.getAreakg());// 每公斤使用面积
								wgjTem.setDensity(wgjTemotherkgliao2
										.getDensity());// 密度
								wgjTem.setMinkc(wgjTemotherkgliao2.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
								wgjTem.setCgcount(wgjTemotherkgliao2
										.getCgcount());// 安全库存采购的采购量
								wgjTem.setCgperiod(wgjTemotherkgliao2
										.getCgperiod());
								wgjTem.setAvgProductionTakt(sonPt
										.getAvgProductionTakt());// 生产节拍(s)
								wgjTem.setAvgDeliveryTime(sonPt
										.getAvgDeliveryTime());// 配送时长(d)
								sonPt.setProName(wgjTemotherkgliao2.getName());
								sonPt.setSpecification(wgjTemotherkgliao2
										.getSpecification());
								sonPt.setTuhao(wgjTemotherkgliao2.getTuhao());// 图号
								sonPt.setUnit(wgjTemotherkgliao2.getUnit());// BOM用的单位
								sonPt.setTuhao(wgjTemotherkgliao2.getTuhao());// 图号
								sonPt.setWgType(wgjTemotherkgliao2.getWgType());
								sonPt.setClType(wgjTemotherkgliao2.getCaizhi());
								sonPt.setBili(wgjTemotherkgliao2.getBili());
								if (sonPt.getProductStyle().equals("批产")) {// 生成批产外购数据时将试制设置为历史
									String wgjbanbenSql = null;
									if (wgjTem.getBanbenhao() == null
											|| wgjTem.getBanbenhao().equals("")) {
										wgjbanbenSql = " and (banbenhao is null or banbenhao='')";
									} else {
										wgjbanbenSql = " and banbenhao='"
												+ wgjTem.getBanbenhao() + "'";
									}
									List<YuanclAndWaigj> hisszwgjList = totalDao
											.query(
													"from YuanclAndWaigj where productStyle='试制'and markId=? and kgliao=? and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用') "
															+ wgjbanbenSql,
													wgjTem.getMarkId(), wgjTem
															.getKgliao());
									if (hisszwgjList != null
											&& hisszwgjList.size() > 0) {
										for (YuanclAndWaigj hisszwgj : hisszwgjList) {
											if (!Util.isEquals(hisszwgj
													.getName(), sonPt
													.getProName())) {
												msg += "第"
														+ (j + 1 + base)
														+ "行"
														+ sonPt.getMarkId()
														+ "--此外购件名称与现有的试制外购件库中名称不一样请先统一在导入!<br/>";
												break;
											}
											hisszwgj.setBanbenStatus("历史");
											totalDao.update(hisszwgj);
											// 资料借用
											wgjTem.setImportance(sonPt
													.getImportance());
											wgjTem.setAreakg(hisszwgj
													.getAreakg());// 每公斤使用面积
											wgjTem.setDensity(hisszwgj
													.getDensity());// 密度
											wgjTem
													.setMinkc(hisszwgj
															.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
											wgjTem.setCgcount(hisszwgj
													.getCgcount());// 安全库存采购的采购量
											wgjTem.setCgperiod(hisszwgj
													.getCgperiod());
											wgjTem
													.setAvgProductionTakt(hisszwgj
															.getAvgProductionTakt());// 生产节拍(s)
											wgjTem.setAvgDeliveryTime(hisszwgj
													.getAvgDeliveryTime());// 配送时长(d)
											wgjTem.setName(hisszwgj.getName());
											wgjTem.setSpecification(hisszwgj
													.getSpecification());
											wgjTem
													.setTuhao(hisszwgj
															.getTuhao());// 图号
											wgjTem.setUnit(hisszwgj.getUnit());// BOM用的单位
											wgjTem
													.setTuhao(hisszwgj
															.getTuhao());// 图号
											wgjTem.setWgType(hisszwgj
													.getWgType());
											wgjTem.setCaizhi(hisszwgj
													.getCaizhi());
											wgjTem.setBili(hisszwgj.getBili());
											sonPt
													.setProName(hisszwgj
															.getName());
											sonPt.setSpecification(hisszwgj
													.getSpecification());
											sonPt.setTuhao(hisszwgj.getTuhao());// 图号
											sonPt.setUnit(hisszwgj.getUnit());// BOM用的单位
											sonPt.setTuhao(hisszwgj.getTuhao());// 图号
											sonPt.setWgType(hisszwgj
													.getWgType());
											sonPt.setClType(hisszwgj
													.getCaizhi());
											sonPt.setBili(hisszwgj.getBili());
										}
									}
								}
								if (wgjTem.getWgType() == null) {
									msg += "第" + (j + 1 + base) + "行"
											+ sonPt.getMarkId()
											+ "--此外购件没有物料类别，请先处理!<br/>";
								} else {
									totalDao.save(wgjTem);
								}
								aboutwgjList.add(wgjTem);
							} else if (wgjhistorytherkgliao != null) {// 存在不可用、同版本、不同供料属性
								if (!"试制".equals(sonPt.getProductStyle())) {
									msg += "第"
											+ (j + 1 + base)
											+ "行外购件"
											+ wgjhistorytherkgliao.getMarkId()
											+ "的"
											+ wgjhistorytherkgliao
													.getSpecification()
											+ "规格的"
											+ wgjhistorytherkgliao
													.getBanbenhao()
											+ "版本已禁用!<br/>";
								}
							} else {
								// 查询其他版本的有没有
								if (wgjTemerror != null) {
									if (!"试制".equals(sonPt.getProductStyle())) {
										msg += "第" + (j + 1 + base) + "行"
												+ sonPt.getMarkId()
												+ "--此件号与系统外购件库中版本不一致(系统版本为:"
												+ wgjTemerror.getBanbenhao()
												+ "，导入版本为:"
												+ sonPt.getBanBenNumber()
												+ ")，请先处理!<br/>";
									} else {// 添加试制新版本外购件
										wgjTem = new YuanclAndWaigj();
										wgjTem.setMarkId(wgjTemerror
												.getMarkId());// 件号
										wgjTem.setName(wgjTemerror.getName());// 名称
										wgjTem.setSpecification(wgjTemerror
												.getSpecification());// 规格
										wgjTem.setUnit(wgjTemerror.getUnit());// BOM用的单位
										wgjTem.setCkUnit(wgjTemerror
												.getCkUnit());// 仓库用的单位
										wgjTem.setBanbenhao(sonPt
												.getBanBenNumber());// 版本号
										if (ywmarkId == null
												|| ywmarkId.length() == 0) {
											wgjTem.setZcMarkId(zcMarkId);// 总成号
										} else {
											wgjTem.setZcMarkId(ywmarkId);// 总成号
										}
										wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
										wgjTem.setCaizhi(wgjTemerror
												.getCaizhi());// 材质类型
										wgjTem.setTuhao(wgjTemerror.getTuhao());// 图号
										wgjTem.setWgType(wgjTemerror
												.getWgType());
										wgjTem.setKgliao(sonPt.getKgliao());
										wgjTem.setProductStyle(sonPt
												.getProductStyle());
										wgjTem.setPricestatus("新增");
										wgjTem.setBanbenStatus("默认");
										wgjTem.setBili(wgjTemerror.getBili());
										wgjTem.setAddTime(time);
										wgjTem.setAddUserCode(user.getCode());
										wgjTem.setAddUserName(user.getName());
										wgjTem.setImportance(sonPt
												.getImportance());
										wgjTem.setAreakg(wgjTemerror
												.getAreakg());// 每公斤使用面积
										wgjTem.setDensity(wgjTemerror
												.getDensity());// 密度
										wgjTem.setMinkc(wgjTemerror.getMinkc());// 最小库存量；低于此库存开始自动采购（为null时不考虑）
										wgjTem.setCgcount(wgjTemerror
												.getCgcount());// 安全库存采购的采购量
										wgjTem.setCgperiod(wgjTemerror
												.getCgperiod());
										wgjTem.setAvgProductionTakt(sonPt
												.getAvgProductionTakt());// 生产节拍(s)
										wgjTem.setAvgDeliveryTime(sonPt
												.getAvgDeliveryTime());// 配送时长(d)
										if (wgjTem.getWgType() == null) {
											msg += "第" + (j + 1 + base) + "行"
													+ sonPt.getMarkId()
													+ "--此外购件没有物料类别，请先处理!<br/>";
										} else {
											totalDao.save(wgjTem);
										}
										sonPt.setProName(wgjTemerror.getName());
										sonPt.setSpecification(wgjTemerror
												.getSpecification());
										sonPt.setTuhao(wgjTemerror.getTuhao());// 图号
										sonPt.setUnit(wgjTemerror.getUnit());// BOM用的单位
										sonPt.setTuhao(wgjTemerror.getTuhao());// 图号
										sonPt
												.setWgType(wgjTemerror
														.getWgType());
										sonPt
												.setClType(wgjTemerror
														.getCaizhi());
										sonPt.setBili(wgjTemerror.getBili());
										aboutwgjList.add(wgjTem);
									}
								} else {// 没有出现过此件号，需添加
									// SELECT distinct LENGTH(type) FROM
									// ta_ChartNOGzType where LENGTH(type)>0

									// 导入的外购件号是否符合编码库的编码规则;
									boolean flag = true;
									// boolean flag =
									// isChartNOGzType(sonPt.getMarkId());
									if (flag) {
										wgjTem = new YuanclAndWaigj();
										wgjTem.setMarkId(sonPt.getMarkId());// 件号
										wgjTem.setName(sonPt.getProName());// 名称
										wgjTem.setSpecification(sonPt
												.getSpecification());// 规格
										wgjTem.setUnit(sonPt.getUnit());// BOM用的单位
										wgjTem.setCkUnit(sonPt.getUnit());// 仓库用的单位
										wgjTem.setBanbenhao(sonPt
												.getBanBenNumber());// 版本号
										if (ywmarkId == null
												|| ywmarkId.length() == 0) {
											wgjTem.setZcMarkId(zcMarkId);// 总成号
										} else {
											wgjTem.setZcMarkId(ywmarkId);// 总成号
										}
										wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
										wgjTem.setCaizhi(sonPt.getClType());// 材质类型
										wgjTem.setTuhao(sonPt.getTuhao());// 图号
										wgjTem.setWgType(sonPt.getWgType());
										wgjTem.setKgliao(sonPt.getKgliao());
										wgjTem.setProductStyle(sonPt
												.getProductStyle());
										wgjTem.setPricestatus("新增");
										wgjTem.setBanbenStatus("默认");
										wgjTem.setAddTime(time);
										wgjTem.setAddUserCode(user.getCode());
										wgjTem.setAddUserName(user.getName());
										wgjTem.setImportance(sonPt
												.getImportance());
										wgjTem.setBili(sonPt.getBili());
										wgjTem.setCgperiod(sonPt.getCgperiod());
										wgjTem.setAvgProductionTakt(sonPt
												.getAvgProductionTakt());// 生产节拍(s)
										wgjTem.setAvgDeliveryTime(sonPt
												.getAvgDeliveryTime());// 配送时长(d)
										if (wgjTem.getWgType() == null) {
											msg += "第" + (j + 1 + base) + "行"
													+ sonPt.getMarkId()
													+ "--此外购件没有物料类别，请先处理!<br/>";
										} else {
											totalDao.save(wgjTem);
										}
										if (sonPt.getProductStyle()
												.equals("批产")) {// 生成批产外购数据时将试制设置为历史
											String wgjbanbenSql = null;
											if (wgjTem.getBanbenhao() == null
													|| wgjTem.getBanbenhao()
															.equals("")) {
												wgjbanbenSql = " and (banbenhao is null or banbenhao='')";
											} else {
												wgjbanbenSql = " and banbenhao='"
														+ wgjTem.getBanbenhao()
														+ "'";
											}
											List<YuanclAndWaigj> hisszwgjList = totalDao
													.query(
															"from YuanclAndWaigj where productStyle='试制'and markId=? and kgliao=? and (banbenStatus is null or banbenStatus !='历史') and (status is null or status!='禁用') "
																	+ wgjbanbenSql,
															wgjTem.getMarkId(),
															wgjTem.getKgliao());
											if (hisszwgjList != null
													&& hisszwgjList.size() > 0) {
												for (YuanclAndWaigj hisszwgj : hisszwgjList) {
													if (!Util.isEquals(hisszwgj
															.getName(), sonPt
															.getProName())) {
														msg += "第"
																+ (j + 1 + base)
																+ "行"
																+ sonPt
																		.getMarkId()
																+ "--此外购件名称与现有的试制外购件库中名称不一样请先统一在导入!<br/>";
													} else {
														hisszwgj
																.setBanbenStatus("历史");
														totalDao
																.update(hisszwgj);
													}

												}
											}
										}
										aboutwgjList.add(wgjTem);
										ChartNOSC chartnosc = (ChartNOSC) totalDao
												.getObjectByCondition(
														" from ChartNOSC where chartNO  =? ",
														wgjTem.getMarkId());
										if (chartnosc == null
												&& isChartNOGzType(wgjTem
														.getMarkId())) {
											// 同步添加到编码库里
											chartnosc = new ChartNOSC();
											chartnosc.setChartNO(wgjTem
													.getMarkId());
											chartnosc.setChartcode(Util
													.Formatnumber(wgjTem
															.getMarkId()));
											chartnosc.setIsuse("YES");
											chartnosc.setRemak("BOM导入");
											chartnosc.setSjsqUsers(user
													.getName());
											totalDao.save(chartnosc);
										}
									} else {
										msg += "第" + (j + 1 + base) + "行外购件件号"
												+ sonPt.getMarkId()
												+ "的格式不符合编码库里的编码规则！<br/>";

									}
								}
							}
						} else if (wgjTem.getStatus() != null
								&& wgjTem.getStatus().equals("禁用")) {
							// if (!"试制".equals(sonPt.getProductStyle())) {
							msg += "第" + (j + 1 + base) + "行外购件"
									+ wgjTem.getMarkId() + "的"
									+ wgjTem.getSpecification() + "规格的"
									+ wgjTem.getBanbenhao() + "版本的"
									+ sonPt.getKgliao() + "的供料属性已禁用!<br/>";
							// }
						} else {
							sonPt.setTuhao(wgjTem.getTuhao());// 图号以库中为标准
							sonPt.setProName(wgjTem.getName());
							sonPt.setUnit(wgjTem.getUnit());
							sonPt.setSpecification(wgjTem.getSpecification());
							sonPt.setLoadMarkId(wgjTem.getZcMarkId());
							sonPt.setImportance(wgjTem.getImportance());
							sonPt.setWgType(wgjTem.getWgType());
						}
					}

				}
				sonPt.setFatherId(fatherPt.getId());
				sonPt.setRootId(rootId);
				sonPt.setProcardTemplate(fatherPt);
				sonPt.setXuhao(xh);
				if (sonPt.getProcardStyle().equals("外购")
						|| sonPt.getProcardStyle().equals("待定")) {
					sonPt.setQuanzi1(1f);
					sonPt.setQuanzi2(sonPt.getCorrCount());
				}
				if (fatherPt.getCardXiaoHao() != null
						&& sonPt.getCorrCount() != null) {
					sonPt.setCardXiaoHao(fatherPt.getCardXiaoHao()
							* sonPt.getCorrCount());
				} else {
					sonPt.setCardXiaoHao(0f);
				}
				xh++;
				sonPt.setYwMarkId(ywmarkId);
				sonSet.add(sonPt);
				// sonPt.setProductStyle(fatherPt.getProductStyle());
				markIdList.add(sonPt.getMarkId());
				if (sonPt.getBanci() == null) {
					sonPt.setBanci(0);
				}
				if (sonPt.getId() == null) {
					if (sonPt.getProcardStyle() == null
							|| (!sonPt.getProcardStyle().equals("总成")
									&& !sonPt.getProcardStyle().equals("自制")
									&& !sonPt.getProcardStyle().equals("待定") && !sonPt
									.getProcardStyle().equals("外购"))) {
						msg += "第" + (j + 1 + base) + "行" + sonPt.getMarkId()
								+ "--此零件生成类型错误：(" + sonPt.getProcardStyle()
								+ ") ,只能为：(总成,自制,外购,'待定'),请先处理!<br/>";
					}
					totalDao.save(sonPt);
				} else {
					totalDao.update(sonPt);
				}
				if (thisminId == null || thisminId == 0) {
					thisminId = sonPt.getId();
				}
				msg += isSameProcardStyle(sonPt);
				aboutptList.add(sonPt);
				if (ptList.size() > (j + 1) && sonPt.getHistoryId() == null) {
					msg += addProcardTemplateTreeK3(sonPt, ptList, rootId, j,
							zcMarkId, ywmarkId, base, bomtype, user);
				}
				/************************** 板材粉末自动计算 *************************************************/
				if ("自制".equals(sonPt.getProcardStyle())
						&& sonPt.getBelongLayer() != null) {
					List<String> sonMarkIds = new ArrayList<String>();
					String banbensql = "";
					if (sonPt.getBanBenNumber() != null
							&& !"".equals(sonPt.getBanBenNumber())) {
						banbensql = " and banBenNumber = '"
								+ sonPt.getBanBenNumber() + "'";
					} else {
						banbensql = " and (banBenNumber is null or banBenNumber = '' )";
					}
					ProcardTemplate bzpt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									" from ProcardTemplate where markId = ? and bzStatus = '已批准' and productStyle= '批产' "
											+ " and (banbenStatus is null or banbenStatus = '' or banbenStatus = '默认') and (dataStatus is null or dataStatus!='删除')"
											+ banbensql, sonPt.getMarkId());
					if (bzpt == null && "试制".equals(sonPt.getProductStyle())) {
						bzpt = (ProcardTemplate) totalDao
								.getObjectByCondition(
										" from ProcardTemplate where markId = ? and bzStatus = '已批准' and productStyle= '试制' "
												+ " and (banbenStatus is null or banbenStatus = '' or banbenStatus = '默认') and (dataStatus is null or dataStatus!='删除')"
												+ banbensql, sonPt.getMarkId());
					}
					if (bzpt == null) {
						Set<ProcardTemplate> ptSonsonSet = sonPt
								.getProcardTSet();
						if (ptSonsonSet != null && ptSonsonSet.size() > 0) {
							for (ProcardTemplate ptsonson : ptSonsonSet) {
								sonMarkIds.add(ptsonson.getMarkId());
							}
						}
						List<ProcardTemplate> zzptList = (List<ProcardTemplate>) totalDao
								.query(
										" from ProcardTemplate where markId = ? and id!=?"
												+ " and (banbenStatus is null or banbenStatus = '' or banbenStatus = '默认') and (dataStatus is null or dataStatus!='删除')",
										sonPt.getMarkId(), sonPt.getId());
						ProcardTemplate historyPt1 = null;// 查看该件号之前是否存在
						ProcardTemplate historyPtother1 = null;// 查看该件号之前是否存在不同版本
						String productStylesql1 = null;
						if (sonPt.getProductStyle() != null
								&& sonPt.getProductStyle().equals("试制")) {
							productStylesql1 = " and 1=1";// 试制可借用批产
						} else {
							productStylesql1 = " and productStyle='批产'";
						}
						String sqlhistory1 = "from ProcardTemplate where markId=?  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')"
								+ productStylesql1;
						String sqlhistoryOther1 = "from ProcardTemplate where markId=?  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')"
								+ productStylesql1;
						Set<ProcardTemplate> sonsetPt0 = sonPt.getProcardTSet();
						if (sonsetPt0 == null) {
							sonsetPt0 = new HashSet<ProcardTemplate>();
						}
						String productStylesql = null;
						if (sonPt.getProductStyle() != null
								&& sonPt.getProductStyle().equals("试制")) {
							productStylesql = " and 1=1";// 试制可借用批产
						} else {
							productStylesql = " and productStyle='批产'";
						}

						String sqlhistory = "from ProcardTemplate where markId=?  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')"
								+ productStylesql;
						// 板材粉末计算开始

						if (sonPt.getThisLength() != null
								&& sonPt.getThisLength() > 0
								&& sonPt.getThisWidth() != null
								&& sonPt.getThisWidth() > 0
								&& sonPt.getThisHight() != null
								&& sonPt.getThisHight() > 0) {
							// 判断是否存在材料类别进行进行板材
							if (sonPt.getClType() != null
									&& sonPt.getClType().length() > 0) {
								PanelSize panel = null;
								Float hudu = null;
								if (sonPt.getThisHight() != null
										&& sonPt.getThisHight() > 0) {
									hudu = sonPt.getThisHight();
								} else {
									ProcardTemplate oldpt = (ProcardTemplate) totalDao
											.getObjectByCondition(
													" from  ProcardTemplate where markId = ? and g and thisLength >0 and thisWidth>0 and (banbenStatus is null or  banbenStatus != '历史') and (dataStatus is null or dataStatus!='删除')",
													sonPt.getMarkId());
									hudu = oldpt.getThisHight();
								}
								int hudu2 = (int) (float) hudu;
								String hql_panel = " from PanelSize where caizhi ='"
										+ sonPt.getClType()
										+ "' and  "
										+ hudu
										+ " BETWEEN  fristThickness AND endThickness ";
								panel = (PanelSize) totalDao
										.getObjectByCondition(hql_panel);
								if (panel != null) {
									String specification = "T" + hudu + "x"
											+ panel.getSize();
									String specification2 = null;
									String hql_wgj = null;
									if (hudu2 == hudu) {
										specification2 = "T" + hudu2 + "x"
												+ panel.getSize();
										hql_wgj = " from YuanclAndWaigj where caizhi like '%"
												+ sonPt.getClType()
												+ "%' and ( specification like '%"
												+ specification
												+ "%' or specification like '%"
												+ specification2
												+ "%') and (banbenStatus is null or  banbenStatus != '历史')";
									} else {
										hql_wgj = " from YuanclAndWaigj where caizhi like '%"
												+ sonPt.getClType()
												+ "%' and specification like '%"
												+ specification
												+ "%' and (banbenStatus is null or  banbenStatus != '历史')";
									}
									YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao
											.getObjectByCondition(hql_wgj);

									if (wgj != null
											&& !sonMarkIds.contains(wgj
													.getMarkId())) {
										if (wgj.getBanbenhao() == null
												|| wgj.getBanbenhao().length() == 0) {// 无版本号
											sqlhistory += " and (banBenNumber is null or banBenNumber='')";
										} else {
											sqlhistory += " and banBenNumber ='"
													+ wgj.getBanbenhao() + "'";
										}
										ProcardTemplate wgpt = new ProcardTemplate();
										historyPt1 = (ProcardTemplate) totalDao
												.getObjectByCondition(
														sqlhistory1, wgj
																.getMarkId());
										if (historyPt1 != null) {
											BeanUtils.copyProperties(
													historyPt1, wgpt,
													new String[] { "id",
															"procardTemplate",
															"procardTSet",
															"processTemplate",
															"zhUsers",
															"quanzi1",
															"quanzi2",
															"maxCount",
															"corrCount",
															"rootId",
															"fatherId",
															"belongLayer",
															"cardXiaoHao",
															"loadMarkId",
															"ywMarkId",
															"historyId",
															"thisLength",
															"thisWidth",
															"thisHight",
															"productStyle" });
											wgpt.setYwMarkId(ywmarkId);
											wgpt.setHistoryId(historyPt1
													.getId());
										} else {
											wgpt.setMarkId(wgj.getMarkId());// 件号
											wgpt.setWgType(wgj.getWgType());// 物料类别
											wgpt.setCaizhi(wgj.getCaizhi());// 材质
											wgpt.setProName(wgj.getName());// 名称\
											wgpt.setBanBenNumber(wgj
													.getBanbenhao());
											wgpt.setSpecification(wgj
													.getSpecification());// 规格
											wgpt.setKgliao(wgj.getKgliao());// 供料属性
											wgpt.setBili(wgj.getBili());// 单张重量
											wgpt.setUnit(wgj.getUnit());// 单位
											wgpt.setProcardStyle("外购");
										}
										wgpt.setProductStyle(sonPt
												.getProductStyle());
										wgpt.setBelongLayer(sonPt
												.getBelongLayer() + 1);// 层数
										wgpt.setFatherId(sonPt.getId());
										wgpt.setRootMarkId(sonPt
												.getRootMarkId());
										wgpt.setRootId(sonPt.getRootId());
										wgpt.setQuanzi1(1f);
										Float quanzhi2 = 0f;
										if (wgj.getDensity() == null) {
											throw new RuntimeException("外购件库中:"
													+ wgj.getMarkId()
													+ "没有填写密度!");
										}
										quanzhi2 = (sonPt.getThisHight()
												* (sonPt.getThisLength() + 12)
												* (sonPt.getThisWidth() + 12) * wgj
												.getDensity()) / 1000000;
										wgpt.setQuanzi2(quanzhi2);// 自动给加上5%的损耗
										wgpt.setSpecification(wgj
												.getSpecification());
										wgpt.setSunhao(5f);
										// wgpt.setSunhaoType("百分比");
										sonsetPt0.add(wgpt);
										wgpt.setProcardTemplate(sonPt);
										if (wgpt.getBzStatus() == null) {
											wgpt.setBzStatus("待编制");
										}
										if (!wgpt.getBzStatus().equals("已批准")
												&& !wgpt.getBzStatus().equals(
														"设变发起中")) {
											wgpt.setBianzhiName(user.getName());
											wgpt.setBianzhiId(user.getId());
										}
										wgpt.setRemark("板材粉末自动计算添加");
										if(wgpt.getKgliao()==null||wgpt.getKgliao().length()==0){
											wgpt.setKgliao("TK");
										}
										totalDao.save(wgpt);
										sonPt.setProcardTSet(sonsetPt0);
										if ("试制"
												.equals(sonPt.getProductStyle())) {
											List<ProcessTemplateFile> ptFileList = totalDao
													.query(
															"from ProcessTemplateFile where processNO is null "
																	+ " and status='默认' and markId=? and glId is null ",
															wgpt.getMarkId());
											if (ptFileList != null
													&& ptFileList.size() > 0) {
												for (ProcessTemplateFile ptFile : ptFileList) {
													ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
													BeanUtils
															.copyProperties(
																	ptFile,
																	ptFile2,
																	new String[] {
																			"id",
																			"productStyle",
																			"glId" });
													ptFile2.setGlId(wgpt
															.getId());
													ptFile2
															.setProductStyle("试制");
													totalDao.save(ptFile2);
												}
											}

										} else {
											// 横向比较之前的自制件是否存在本外购件，如果不存在则新添，存在则修改其长、宽、厚和权值2
											// 为了达到数据一致的效果
											for (ProcardTemplate zzpt : zzptList) {
												if (!zzpt.getId().equals(
														sonPt.getId())) {
													String hql_historywgpt = " from ProcardTemplate where fatherId = ? and markId = ? and (banbenStatus is null or  banbenStatus != '历史') and (dataStatus is null or dataStatus!='删除') ";
													ProcardTemplate historywgpt = (ProcardTemplate) totalDao
															.getObjectByCondition(
																	hql_historywgpt,
																	zzpt
																			.getId(),
																	wgpt
																			.getMarkId());
													if (historywgpt != null) {
														historywgpt
																.setQuanzi2(wgpt
																		.getQuanzi2());
														historywgpt
																.setThisHight(wgpt
																		.getThisHight());
														historywgpt
																.setThisLength(wgpt
																		.getThisLength());
														historywgpt
																.setThisWidth(wgpt
																		.getThisWidth());
														historywgpt
																.setSunhao(5f);
														// historywgpt.setSunhaoType("百分比");
														totalDao
																.update(historywgpt);
													} else {
														historywgpt = new ProcardTemplate();
														BeanUtils
																.copyProperties(
																		wgpt,
																		historywgpt,
																		new String[] {
																				"id",
																				"procardTemplate",
																				"procardTSet",
																				"processTemplate",
																				"zhUsers",
																				"maxCount",
																				"corrCount",
																				"rootId",
																				"fatherId",
																				"belongLayer",
																				"cardXiaoHao",
																				"loadMarkId",
																				"ywMarkId",
																				"historyId",
																				"thisLength",
																				"thisWidth",
																				"thisHight",
																				"productStyle" });

														historywgpt
																.setYwMarkId(zzpt
																		.getYwMarkId());
														historywgpt
																.setHistoryId(wgpt
																		.getHistoryId());
														historywgpt
																.setRootId(zzpt
																		.getRootId());
														historywgpt
																.setRootMarkId(zzpt
																		.getRootMarkId());
														historywgpt
																.setFatherId(zzpt
																		.getId());
														historywgpt
																.setProcardTemplate(zzpt);
														historywgpt
																.setBelongLayer(zzpt
																		.getBelongLayer() + 1);
														historywgpt
																.setProductStyle(zzpt
																		.getProductStyle());
														historywgpt
																.setSunhao(5f);
														// historywgpt.setSunhaoType("百分比");
														totalDao
																.save(historywgpt);

													}
												}
											}
										}

									} else {
										jsbcmsg += "第" + (j + 1 + base)
												+ "行错误，没有在外购件库找到" + "材质为："
												+ sonPt.getClType() + "，规格为:"
												+ specification + "的板材。 !<br/>";
									}
								} else {
									jsbcmsg += "第" + (j + 1 + base)
											+ "行错误，没有在板材尺寸库中找到" + "材质为："
											+ sonPt.getClType() + "，厚度为:"
											+ hudu + "的板材。 !<br/>";

								}
							}
							// 开始计算本层自制件有长宽的粉末
							if (sonPt.getCorrCount() != null
									&& sonPt.getCorrCount() > 0
									&& sonPt.getBiaochu() != null
									&& sonPt.getBiaochu().length() > 0) {
								Float mianji = sonPt.getThisLength()
										* sonPt.getThisWidth();
								if (mianji > 0) {
									// 查询粉末表处对照关系
									String biaochu = (String) totalDao
											.getObjectByCondition(
													" select valueCode from CodeTranslation where type = '粉末' and keyCode =? ",
													sonPt.getBiaochu());
									if (biaochu != null && biaochu.length() > 0) {
										PanelSize fuhefenmo = (PanelSize) totalDao
												.getObjectByCondition(
														" from PanelSize where caizhi= ? and type = '复合粉末'",
														biaochu);
										if (fuhefenmo != null) {
											String hql_fenmowgj1 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo1()
													+ "%' and wgType = '粉末' and (banbenStatus is null or  banbenStatus != '历史') ";
											if (fuhefenmo.getFenmo1().indexOf(
													"华彩") == -1) {
												hql_fenmowgj1 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj1 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj1);
											if (fenmowgj1 != null) {
												// 复合粉末1
												fenmojisuan(
														fenmowgj1,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount1(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，" + "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo1()
														+ "的复合粉末1 !<br/>";
											}
											String hql_fenmowgj2 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo2()
													+ "%' and wgType = '粉末'  and (banbenStatus is null or  banbenStatus != '历史') ";
											if (fuhefenmo.getFenmo2().indexOf(
													"华彩") == -1) {
												hql_fenmowgj2 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj2 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj2);
											if (fenmowgj2 != null) {
												// 复合粉末2
												fenmojisuan(
														fenmowgj2,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount2(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第"
														+ (j + 1 + base)
														+ "行错误，"
														+ "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo2()
														+ "的复合粉末2 !<br/>&bcfmjs&";
											}

										} else {
											String hql_fenmowgj = " from  YuanclAndWaigj where name like '%"
													+ biaochu
													+ "%' and wgType = '粉末'  and (banbenStatus is null or  banbenStatus != '历史') ";
											YuanclAndWaigj fenmowgj = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj);
											if (fenmowgj != null) {
												// 粉末
												fenmojisuan(fenmowgj,
														sonMarkIds, sqlhistory,
														historyPtother1,
														sqlhistory1, ywmarkId,
														sonPt, mianji, 2,
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，"
														+ "即没有在复合粉末库中找到材质为:"
														+ sonPt.getBiaochu()
														+ "的复合粉末。"
														+ "也没有在外购件库中找到" + "表处为"
														+ biaochu
														+ "的粉末 !<br/>";
											}
										}
									} else {
										jsbcmsg += "第" + (j + 1 + base)
												+ "行错误，" + "未找到"
												+ sonPt.getBiaochu()
												+ "的对应关系表处<br/>";
									}
								}
							}

						} else {
							// 开始计算自制件本身没有长宽的但其下层自制件有长宽的 粉末;
							if (sonPt.getBiaochu() != null
									&& sonPt.getBiaochu().length() > 0) {
								Float briefnum = 1f;// 所有上阶层消耗数;
								Integer brieBelongLayer = sonPt
										.getBelongLayer();
								Float mianji = 0f;
								DecimalFormat df1 = new DecimalFormat(
										"###,###,###,###,###");
								for (int m = j + 1; m < ptList.size(); m++) {
									ProcardTemplate pt = ptList.get(m);
									if (pt.getCorrCount() == null) {
										continue;
									}
									if (pt.getBelongLayer() == sonPt
											.getBelongLayer()) {// 当层数和
										break;
									} else if ("自制"
											.equals(pt.getProcardStyle())) {
										if (pt.getThisHight() != null
												&& pt.getThisHight() > 0
												&& pt.getThisWidth() != null
												&& pt.getThisWidth() > 0
												&& pt.getThisLength() != null
												&& pt.getThisLength() > 0) {
											if (brieBelongLayer >= pt
													.getBelongLayer()) {// 如果跳出本层回归到上层让上阶层消耗数归1；
												briefnum = 1f;// 重新归1；
											}
											Float aa = briefnum
													* pt.getThisLength()
													* pt.getThisWidth()
													* pt.getCorrCount();
											mianji += aa;
										} else {
											ProcardTemplate oldpt = (ProcardTemplate) totalDao
													.getObjectByCondition(
															" from ProcardTemplate where markId = ? and thisHight>0 and thisLength >0 and thisWidth>0  and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')",
															pt.getMarkId());
											if (oldpt != null
													&& oldpt.getThisWidth() != null
													&& oldpt.getThisWidth() > 0
													&& oldpt.getThisHight() != null
													&& oldpt.getThisHight() > 0
													&& oldpt.getThisLength() != null
													&& oldpt.getThisLength() > 0) {
												Float aa = briefnum
														* oldpt.getThisLength()
														* oldpt.getThisWidth()
														* oldpt.getCorrCount();
												mianji += aa;
											} else {
												if (pt.getBelongLayer() != null
														&& pt.getBelongLayer()
																- brieBelongLayer == 1) {
													briefnum = briefnum
															* pt.getCorrCount();
												} else {
													briefnum = 1 * pt
															.getCorrCount();
												}
												brieBelongLayer = pt
														.getBelongLayer();
											}
										}
									}
								}
								if (mianji > 0) {
									// 查询粉末表处对照关系
									String biaochu = (String) totalDao
											.getObjectByCondition(
													" select valueCode from CodeTranslation where type = '粉末' and keyCode =? ",
													sonPt.getBiaochu());
									if (biaochu != null && biaochu.length() > 0) {
										PanelSize fuhefenmo = (PanelSize) totalDao
												.getObjectByCondition(
														" from PanelSize where caizhi= ? and type = '复合粉末'",
														biaochu);
										if (fuhefenmo != null) {
											String hql_fenmowgj1 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo1()
													+ "%' and wgType = '粉末' and (banbenStatus is null or banbenStatus!='历史')";
											if (fuhefenmo.getFenmo1().indexOf(
													"华彩") == -1) {
												hql_fenmowgj1 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj1 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj1);
											if (fenmowgj1 != null) {
												// 复合粉末1
												fenmojisuan(
														fenmowgj1,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount1(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，" + "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo1()
														+ "的复合粉末1 !<br/>";
											}
											String hql_fenmowgj2 = " from  YuanclAndWaigj where name like '%"
													+ fuhefenmo.getFenmo2()
													+ "%' and wgType = '粉末' and (banbenStatus is null or banbenStatus!='历史')";
											if (fuhefenmo.getFenmo2().indexOf(
													"华彩") == -1) {
												hql_fenmowgj2 += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj2 = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj2);
											if (fenmowgj2 != null) {
												// 复合粉末2
												fenmojisuan(
														fenmowgj2,
														sonMarkIds,
														sqlhistory,
														historyPtother1,
														sqlhistory1,
														ywmarkId,
														sonPt,
														mianji,
														fuhefenmo
																.getMiancount2(),
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，" + "没有在外购件库中找到"
														+ "表处为"
														+ fuhefenmo.getFenmo2()
														+ "的复合粉末2 !<br/>";
											}
										} else {
											String hql_fenmowgj = " from  YuanclAndWaigj where name like '%"
													+ biaochu
													+ "%' and wgType = '粉末' and (banbenStatus is null or banbenStatus!='历史')";
											if (biaochu.indexOf("华彩") == -1) {
												hql_fenmowgj += " and name not like '%华彩%'";
											}
											YuanclAndWaigj fenmowgj = (YuanclAndWaigj) totalDao
													.getObjectByCondition(hql_fenmowgj);
											if (fenmowgj != null) {
												// 粉末
												fenmojisuan(fenmowgj,
														sonMarkIds, sqlhistory,
														historyPtother1,
														sqlhistory1, ywmarkId,
														sonPt, mianji, 2,
														sonsetPt0, user);
											} else {
												jsbcmsg += "第" + (j + 1 + base)
														+ "行错误，"
														+ "即没有在复合粉末库中找到材质为:"
														+ sonPt.getBiaochu()
														+ "的复合粉末。"
														+ "也没有在外购件库中找到" + "表处为"
														+ sonPt.getBiaochu()
														+ "的粉末 !<br/>";
											}
										}
									} else {
										jsbcmsg += "第" + (j + 1 + base)
												+ "行错误，" + "未找到"
												+ sonPt.getBiaochu()
												+ "的对应关系表处<br/>";
									}
								}
							}
						}
					}
				}
			} else if (sonPt.getBelongLayer() > (fatherPt.getBelongLayer() + 1)) {
				continue;
			} else {
				break;
			}
		}
		if (sonSet.size() > 0 && !fatherPt.getProcardStyle().equals("总成")) {
			if (fatherPt.getProcardStyle().equals("外购")) {
				msg += "第" + (i + base) + "行错误,外购件" + fatherPt.getMarkId()
						+ "下不能有零件!<br/>";
			}
		}
		fatherPt.setProcardTSet(sonSet);
		totalDao.update(fatherPt);
		return msg;
	}

	private String compareBanben(ProcardTemplate sonPt,
			List<ProcardTemplate> aboutptList2, String ywmarkId, Users user,
			int count) {
		// TODO Auto-generated method stub
		if (aboutptList2 != null && aboutptList2.size() > 0) {
			ProcardTemplate pc = null;// 批产
			ProcardTemplate sz = null;// 试制
			ProcardTemplate ls = null;// 历史版次
			ProcardTemplate high = null;// 最高版本
			boolean checkmakrId = false;
			int i = 0;
			for (ProcardTemplate old : aboutptList2) {
				if (old.getMarkId().equalsIgnoreCase(sonPt.getMarkId())) {
					i++;
					if (i == 1) {
						if (old.getTuhao() != null
								&& (old.getTuhao().startsWith("DKBA0") || old
										.getTuhao().startsWith("dkba0"))) {
							sonPt.setBanBenNumber(null);
						}
					}
					checkmakrId = true;
					if ((old.getBanbenStatus() == null || !old
							.getBanbenStatus().equals("历史"))
							&& (old.getDataStatus() == null || !old
									.getDataStatus().equals("删除"))) {
						if (sonPt.getUnit() != null
								&& sonPt.getUnit().length() > 0) {
							if (!Util.isEquals(old.getUnit(), sonPt.getUnit())) {
								return "第" + count + "行,零件" + sonPt.getMarkId()
										+ "的单位为:" + sonPt.getUnit() + "与现有的单位:"
										+ old.getUnit() + "的单位不一致!<br/>";
							}
						}
						if (old.getBzStatus() != null
								&& old.getBzStatus().equals("禁用")) {
							if (Util.isEquals(old.getBanBenNumber(), sonPt
									.getBanBenNumber())) {
								if (old.getProcardStyle().equals("外购")
										&& Util.isEquals(old.getKgliao(), sonPt
												.getKgliao())) {
									return "第" + count + "行"
											+ sonPt.getMarkId() + "--此件号的"
											+ old.getBanBenNumber() + "版本的"
											+ old.getKgliao() + "供料属性已禁用<br/>";
								} else {
									return "第" + count + "行"
											+ sonPt.getMarkId()
											+ "--此件号版本已禁用<br/>";
								}
							}
						} else {
							if (old.getProductStyle().equals("批产")) {
								if (pc == null) {
									pc = old;
								}
							} else {
								if (sz == null
										&& Util.isEquals(old.getBanBenNumber(),
												sonPt.getBanBenNumber())) {
									sz = old;
								}
							}
							if (high == null) {
								high = old;
							} else {
								int cjCount = Banbenxuhao.comparebanben(old
										.getBanBenNumber(), high
										.getBanBenNumber(), banBenxuhaoMap);
								if (cjCount > 0 && cjCount < 5) {
									high = old;
								}

							}
						}

					}
					if (ls != null) {
						Integer banci1 = old.getBanci();
						Integer banci2 = ls.getBanci();
						if (banci1 == null) {
							banci1 = 0;
						}
						if (banci2 == null) {
							banci2 = 0;
						}
						if (banci1 > banci2) {
							ls = old;
						}
					} else {
						ls = old;
					}

				} else if (checkmakrId) {
					break;
				}
			}
			boolean szhad = false;
			if (sonPt.getProductStyle().equals("试制")) {
				if (high != null) {
					int cjCount = Banbenxuhao.comparebanben(sonPt
							.getBanBenNumber(), high.getBanBenNumber(),
							banBenxuhaoMap);
					if (cjCount < 0) {
						return "第" + count + "行" + sonPt.getMarkId()
								+ "--此件号版本低于系统BOM中的版本(系统批产版本为:"
								+ high.getBanBenNumber() + "，导入版本为:"
								+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
					}
					szhad = true;
				}
			}
			if (pc != null) {// 有批产数据
				if (!isEquals(pc.getBanBenNumber(), sonPt.getBanBenNumber())) {
					if (!szhad) {
						return "第" + count + "行" + sonPt.getMarkId()
								+ "--此件号版本与系统批产BOM中的版本不一致(系统批产版本为:"
								+ pc.getBanBenNumber() + "，导入版本为:"
								+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
					} else {
						int cjCount = Banbenxuhao.comparebanben(sonPt
								.getBanBenNumber(), pc.getBanBenNumber(),
								banBenxuhaoMap);
						if (cjCount < 0) {
							return "第" + count + "行" + sonPt.getMarkId()
									+ "--此件号版本低于系统BOM中的版本(系统批产版本为:"
									+ pc.getBanBenNumber() + "，导入版本为:"
									+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
						}
					}
				} else {
					copyjcMsg(pc, sonPt, ywmarkId, 0, user);
					sonPt.setHistoryId(pc.getId());
					return "";
				}
			}
			if (sonPt.getProductStyle().equals("试制")) {
				if (sz != null) {
					copyjcMsg(sz, sonPt, ywmarkId, 0, user);
					sonPt.setHistoryId(sz.getId());
					if (sz.getTuhao() != null
							&& (sz.getTuhao().startsWith("DKBA0") || sz
									.getTuhao().startsWith("dkba0"))) {
						sonPt.setBanBenNumber(null);
					}
					return "";
				} else if (high != null) {
					int cjCount = Banbenxuhao.comparebanben(sonPt
							.getBanBenNumber(), high.getBanBenNumber(),
							banBenxuhaoMap);
					if (cjCount > 0 && cjCount < 5) {// 导入了新版本
						sonPt.setProName(high.getProName());
						sonPt.setSpecification(high.getSpecification());
						sonPt.setWgType(high.getWgType());
						sonPt.setTuhao(high.getTuhao());
						return "";
					} else {
						return "第" + count + "行" + sonPt.getMarkId()
								+ "--此件号版本与系统试制BOM中的版本不一致(系统批产版本为:"
								+ high.getBanBenNumber() + "，导入版本为:"
								+ sonPt.getBanBenNumber() + ")，请先处理!<br/>";
					}
				}
			}
			if (ls != null) {
				sonPt.setBanci(ls.getBanci());
			} else {
				sonPt.setBanci(0);
			}

		}
		return "";
		// ProcardTemplate historyPt = null;// 查看该件号批产在使用的版次
		// ProcardTemplate historyPt2 = null;// 查看该件号试制在使用的版次
		//
		// ProcardTemplate samebanbenPt = null;// 查看该件号同版本最高版次
		// ProcardTemplate historyPtother = null;// 查看该件号最高版次
		// if (aboutptList != null && aboutptList.size() > 0) {
		// boolean checkmakrId = false;
		// for (ProcardTemplate aboutpt : aboutptList) {
		// if (aboutpt.getMarkId().equalsIgnoreCase(
		// sonPt.getMarkId())) {
		// checkmakrId = true;
		// if (isEquals(aboutpt.getBanBenNumber(), sonPt
		// .getBanBenNumber())) {
		// if (samebanbenPt != null) {
		// Integer banci1 = aboutpt.getBanci();
		// Integer banci2 = samebanbenPt.getBanci();
		// if (banci1 == null) {
		// banci1 = 0;
		// }
		// if (banci2 == null) {
		// banci2 = 0;
		// }
		// if (banci1 > banci2) {
		// samebanbenPt = aboutpt;
		// }
		// } else {
		// samebanbenPt = aboutpt;
		// }
		// }
		// if ((aboutpt.getBanbenStatus() == null || !aboutpt
		// .getBanbenStatus().equals("历史"))
		// && (aboutpt.getDataStatus() == null || !aboutpt
		// .getDataStatus().equals("删除"))) {
		// if (aboutpt.getProductStyle().equals("批产")) {
		// if (historyPt == null) {
		// historyPt = aboutpt;
		// }
		// } else {
		// if (historyPt2 == null
		// && Banbenxuhao.comparebanben(
		// aboutpt.getBanBenNumber(),
		// sonPt.getBanBenNumber(),
		// banBenxuhaoMap) == 0) {
		// historyPt2 = aboutpt;
		// }
		//
		// }
		// } else {
		// if (historyPtother != null) {
		// Integer banci1 = aboutpt.getBanci();
		// Integer banci2 = historyPtother.getBanci();
		// if (banci1 == null) {
		// banci1 = 0;
		// }
		// if (banci2 == null) {
		// banci2 = 0;
		// }
		// if (banci1 > banci2) {
		// historyPtother = aboutpt;
		// }
		// } else {
		// historyPtother = aboutpt;
		// }
		// }
		// } else if (checkmakrId) {
		// break;
		// }
		// }
		// }
		// if (historyPt != null) {
		// if (sonPt.getProductStyle().equals("试制")
		// && Banbenxuhao.comparebanben(sonPt
		// .getBanBenNumber(), historyPt
		// .getBanBenNumber(), banBenxuhaoMap) < 0) {
		// msg += "第" + (j + 1 + base) + "行" + sonPt.getMarkId()
		// + "--此件号版本低于系统批产BOM中的版本(系统批产版本为:"
		// + historyPt.getBanBenNumber() + "，导入版本为:"
		// + sonPt.getBanBenNumber() + ")，请先处理!<br/>";
		// } else if (!Util.isEquals(historyPt.getBanBenNumber(),
		// sonPt.getBanBenNumber())) {
		// // if (sonPt.getBanBenNumber() == null
		// // || sonPt.getBanBenNumber().length() == 0) {// 无版本号
		// // sonPt.setBanBenNumber("空");
		// // }
		// if (!"试制".equals(sonPt.getProductStyle())) {
		// msg += "第" + (j + 1 + base) + "行"
		// + sonPt.getMarkId()
		// + "--此件号与系统BOM中版本不一致(系统版本为:"
		// + historyPt.getBanBenNumber() + "，导入版本为:"
		// + sonPt.getBanBenNumber() + ")，请先处理!<br/>";
		// } else {
		// if (historyPt2 != null
		// && sonPt.getProductStyle().equals("试制")) {// 含有同版本号的试制正式零件
		// copyjcMsg(historyPt2, sonPt, ywmarkId, 0, user);
		// sonPt.setProductStyle("试制");
		// } else {
		// copyjcMsg(historyPt, sonPt, ywmarkId, 0, user);
		// }
		// }
		// } else {
		// isHistorySon = true;// 已有
		// copyjcMsg(historyPt, sonPt, ywmarkId, 1, user);
		// sonPt.setHistoryId(historyPt.getId());
		// }
		// } else if (historyPt2 != null
		// && sonPt.getProductStyle().equals("试制")) {// 含有同版本号的试制正式零件
		// isHistorySon = true;// 已有
		// copyjcMsg(historyPt2, sonPt, ywmarkId, 1, user);
		// sonPt.setHistoryId(historyPt2.getId());
		// } else if (samebanbenPt != null
		// && sonPt.getProductStyle().equals("试制")) {// 含有同版本号的非正式零件
		// copyjcMsg(samebanbenPt, sonPt, ywmarkId, 1, user);
		//
		// } else if (historyPtother != null) {
		// sonPt.setProName(historyPtother.getProName());
		// if (sonPt.getProcardStyle().equals("待定")) {
		// if (historyPtother.getProcardStyle().equals("总成")) {
		// sonPt.setProcardStyle("自制");
		// } else {
		// sonPt.setProcardStyle(historyPtother
		// .getProcardStyle());
		// }
		// }
		// sonPt.setBanci(historyPtother.getBanci());
		// } else {
		// // 查询已删除或者历史数据中同版本最高版次的数据
		// sonPt.setBanci(0);
		// }
	}

	private void copyjcMsg(ProcardTemplate historyPt, ProcardTemplate sonPt,
			String ywmarkId, Integer youxiao, Users user) {
		try {
			// TODO Auto-generated method stub
			BeanUtils.copyProperties(historyPt, sonPt, new String[] { "id",
					"procardTemplate", "procardTSet", "processTemplate",
					"zhUsers", "quanzi1", "quanzi2", "maxCount", "corrCount",
					"rootId", "fatherId", "belongLayer", "cardXiaoHao",
					"loadMarkId", "ywMarkId", "historyId", "kgliao",
					"thisLength", "thisWidth", "thisHight", "biaochu",
					"productStyle", "bzStatus", "procardStyle", "sunhao",
					"sunhaoType", "banBenNumber", "lingliaostatus", "wgType" });
			if ((historyPt.getBanbenStatus() == null || !"历史".equals(historyPt
					.getBzStatus()))
					&& (historyPt.getDataStatus() == null || !historyPt
							.getDataStatus().equals("删除"))) {
				sonPt.setBzStatus(historyPt.getBzStatus());
			} else {
				sonPt.setBzStatus("待编制");
				sonPt.setBianzhiId(user.getId());
				sonPt.setBianzhiName(user.getName());
			}
			if (sonPt.getProcardStyle().equals("待定")) {
				if (historyPt.getProcardStyle().equals("总成")) {
					sonPt.setProcardStyle("自制");
				} else {
					sonPt.setProcardStyle(historyPt.getProcardStyle());
				}
			}
			if ("外购".equals(sonPt.getProcardStyle())) {
				sonPt.setWgType(historyPt.getWgType());
			}

			sonPt.setYwMarkId(ywmarkId);
			sonPt.setHistoryId(historyPt.getId());
			if (youxiao == 1 && historyPt.getBanbenStatus() != null
					&& historyPt.getBanbenStatus().equals("已批准")) {
				if (sonPt.getProcardStyle() != null
						&& !sonPt.getProcardStyle().equals("外购")) {// 非外购件连带长宽高
					sonPt.setQuanzi1(historyPt.getQuanzi1());
					sonPt.setQuanzi2(historyPt.getQuanzi2());
					sonPt.setThisLength(historyPt.getThisLength());
					sonPt.setThisWidth(historyPt.getThisWidth());
					sonPt.setThisHight(historyPt.getThisHight());
				}
			}
			if (historyPt.getProcardStyle().equals("总成")) {
				sonPt.setProcardStyle("自制");
			}
			String banciSql = null;
			if (historyPt.getBanci() == null || historyPt.getBanci() == 0) {
				banciSql = "and (banci is null or banci=0)";
			} else {
				banciSql = " and banci=" + historyPt.getBanci();
			}
			// 同步工序
			Set<ProcessTemplate> processSet1 = historyPt.getProcessTemplate();
			Set<ProcessTemplate> processSet2 = new HashSet<ProcessTemplate>();
			if (processSet1 != null) {// 添加在添加列表中存在的工序号的工序
				for (ProcessTemplate process1 : processSet1) {
					if (process1.getProcessNO() != null) {
						ProcessTemplate process2 = new ProcessTemplate();
						// -----------辅料开始----------
						if (process1.getIsNeedFuliao() != null
								&& process1.getIsNeedFuliao().equals("yes")) {
							Set<ProcessFuLiaoTemplate> fltmpSet = process1
									.getProcessFuLiaoTemplate();
							if (fltmpSet.size() > 0) {
								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
									BeanUtils.copyProperties(fltmp, pinfoFl,
											new String[] { "id",
													"processTemplate" });
									if (pinfoFl.getQuanzhi1() == null) {
										pinfoFl.setQuanzhi1(1f);
									}
									if (pinfoFl.getQuanzhi2() == null) {
										pinfoFl.setQuanzhi2(1f);
									}
									pinfoFl.setProcessTemplate(process2);
									pinfoFlSet.add(pinfoFl);
								}
								process2.setProcessFuLiaoTemplate(pinfoFlSet);
							}
						}
						// -----------辅料结束----------
						BeanUtils
								.copyProperties(process1, process2,
										new String[] { "id", "procardTemplate",
												"taSopGongwei",
												"processFuLiaoTemplate" });
						process2.setProcardTemplate(sonPt);
						totalDao.save(process2);
						processSet2.add(process2);
						if ("试制".equals(sonPt.getProductStyle())) {
							List<ProcessTemplateFile> ptFileList = null;
							if ("试制".equals(historyPt.getProductStyle())) {
								ptFileList = totalDao.query(
										"from ProcessTemplateFile where processNO =? "
												+ banciSql + " and glId=? ",
										process2.getProcessNO(), process1
												.getId());
							} else {
								ptFileList = totalDao
										.query(
												"from ProcessTemplateFile where processNO =? "
														+ "  and markId=? and productStyle='批产' "
														+ banciSql, process2
														.getProcessNO(), sonPt
														.getMarkId());
							}
							if (ptFileList != null && ptFileList.size() > 0) {
								for (ProcessTemplateFile ptFile : ptFileList) {
									if (ptFile != null) {
										ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
										BeanUtils
												.copyProperties(ptFile,
														ptFile2, new String[] {
																"id",
																"productStyle",
																"glId" });
										ptFile2.setGlId(process2.getId());
										ptFile2.setProductStyle("试制");
										totalDao.save(ptFile2);
									}
								}
							}
						}

					}
				}
			}
			if ("试制".equals(sonPt.getProductStyle())) {
				if (sonPt.getId() == null) {
					totalDao.save(sonPt);
				}
				List<ProcessTemplateFile> ptFileList = null;
				if ("试制".equals(historyPt.getProductStyle())) {
					ptFileList = totalDao.query(
							"from ProcessTemplateFile where processNO is null "
									+ banciSql + " and glId=? ", historyPt
									.getId());
				} else {
					ptFileList = totalDao.query(
							"from ProcessTemplateFile where processNO is null "
									+ "  and markId=? and productStyle='批产' "
									+ banciSql, sonPt.getMarkId());
				}
				if (ptFileList != null && ptFileList.size() > 0) {
					for (ProcessTemplateFile ptFile : ptFileList) {
						if (ptFile != null) {
							ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
							BeanUtils
									.copyProperties(ptFile, ptFile2,
											new String[] { "id",
													"productStyle", "glId" });
							ptFile2.setGlId(sonPt.getId());
							ptFile2.setProductStyle("试制");
							totalDao.save(ptFile2);
						}
					}
				}
			}

			sonPt.setProcessTemplate(processSet2);
		} catch (BeansException e) {
			System.out.println(historyPt.getId()
					+ " ---------------------------");
			e.printStackTrace();
		}
	}

	@Override
	public boolean updateProcessNameOfFile() {
		// TODO Auto-generated method stub
		List<ProcessTemplateFile> files = totalDao
				.query("from ProcessTemplateFile where processNO is not null   and status='默认'");
		if (files.size() > 0) {
			for (ProcessTemplateFile file : files) {
				if (file.getMarkId() != null && file.getMarkId().length() > 0) {
					String processName = (String) totalDao
							.getObjectByCondition(
									"select processName from ProcessTemplate where procardTemplate.markId=? and processNO=?",
									file.getMarkId(), file.getProcessNO());
					if (processName != null && processName.length() > 0) {
						file.setProcessName(processName);
						totalDao.update(file);
					}
				}
			}
		}
		return true;
	}

	@Override
	public String getBzStatus(Integer id) {
		// TODO Auto-generated method stub
		return (String) totalDao.getObjectByCondition(
				"select bzStatus from ProcardTemplate where id =?", id);
	}

	@Override
	public List<ProcardTemplate> showToUpdatebanben(String ids) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> ptList = totalDao
				.query("from ProcardTemplate where id in(" + ids + ")");
		return ptList;
	}

	@Override
	public String applyUpdateBan(List<ProcardTemplate> procardTemplateList,
			String remark) {
		// TODO Auto-generated method stub
		if (procardTemplateList.size() > 0) {
			Users user = Util.getLoginUser();
			ProcardTemplateBanBenApply bbApply = new ProcardTemplateBanBenApply();
			bbApply.setRemark(remark);
			bbApply.setApplicantId(user.getId());
			bbApply.setApplicantName(user.getName());
			bbApply.setApplyTime(Util.getDateTime());
			Set<ProcardTemplateBanBen> bbSet = new HashSet<ProcardTemplateBanBen>();
			for (ProcardTemplate pt : procardTemplateList) {
				ProcardTemplateBanBen bb = new ProcardTemplateBanBen();
				ProcardTemplate old = (ProcardTemplate) totalDao.getObjectById(
						ProcardTemplate.class, pt.getId());
				BeanUtils.copyProperties(old, bb, new String[] { "id" });
				if (bb.getBanci() == null) {
					bb.setBanci(0);
				}
				bb.setNewbanci(bb.getBanci() + 1);
				bb.setPtId(pt.getId());
				bb.setProcardTemplateBanBenApply(bbApply);
				bb.setNewBanBenNumber(pt.getNewBanBenNumber());
				bbSet.add(bb);
			}
			// bbApply.setProcardTemplateBanBen(bbSet);
			totalDao.save(bbApply);
			Integer epId = 0;
			try {
				epId = CircuitRunServerImpl.createProcess("BOM模板版本升级",
						ProcardTemplateBanBenApply.class, bbApply.getId(),
						"status", "id",
						"procardTemplateGyAction_bbApplydetail.action?id="
								+ bbApply.getId(), "BOM模板版本升级申请审批", true);
				if (epId != null) {
					bbApply.setEpId(epId);
					// bbApply.setStatus("未审批");
					return totalDao.update(bbApply) + "";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("审批流程异常，请联系管理员!");
			}

		}
		return "没有申请的模板,申请失败!";
	}

	@Override
	public Map<Integer, Object> findBbAplyListByCondition(
			ProcardTemplateBanBenApply bbAply, int pageNo, int pageSize,
			String startTime, String endTime, String pageStatus) {
		// TODO Auto-generated method stub
		if (bbAply == null) {
			bbAply = new ProcardTemplateBanBenApply();
		}
		String hql = totalDao.criteriaQueries(bbAply, null, null);
		if (endTime == null || endTime.equals("")) {
			endTime = Util.getDateTime();
		}
		if (startTime != null && !startTime.equals("")) {
			hql = hql + " and applyTime>='" + startTime + "'";
		}
		hql = hql + " and applyTime<='" + endTime + "'";
		if (pageStatus != null && pageStatus.equals("all")) {
		} else {
			Users use = Util.getLoginUser();
			hql = hql + " and applicantName='" + use.getName() + "'";
		}
		hql = hql + " order by id desc";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public ProcardTemplateBanBenApply getBBApplyById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardTemplateBanBenApply) totalDao.getObjectById(
				ProcardTemplateBanBenApply.class, id);
	}

	@Override
	public List<ProcardTemplateBanBen> findptBBListByBBApplyId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao
				.query(
						"from ProcardTemplateBanBen where procardTemplateBanBenApply.id=?",
						id);
	}

	@Override
	public Map<Integer, Object> findPtbbRelationListByCondition(
			ProcardTBanbenRelation ptbbRelation, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (ptbbRelation == null) {
			ptbbRelation = new ProcardTBanbenRelation();
		}
		String hql = totalDao.criteriaQueries(ptbbRelation, null, null);
		hql = hql + " order by id desc";
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public void findDaoChuBom1(String pageStatus) {
		// TODO Auto-generated method stub
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			response
					.setHeader("Content-disposition", "attachment; filename="
							+ new String("所有bom".getBytes("GB2312"), "8859_1")
							+ ".xls");
			response.setContentType("application/msexcel");
			WritableSheet ws = wwb.createSheet("Bom明细", 0);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);
			wc.setAlignment(Alignment.LEFT);

			ws.addCell(new jxl.write.Label(0, 2, "层次", wc));
			ws.addCell(new jxl.write.Label(1, 2, "物料名称", wc));
			ws.addCell(new jxl.write.Label(2, 2, "规格型号", wc));
			ws.addCell(new jxl.write.Label(3, 2, "物料属性", wc));
			ws.addCell(new jxl.write.Label(4, 2, "件号", wc));
			ws.addCell(new jxl.write.Label(5, 2, "图号", wc));
			ws.addCell(new jxl.write.Label(6, 2, "单位", wc));
			ws.addCell(new jxl.write.Label(7, 2, "单位用量", wc));
			ws.addCell(new jxl.write.Label(8, 2, "单台用量(导入前删除)", wc));
			ws.addCell(new jxl.write.Label(9, 2, "版本", wc));
			ws.addCell(new jxl.write.Label(10, 2, "比重", wc));
			ws.addCell(new jxl.write.Label(11, 2, "表处", wc));
			ws.addCell(new jxl.write.Label(12, 2, "材料类别", wc));
			ws.addCell(new jxl.write.Label(13, 2, "初始总成", wc));
			ws.addCell(new jxl.write.Label(14, 2, "供料属性", wc));
			ws.addCell(new jxl.write.Label(15, 2, "外购属性", wc));
			ws.addCell(new jxl.write.Label(16, 2, "已有原图", wc));
			ws.addCell(new jxl.write.Label(17, 2, "已有展开图", wc));
			ws.addCell(new jxl.write.Label(18, 2, "已有工艺卡", wc));
			ws.addCell(new jxl.write.Label(19, 2, "已有工序", wc));
			ws.addCell(new jxl.write.Label(25, 2, "编制状态", wc));
			int i = 3;
			List<ProcardTemplate> list = totalDao
					.query("from ProcardTemplate where procardStyle = '总成'");
			for (ProcardTemplate procardTemplate : list) {
				if (procardTemplate != null) {
					ProcardTemplate totalCard = procardTemplate;
					String name = totalCard.getYwMarkId();
					if (name == null || name.length() == 0) {
						name = totalCard.getMarkId();
					}
					totalCard.setXiaohaoCount(1f);
					i = daochuBom2(totalCard, i, ws, wc, pageStatus);
					i++;
				}
			}
			// ws.addCell(new jxl.write.Label(0, 0, "物料代码", wc));
			// ws.addCell(new jxl.write.Label(1, 0, "物料名称", wc));
			// ws.addCell(new jxl.write.Label(2, 0, "规格型号", wc));
			// ws.addCell(new jxl.write.Label(3, 0, "物料属性", wc));
			// ws.addCell(new jxl.write.Label(4, 0, "单位", wc));
			// ws.addCell(new jxl.write.Label(5, 0, "图号", wc));
			// ws.addCell(new jxl.write.Label(6, 0, "建立人员", wc));
			// ws.addCell(new jxl.write.Label(7, 0, "建立日期", wc));
			// ws.addCell(new jxl.write.Label(8, 0, "使用人员", wc));
			//			
			// ws.addCell(new jxl.write.Label(0, 1, totalCard.getYwMarkId(),
			// wc));
			// ws.addCell(new jxl.write.Label(1, 1, totalCard.getProName(),
			// wc));
			// ws.addCell(new jxl.write.Label(2, 1,
			// totalCard.getSpecification(),
			// wc));
			// ws.addCell(new jxl.write.Label(3, 1, totalCard.getProcardStyle(),
			// wc));
			// ws.addCell(new jxl.write.Label(4, 1, totalCard.getUnit(), wc));
			// ws.addCell(new jxl.write.Label(5, 1, totalCard.getYwMarkId(),
			// wc));
			// ws.addCell(new jxl.write.Label(6, 1,
			// totalCard.getCreateUserName(),
			// wc));
			// ws
			// .addCell(new jxl.write.Label(7, 1, totalCard
			// .getCreateDate(), wc));
			// ws.addCell(new jxl.write.Label(8, 1,
			// totalCard.getCreateUserName(),
			// wc));
			// ws.addCell(new jxl.write.Label(0, 2, "层次", wc));
			// ws.addCell(new jxl.write.Label(1, 2, "物料名称", wc));
			// ws.addCell(new jxl.write.Label(2, 2, "规格型号", wc));
			// ws.addCell(new jxl.write.Label(3, 2, "物料属性", wc));
			// ws.addCell(new jxl.write.Label(4, 2, "件号", wc));
			// ws.addCell(new jxl.write.Label(5, 2, "图号", wc));
			// ws.addCell(new jxl.write.Label(6, 2, "单位", wc));
			// ws.addCell(new jxl.write.Label(7, 2, "单位用量", wc));
			// ws.addCell(new jxl.write.Label(8, 2, "单台用量(导入前删除)", wc));
			// ws.addCell(new jxl.write.Label(9, 2, "版本", wc));
			// ws.addCell(new jxl.write.Label(10, 2, "比重", wc));
			// ws.addCell(new jxl.write.Label(11, 2, "表处", wc));
			// ws.addCell(new jxl.write.Label(12, 2, "材料类别", wc));
			// ws.addCell(new jxl.write.Label(13, 2, "初始总成", wc));
			// ws.addCell(new jxl.write.Label(14, 2, "供料属性", wc));
			// ws.addCell(new jxl.write.Label(15, 2, "外购属性", wc));
			// ws.addCell(new jxl.write.Label(16, 2, "已有原图", wc));
			// ws.addCell(new jxl.write.Label(17, 2, "已有展开图", wc));
			// ws.addCell(new jxl.write.Label(18, 2, "已有工艺卡", wc));
			// ws.addCell(new jxl.write.Label(19, 2, "已有工序", wc));
			//			
			// totalCard.setXiaohaoCount(1f);
			// daochuBom2(totalCard, 3, ws, wc, pageStatus);
			// if ("price".equals(pageStatus)) {
			// Util.FomartFloat(sumprice, 4);
			// ws.addCell(new jxl.write.Label(9, 1, sumprice + "", wc));
			// }
			// for (int i = 0; i < list.size(); i++) {
			// Oabusiness o = (Oabusiness) list.get(i);
			// ws.addCell(new jxl.write.Number(0, i + 4, i + 1, wc));
			// ws.addCell(new Label(1, i + 4, o.getOaproductnumber(), wc));
			// ws.addCell(new Label(2, i + 4, o.getOaproductName(), wc));
			// ws.addCell(new Label(3, i + 4, o.getOaspecification(), wc));
			// ws.addCell(new Label(4, i + 4, o.getOaunit(), wc));
			// ws.addCell(new Label(5, i + 4, String.format("%.2f", o
			// .getOaquantity()), wc));
			// // 单价
			// ws.addCell(new Label(6, i + 4, String.format("%.2f", o
			// .getOaunitprice()), wc));
			// ws.addCell(new Label(7, i + 4, String.format("%.2f", o
			// .getOatotalMon()), wc));
			// ws.addCell(new Label(8, i + 4, o.getOastatus(), wc));
			// ws.addCell(new Label(9, i + 4, o.getOausername(), wc));
			// ws.addCell(new Label(10, i + 4, o.getOafactory(), wc));
			// // 支出
			// ws
			// .addCell(new Label(11, i + 4, o
			// .getOainvoicenumber(), wc));
			// ws.addCell(new Label(12, i + 4, o.getOadate(), wc));
			// // 结余
			// ws.addCell(new Label(13, i + 4, o.getOahetongnumber(), wc));
			// }
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	public void findDaoChuBom(Integer rootId, String pageStatus) {
		// TODO Auto-generated method stub
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			ProcardTemplate totalCard = (ProcardTemplate) totalDao
					.getObjectById(ProcardTemplate.class, rootId);
			OutputStream os = response.getOutputStream();
			response.reset();
			String name = totalCard.getYwMarkId();
			if (name == null || name.length() == 0) {
				name = totalCard.getMarkId();
			}
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(name.getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("Bom明细", 0);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);
			wc.setAlignment(Alignment.LEFT);
			ws.addCell(new jxl.write.Label(0, 0, "物料代码", wc));
			ws.addCell(new jxl.write.Label(1, 0, "物料名称", wc));
			ws.addCell(new jxl.write.Label(2, 0, "规格型号", wc));
			ws.addCell(new jxl.write.Label(3, 0, "物料属性", wc));
			ws.addCell(new jxl.write.Label(4, 0, "单位", wc));
			ws.addCell(new jxl.write.Label(5, 0, "图号", wc));
			ws.addCell(new jxl.write.Label(6, 0, "建立人员", wc));
			ws.addCell(new jxl.write.Label(7, 0, "建立日期", wc));
			ws.addCell(new jxl.write.Label(8, 0, "使用人员", wc));
			if ("price".equals(pageStatus)) {
				ws.addCell(new jxl.write.Label(9, 0, "外购总价格(含税)", wc));
			}
			ws.addCell(new jxl.write.Label(0, 1, totalCard.getYwMarkId(), wc));
			ws.addCell(new jxl.write.Label(1, 1, totalCard.getProName(), wc));
			ws.addCell(new jxl.write.Label(2, 1, totalCard.getSpecification(),
					wc));
			ws.addCell(new jxl.write.Label(3, 1, totalCard.getProcardStyle(),
					wc));
			ws.addCell(new jxl.write.Label(4, 1, totalCard.getUnit(), wc));
			ws.addCell(new jxl.write.Label(5, 1, totalCard.getYwMarkId(), wc));
			ws.addCell(new jxl.write.Label(6, 1, totalCard.getCreateUserName(),
					wc));
			ws
					.addCell(new jxl.write.Label(7, 1, totalCard
							.getCreateDate(), wc));
			ws.addCell(new jxl.write.Label(8, 1, totalCard.getCreateUserName(),
					wc));
			ws.addCell(new jxl.write.Label(0, 2, "层次", wc));
			ws.addCell(new jxl.write.Label(1, 2, "物料名称", wc));
			ws.addCell(new jxl.write.Label(2, 2, "规格型号", wc));
			ws.addCell(new jxl.write.Label(3, 2, "物料属性", wc));
			ws.addCell(new jxl.write.Label(4, 2, "件号", wc));
			ws.addCell(new jxl.write.Label(5, 2, "图号", wc));
			ws.addCell(new jxl.write.Label(6, 2, "单位", wc));
			ws.addCell(new jxl.write.Label(7, 2, "单位用量", wc));
			ws.addCell(new jxl.write.Label(8, 2, "单台用量(导入前删除)", wc));
			ws.addCell(new jxl.write.Label(9, 2, "版本", wc));
			ws.addCell(new jxl.write.Label(10, 2, "比重", wc));
			ws.addCell(new jxl.write.Label(11, 2, "表处", wc));
			ws.addCell(new jxl.write.Label(12, 2, "材料类别", wc));
			ws.addCell(new jxl.write.Label(13, 2, "初始总成", wc));
			ws.addCell(new jxl.write.Label(14, 2, "供料属性", wc));
			ws.addCell(new jxl.write.Label(15, 2, "外购属性", wc));
			if ("price".equals(pageStatus)) {
				ws.addCell(new jxl.write.Label(16, 2, "外购单价(含税)", wc));
				ws.addCell(new jxl.write.Label(17, 2, "外购总额(含税)", wc));
				ws.addCell(new jxl.write.Label(18, 2, "外购单价(不含税)", wc));
				ws.addCell(new jxl.write.Label(19, 2, "外购总额(不含税)", wc));
				ws.addCell(new jxl.write.Label(20, 2, "外委单价(含税)", wc));
				ws.addCell(new jxl.write.Label(21, 2, "外委总额(含税)", wc));
				ws.addCell(new jxl.write.Label(22, 2, "人工成本", wc));
				ws.addCell(new jxl.write.Label(23, 2, "长", wc));
				ws.addCell(new jxl.write.Label(24, 2, "宽", wc));
				ws.addCell(new jxl.write.Label(25, 2, "厚", wc));
				ws.addCell(new jxl.write.Label(26, 2, "材质", wc));
				ws.addCell(new jxl.write.Label(27, 2, "编制状态", wc));
			} else {
				ws.addCell(new jxl.write.Label(16, 2, "已有原图", wc));
				ws.addCell(new jxl.write.Label(17, 2, "已有展开图", wc));
				ws.addCell(new jxl.write.Label(18, 2, "已有工艺卡", wc));
				ws.addCell(new jxl.write.Label(19, 2, "已有成型图", wc));
				ws.addCell(new jxl.write.Label(20, 2, "已有工序", wc));
				ws.addCell(new jxl.write.Label(21, 2, "长", wc));
				ws.addCell(new jxl.write.Label(22, 2, "宽", wc));
				ws.addCell(new jxl.write.Label(23, 2, "厚", wc));
				ws.addCell(new jxl.write.Label(24, 2, "材质", wc));
				ws.addCell(new jxl.write.Label(25, 2, "编制状态", wc));

			}

			totalCard.setXiaohaoCount(1f);
			daochuBom2(totalCard, 3, ws, wc, pageStatus);
			if ("price".equals(pageStatus)) {
				Util.FomartFloat(sumprice, 4);
				ws.addCell(new jxl.write.Label(9, 1, sumprice + "", wc));
				sumprice = 0f;
			}
			// for (int i = 0; i < list.size(); i++) {
			// Oabusiness o = (Oabusiness) list.get(i);
			// ws.addCell(new jxl.write.Number(0, i + 4, i + 1, wc));
			// ws.addCell(new Label(1, i + 4, o.getOaproductnumber(), wc));
			// ws.addCell(new Label(2, i + 4, o.getOaproductName(), wc));
			// ws.addCell(new Label(3, i + 4, o.getOaspecification(), wc));
			// ws.addCell(new Label(4, i + 4, o.getOaunit(), wc));
			// ws.addCell(new Label(5, i + 4, String.format("%.2f", o
			// .getOaquantity()), wc));
			// // 单价
			// ws.addCell(new Label(6, i + 4, String.format("%.2f", o
			// .getOaunitprice()), wc));
			// ws.addCell(new Label(7, i + 4, String.format("%.2f", o
			// .getOatotalMon()), wc));
			// ws.addCell(new Label(8, i + 4, o.getOastatus(), wc));
			// ws.addCell(new Label(9, i + 4, o.getOausername(), wc));
			// ws.addCell(new Label(10, i + 4, o.getOafactory(), wc));
			// // 支出
			// ws
			// .addCell(new Label(11, i + 4, o
			// .getOainvoicenumber(), wc));
			// ws.addCell(new Label(12, i + 4, o.getOadate(), wc));
			// // 结余
			// ws.addCell(new Label(13, i + 4, o.getOahetongnumber(), wc));
			// }

			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	private int daochuBom2(ProcardTemplate pt, int i, WritableSheet ws,
			WritableCellFormat wc, String pageStatus) {
		// TODO Auto-generated method stub
		try {
			String befor = "";
			for (int b = 0; b < pt.getBelongLayer(); b++) {
				befor += ".";
			}
			ws.addCell(new Label(0, i, befor + pt.getBelongLayer(), wc));// 层次
			ws.addCell(new Label(1, i, pt.getProName(), wc));// 名称
			if (pt.getProcardStyle() != null
					&& !pt.getProcardStyle().equals("外购")) {
				ws.addCell(new Label(2, i, "", wc));// 规格
			} else {
				if (pt.getSpecification() != null) {
					ws.addCell(new Label(2, i, pt.getSpecification().replace(
							"\t", "  "), wc));// 规格
				} else {
					ws.addCell(new Label(2, i, "", wc));// 规格
				}
			}
			if (pt.getProcardStyle() != null
					&& pt.getProcardStyle().equals("外购")
					&& pt.getNeedProcess() != null
					&& pt.getNeedProcess().equals("yes")) {
				ws.addCell(new Label(3, i, "半成品", wc));// 零件类型
			} else {
				ws.addCell(new Label(3, i, pt.getProcardStyle(), wc));// 零件类型

			}
			ws.addCell(new Label(4, i, pt.getMarkId(), wc));// 件号
			ws.addCell(new Label(5, i, pt.getTuhao(), wc));// 图号
			ws.addCell(new Label(6, i, pt.getUnit(), wc));// 单位
			if (pt.getProcardStyle() != null
					&& pt.getProcardStyle().equals("总成")) {
				ws.addCell(new Label(7, i, 1 + "", wc));
			} else if (pt.getProcardStyle() != null
					&& !pt.getProcardStyle().equals("外购")) {
				ws.addCell(new Label(7, i, pt.getCorrCount() + "", wc));// 用量
			} else {
				if (pt.getQuanzi2() != null && pt.getQuanzi1() != null
						&& pt.getQuanzi1() != 0) {
					ws.addCell(new Label(7, i, (pt.getQuanzi2() / pt
							.getQuanzi1())
							+ "", wc));// 用量
				} else {
					ws.addCell(new Label(7, i, "需填写", wc));// 用量
				}
			}
			ws.addCell(new Label(8, i, pt.getXiaohaoCount() + "", wc));// 单台用量
			ws.addCell(new Label(9, i, pt.getBanBenNumber(), wc));// 版本
			// ws.addCell(new Label(9,i, pt.getBili()+"", wc));//比重
			ws.addCell(new Label(10, i, "", wc));// 比重
			ws.addCell(new Label(11, i, pt.getBiaochu(), wc));// 表处
			ws.addCell(new Label(12, i, pt.getClType(), wc));// 材料类别
			ws.addCell(new Label(13, i, pt.getLoadMarkId(), wc));// 初始总成
			ws.addCell(new Label(14, i, pt.getKgliao(), wc));// 供料属性
			ws.addCell(new Label(15, i, pt.getWgType(), wc));// 物料类别
			if ("price".equals(pageStatus)) {
				if ("外购".equals(pt.getProcardStyle())) {
					String time = Util.getDateTime();
					String before_hql = "select min(hsPrice),min(bhsPrice) ";
					if ("试制".equals(pt.getProductStyle())) {
						before_hql = "select max(hsPrice),max(bhsPrice) ";
					}
					String hql_price = " from Price where produceType='外购' and partNumber = ? and pricePeriodStart<= '"
							+ time
							+ "' and (pricePeriodEnd >= '"
							+ time
							+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
					if (pt.getKgliao() != null && pt.getKgliao().length() > 0) {
						hql_price += "  and kgliao = '" + pt.getKgliao() + "'";
					} else {
						hql_price += " and (kgliao is null or kgliao = '')";
					}
					if (pt.getBanBenNumber() != null
							&& pt.getBanBenNumber().length() > 0) {
						hql_price += " and banbenhao = '"
								+ pt.getBanBenNumber() + "'";
					} else {
						hql_price += " and (banbenhao is null or banbenhao = '')";
					}
					hql_price += " order by hsPrice ";

					Object[] price = null;
					try {
						price = (Object[]) totalDao.getObjectByCondition(
								before_hql + hql_price, pt.getMarkId());
					} catch (Exception e) {
						System.out.println(before_hql + hql_price);
						System.out.println(pt.getMarkId());
						e.printStackTrace();
					}

					if (price != null && price.length > 0) {
						Double hsPrice = null;
						Double bhsPrice = null;
						try {
							hsPrice = (Double) price[0];
							bhsPrice = (Double) price[1];
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (hsPrice != null) {

							ws.addCell(new Label(16, i, Util.FomartFloat(
									hsPrice.floatValue(), 4)
									+ "", wc));// 
							ws.addCell(new Label(17, i, Util
									.FomartFloat(hsPrice.floatValue()
											* pt.getXiaohaoCount(), 4)
									+ "", wc));//
							sumprice += hsPrice.floatValue()
									* pt.getXiaohaoCount();
						}
						if (bhsPrice != null) {
							ws.addCell(new Label(18, i, Util.FomartFloat(
									bhsPrice.floatValue(), 4)
									+ "", wc));// 
							ws.addCell(new Label(19, i, Util.FomartFloat(
									bhsPrice.floatValue()
											* pt.getXiaohaoCount(), 4)
									+ "", wc));//
						}
					}
				}
				// }else{
				// 查询是否存在外委工序
				StringBuffer wwmsg = new StringBuffer();
				Double wwtotal = 0d;
				Float wwCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus !='删除') and productStyle='外委'",
								pt.getId());
				if (wwCount != null && wwCount > 0) {
					List<ProcessTemplate> processList = totalDao
							.query(
									"from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus !='删除') order by processNO ",
									pt.getId());
					if (processList != null && processList.size() > 0) {
						// 合并归类连续外委
						List<String> wwprocessList = new ArrayList<String>();
						boolean ww = false;
						String wwprocess = "";
						for (ProcessTemplate process : processList) {
							if (process.getProductStyle().equals("外委")) {
								if (ww) {// 上道工序为外委
									wwprocess += ";" + process.getProcessNO();
								} else {
									wwprocess += process.getProcessNO();
								}
								ww = true;
							} else {
								if (ww) {// 上道工序为外委
									wwprocessList.add(wwprocess);
								}
								wwprocess = "";
								ww = false;
							}
						}
						if (ww) {// 最后一道工序为外委
							wwprocessList.add(wwprocess);
						}
						if (wwprocessList.size() > 0) {
							for (String wwprocessNo : wwprocessList) {
								String time = Util.getDateTime();
								String hql_price = " from Price where produceType='外委' and partNumber = ? and gongxunum=?"
										+ "and pricePeriodStart<= '"
										+ time
										+ "' and (pricePeriodEnd >= '"
										+ time
										+ "' or pricePeriodEnd is null or pricePeriodEnd = '')";
								if (pt.getBanBenNumber() != null
										&& pt.getBanBenNumber().length() > 0) {
									hql_price += " and banbenhao = '"
											+ pt.getBanBenNumber() + "'";
								} else {
									hql_price += " and (banbenhao is null or banbenhao = '')";
								}
								hql_price += " order by hsPrice ";
								Price price = (Price) totalDao
										.getObjectByCondition(hql_price, pt
												.getMarkId(), wwprocessNo);
								if (price != null && price.getHsPrice() != null) {
									wwtotal += price.getHsPrice();
									if (wwmsg.length() == 0) {
										wwmsg.append(wwprocessNo
												+ ":"
												+ Util.FomartFloat(price
														.getHsPrice()
														.floatValue(), 4));
									} else {
										wwmsg.append(", "
												+ wwprocessNo
												+ ":"
												+ Util.FomartFloat(price
														.getHsPrice()
														.floatValue(), 4));
									}
								} else {
									if (wwmsg.length() == 0) {
										wwmsg.append(wwprocessNo + ":无价格");
									} else {
										wwmsg.append(", " + wwprocessNo
												+ ":无价格");
									}
								}
							}
						}
					}

				}
				// 显示外委单价
				ws.addCell(new Label(20, i, wwmsg.toString(), wc));// 
				ws.addCell(new Label(21, i, Util.FomartFloat(wwtotal
						.floatValue(), 4)
						+ "", wc));//

				// }
				// 人工成本计算;
				if (!"外购".equals(pt.getProcardStyle())) {
					Double rgcb = 0d;
					List<ProcessTemplate> pocessTList = totalDao
							.query(
									" from ProcessTemplate where procardTemplate.id =? and "
											+ " processjjMoney is not null and processjjMoney >0 and procesdianshu is not null"
											+ " and procesdianshu >0 and (productStyle is null or productStyle <> '外委') "
											+ " and (dataStatus is null or dataStatus <> '删除')",
									pt.getId());
					Float yongliang = getcgNum(pt, 1f, pt.getBelongLayer());
					if (pocessTList != null && pocessTList.size() > 0) {
						for (ProcessTemplate processT : pocessTList) {
							rgcb += (processT.getProcessjjMoney()
									* processT.getProcesdianshu() * yongliang);
						}
					}
					ws.addCell(new jxl.write.Number(22, i, rgcb));// 人工成本
				} else {
					ws.addCell(new Label(22, i, ""));// 人工成本
				}

				ws.addCell(new Label(23, i, pt.getThisLength() == null ? ""
						: pt.getThisLength() + "", wc));// 长
				ws.addCell(new Label(24, i, pt.getThisWidth() == null ? "" : pt
						.getThisWidth()
						+ "", wc));// 宽
				ws.addCell(new Label(25, i, pt.getThisHight() == null ? "" : pt
						.getThisHight()
						+ "", wc));// 厚
				ws.addCell(new Label(26, i, pt.getCaizhi() == null ? "" : pt
						.getCaizhi()
						+ "", wc));// 材质
				ws.addCell(new Label(27, i, pt.getBzStatus() == null ? "" : pt
						.getBzStatus()
						+ "", wc));// 编制状态

			} else {
				String addSql2 = null;
				if (pt.getProductStyle() == null) {
					pt.setProductStyle("批产");
				}
				if (pt.getProductStyle().equals("批产")) {
					addSql2 = " and (productStyle is null or productStyle !='试制') ";
				} else {
					addSql2 = " and glId='" + pt.getId()
							+ "' and processNO is null  ";
				}
				if (pt.getBanci() == null) {
					addSql2 += " and (banci is null or banci=0)";
				} else {
					addSql2 += " and banci=" + pt.getBanci();
				}

				// 查询图纸是否存在
				Float ytfileCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcessTemplateFile where markId=?  and oldfileName like '%原图%'"
										+ addSql2, pt.getMarkId());
				if (ytfileCount != null && ytfileCount > 0) {
					ws.addCell(new Label(16, i, "是", wc));// 已有原图
				} else {
					ws.addCell(new Label(16, i, "否", wc));// 已有原图
				}

				Float zktfileCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcessTemplateFile where markId=? and oldfileName like '%展开图%'"
										+ addSql2, pt.getMarkId());
				if (zktfileCount != null && zktfileCount > 0) {
					ws.addCell(new Label(17, i, "是", wc));// 已有展开图
				} else {
					ws.addCell(new Label(17, i, "否", wc));// 已有展开图
				}
				Float gykfileCount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcessTemplateFile where markId=?  and oldfileName like '%工艺卡%'"
										+ addSql2, pt.getMarkId());
				if (gykfileCount != null && gykfileCount > 0) {
					ws.addCell(new Label(18, i, "是", wc));// 已有工艺卡
				} else {
					ws.addCell(new Label(18, i, "否", wc));// 已有工艺卡
				}
				Float cxtfileCount = (Float) totalDao.getObjectByCondition(
						"select count(*) from ProcessTemplateFile where markId=?  and type='成型图'"
								+ addSql2, pt.getMarkId());
				if (cxtfileCount != null && cxtfileCount > 0) {
					ws.addCell(new Label(19, i, "是", wc));// 已有成型图
				} else {
					ws.addCell(new Label(19, i, "否", wc));// 已有成型图
				}
				if (pt.getProcessTemplate() == null
						|| pt.getProcessTemplate().size() == 0) {
					ws.addCell(new Label(20, i, "否", wc));// 没有工序
				} else {
					ws.addCell(new Label(20, i, "是", wc));// 没有工序
				}
				ws.addCell(new Label(21, i, pt.getThisLength() == null ? ""
						: pt.getThisLength() + "", wc));// 长
				ws.addCell(new Label(22, i, pt.getThisWidth() == null ? "" : pt
						.getThisWidth()
						+ "", wc));// 宽
				ws.addCell(new Label(23, i, pt.getThisHight() == null ? "" : pt
						.getThisHight()
						+ "", wc));// 厚
				ws.addCell(new Label(24, i, pt.getCaizhi() == null ? "" : pt
						.getCaizhi()
						+ "", wc));// 材质
				ws.addCell(new Label(25, i, pt.getBzStatus() == null ? "" : pt
						.getBzStatus()
						+ "", wc));// 材质

				// if (pt.getProcardStyle() != null
				// && !pt.getProcardStyle().equals("外购")
				// && pt.getTrademark() != null
				// && pt.getTrademark().length() > 0) {// 原材料
				// String befor2 = "";
				// for (int b = 0; b <= pt.getBelongLayer(); b++) {
				// befor2 += ".";
				// }
				// ws.addCell(new Label(0, i, befor2 + (pt.getBelongLayer() +
				// 1),
				// wc));// 层次
				// ws.addCell(new Label(1, i, pt.getYuanName(), wc));// 名称
				// ws.addCell(new Label(2, i, pt.getSpecification(), wc));// 规格
				// ws.addCell(new Label(3, i, "原材料", wc));// 零件类型
				// ws.addCell(new Label(4, i, pt.getTrademark(), wc));// 件号
				// ws.addCell(new Label(5, i, pt.getYtuhao(), wc));// 图号
				// ws.addCell(new Label(6, i, pt.getYuanUnit(), wc));// 单位
				// ws.addCell(new Label(7, i, (pt.getQuanzi2() /
				// pt.getQuanzi1())
				// + "", wc));// 用量
				// ws.addCell(new Label(8, i, pt.getBanBenNumber(), wc));// 版本
				// if (pt.getBili() != null) {
				// ws.addCell(new Label(9, i, pt.getBili() + "", wc));// 比重
				// } else {
				// ws.addCell(new Label(9, i, "", wc));// 比重
				// }
				// // ws.addCell(new Label(10,i, pt.getBiaochu(), wc));//表处
				// ws.addCell(new Label(10, i, "", wc));// 表处
				// ws.addCell(new Label(11, i, pt.getClType(), wc));// 材料类别
				// ws.addCell(new Label(12, i, pt.getLoadMarkId(), wc));// 初始总成
				// ws.addCell(new Label(13, i, "", wc));// 供料属性
				// ws.addCell(new Label(14, i, "", wc));// 外购属性
				// ws.addCell(new Label(15, i, "", wc));// 已有原图
				// ws.addCell(new Label(16, i, "", wc));// 已有展开图
				// ws.addCell(new Label(17, i, "", wc));// 已有工艺卡
				// }
			}

			i++;
			Set<ProcardTemplate> sonSet = pt.getProcardTSet();
			if (sonSet != null && sonSet.size() > 0) {
				for (ProcardTemplate son : sonSet) {
					if ((son.getBanbenStatus() == null || !son
							.getBanbenStatus().equals("历史"))
							&& (son.getDataStatus() == null || !son
									.getDataStatus().equals("删除"))) {
						Float xiaohao = 0f;
						if (son.getProcardStyle().equals("外购")) {
							if (son.getQuanzi2() != null
									&& son.getQuanzi1() != null
									&& son.getQuanzi1() != 0) {
								xiaohao = pt.getXiaohaoCount()
										* son.getQuanzi2() / son.getQuanzi1();
								xiaohao = Util.FomartFloat(xiaohao, 4);
							}
						} else {
							if (son.getCorrCount() != null) {
								xiaohao = pt.getXiaohaoCount()
										* son.getCorrCount();
								xiaohao = Util.FomartFloat(xiaohao, 4);
							}
							if (pt.getXiaohaoCount() != null) {
								if (son.getProcardStyle().equals("外购")) {
									xiaohao = pt.getXiaohaoCount()
											* son.getQuanzi2()
											/ son.getQuanzi1();
								} else {
									if (son.getCorrCount() != null) {
										xiaohao = pt.getXiaohaoCount()
												* son.getCorrCount();
									}
								}
								xiaohao = Util.FomartFloat(xiaohao, 4);
							}
						}
						son.setXiaohaoCount(xiaohao);
						i = daochuBom2(son, i, ws, wc, pageStatus);
					}
				}
			}
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 材质
		return i;
	}

	@Override
	public void findDaoChuHWBom(Integer rootId) {
		// TODO Auto-generated method stub
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			ProcardTemplate totalCard = (ProcardTemplate) totalDao
					.getObjectById(ProcardTemplate.class, rootId);
			OutputStream os = response.getOutputStream();
			response.reset();
			String name = totalCard.getYwMarkId();
			if (name == null || name.length() == 0) {
				name = totalCard.getMarkId();
			}
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(name.getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws1 = wwb.createSheet("sheet1", 0);
			WritableSheet ws = wwb.createSheet("生产物料BOM表", 1);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);
			wc.setAlignment(Alignment.LEFT);
			// ws.addCell(new jxl.write.Label(0, 0, "物料代码", wc));
			// ws.addCell(new jxl.write.Label(1, 0, "物料名称", wc));
			// ws.addCell(new jxl.write.Label(2, 0, "规格型号", wc));
			// ws.addCell(new jxl.write.Label(3, 0, "物料属性", wc));
			ws.addCell(new jxl.write.Label(0, 3, "版本:" + totalCard.getBanci(),
					wc));
			ws.addCell(new jxl.write.Label(4, 3, "产品编码:"
					+ totalCard.getYwMarkId(), wc));
			ws.addCell(new jxl.write.Label(22, 3, "客户名称：华为", wc));
			ws.mergeCells(4, 2, 7, 2);
			ws.mergeCells(0, 2, 3, 2);
			// ws.addCell(new jxl.write.Label(7, 0, "建立日期", wc));
			// ws.addCell(new jxl.write.Label(8, 0, "使用人员", wc));
			// ws.addCell(new jxl.write.Label(0, 1, totalCard.getYwMarkId(),
			// wc));
			// ws.addCell(new jxl.write.Label(1, 1, totalCard.getProName(),
			// wc));
			// ws.addCell(new jxl.write.Label(2, 1,
			// totalCard.getSpecification(),
			// wc));
			// ws.addCell(new jxl.write.Label(3, 1, totalCard.getProcardStyle(),
			// wc));
			// ws.addCell(new jxl.write.Label(4, 1, totalCard.getUnit(), wc));
			// ws.addCell(new jxl.write.Label(5, 1, totalCard.getYwMarkId(),
			// wc));
			// ws.addCell(new jxl.write.Label(6, 1,
			// totalCard.getCreateUserName(),
			// wc));
			// ws
			// .addCell(new jxl.write.Label(7, 1, totalCard
			// .getCreateDate(), wc));
			// ws.addCell(new jxl.write.Label(8, 1,
			// totalCard.getCreateUserName(),
			// wc));
			ws.addCell(new jxl.write.Label(0, 4, "序号", wc));
			ws.addCell(new jxl.write.Label(1, 4, "物料编号", wc));
			ws.addCell(new jxl.write.Label(2, 4, "模型图号", wc));
			ws.addCell(new jxl.write.Label(3, 4, "图号", wc));
			ws.addCell(new jxl.write.Label(4, 4, "版本", wc));
			ws.addCell(new jxl.write.Label(5, 4, "名称", wc));
			ws.mergeCells(6, 1, 15, 1);// 合并单元格
			WritableCellFormat wc2 = new WritableCellFormat(wf);
			wc2.setAlignment(Alignment.CENTRE);
			ws.addCell(new jxl.write.Label(6, 4, "阶层/单台数量6-15", wc2));
			ws.addCell(new jxl.write.Label(16, 4, "来源", wc));
			ws.addCell(new jxl.write.Label(17, 4, "实际用量", wc));
			ws.addCell(new jxl.write.Label(18, 4, "单位", wc));
			ws.addCell(new jxl.write.Label(19, 4, "表处", wc));
			ws.addCell(new jxl.write.Label(20, 4, "材质", wc));
			ws.addCell(new jxl.write.Label(21, 4, "长", wc));
			ws.addCell(new jxl.write.Label(22, 4, "宽", wc));
			ws.addCell(new jxl.write.Label(23, 4, "高", wc));
			ws.addCell(new jxl.write.Label(24, 4, "备注", wc));
			ws.addCell(new jxl.write.Label(25, 4, "材料类别", wc));
			ws.addCell(new jxl.write.Label(26, 4, "规格(导入截止列)", wc));
			ws.addCell(new jxl.write.Label(27, 4, "零件类型", wc));
			ws.addCell(new jxl.write.Label(28, 4, "是否有工序", wc));
			ws.addCell(new jxl.write.Label(29, 4, "编制状态", wc));
			ws.addCell(new jxl.write.Label(30, 4, "展开图", wc));
			daoChuHWBom2(totalCard, 5, ws, wc);
			// for (int i = 0; i < list.size(); i++) {
			// Oabusiness o = (Oabusiness) list.get(i);
			// ws.addCell(new jxl.write.Number(0, i + 4, i + 1, wc));
			// ws.addCell(new Label(1, i + 4, o.getOaproductnumber(), wc));
			// ws.addCell(new Label(2, i + 4, o.getOaproductName(), wc));
			// ws.addCell(new Label(3, i + 4, o.getOaspecification(), wc));
			// ws.addCell(new Label(4, i + 4, o.getOaunit(), wc));
			// ws.addCell(new Label(5, i + 4, String.format("%.2f", o
			// .getOaquantity()), wc));
			// // 单价
			// ws.addCell(new Label(6, i + 4, String.format("%.2f", o
			// .getOaunitprice()), wc));
			// ws.addCell(new Label(7, i + 4, String.format("%.2f", o
			// .getOatotalMon()), wc));
			// ws.addCell(new Label(8, i + 4, o.getOastatus(), wc));
			// ws.addCell(new Label(9, i + 4, o.getOausername(), wc));
			// ws.addCell(new Label(10, i + 4, o.getOafactory(), wc));
			// // 支出
			// ws
			// .addCell(new Label(11, i + 4, o
			// .getOainvoicenumber(), wc));
			// ws.addCell(new Label(12, i + 4, o.getOadate(), wc));
			// // 结余
			// ws.addCell(new Label(13, i + 4, o.getOahetongnumber(), wc));
			// }

			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

	}

	private int daoChuHWBom2(ProcardTemplate pt, int i, WritableSheet ws,
			WritableCellFormat wc) {
		// TODO Auto-generated method stub
		try {
			ws.addCell(new jxl.write.Label(0, i, (i - 1) + "", wc));
			ws.addCell(new jxl.write.Label(1, i, "", wc));
			ws.addCell(new jxl.write.Label(2, i, pt.getTuhao(), wc));
			ws.addCell(new jxl.write.Label(3, i, pt.getMarkId(), wc));
			// ws.addCell(new jxl.write.Label(3, i, pt.getTuhao(), wc));
			ws.addCell(new jxl.write.Label(4, i, pt.getBanBenNumber(), wc));
			ws.addCell(new jxl.write.Label(5, i, pt.getProName(), wc));
			if (pt.getProcardStyle().equals("外购")) {
				if (pt.getNeedProcess() != null
						&& pt.getNeedProcess().equals("yes")) {
					ws.addCell(new jxl.write.Label(16, i, "半成品", wc));
				} else {
					ws.addCell(new jxl.write.Label(16, i, "外购", wc));
				}
				try {
					ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i,
							pt.getQuanzi2() / pt.getQuanzi1() + "", wc));
					ws.addCell(new jxl.write.Label(17, i, pt.getQuanzi2()
							/ pt.getQuanzi1() + "", wc));
				} catch (Exception e) {
					// TODO: handle exception
					ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i,
							"待填", wc));
					ws.addCell(new jxl.write.Label(17, i, "待填", wc));
				}

				if (pt.getThisLength() != null) {
					ws.addCell(new jxl.write.Label(21, i, pt.getThisLength()
							+ "", wc));
				}
				if (pt.getThisWidth() != null) {
					ws.addCell(new jxl.write.Label(22, i, pt.getThisWidth()
							+ "", wc));
				}
				if (pt.getThisHight() != null) {
					ws.addCell(new jxl.write.Label(23, i, pt.getThisHight()
							+ "", wc));
				}
				ws
						.addCell(new jxl.write.Label(24, i, pt
								.getSpecification(), wc));
			} else if (pt.getProcardStyle().equals("待定")) {
				ws.addCell(new jxl.write.Label(16, i, "待定", wc));
				ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i, pt
						.getCorrCount()
						+ "", wc));
				ws.addCell(new jxl.write.Label(17, i, pt.getCorrCount() + "",
						wc));
				ws.addCell(new jxl.write.Label(24, i, pt.getLoadMarkId(), wc));
			} else if (pt.getProcardStyle().equals("总成")) {
				ws.addCell(new jxl.write.Label(16, i, "自制", wc));
				ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i,
						1 + "", wc));
			} else {
				ws.addCell(new jxl.write.Label(16, i, "自制", wc));
				ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), i, pt
						.getCorrCount()
						+ "", wc));
				ws.addCell(new jxl.write.Label(17, i, pt.getCorrCount() + "",
						wc));
				ws.addCell(new jxl.write.Label(24, i, pt.getLoadMarkId(), wc));
			}
			ws.addCell(new jxl.write.Label(18, i, pt.getUnit(), wc));
			ws.addCell(new jxl.write.Label(19, i, pt.getBiaochu(), wc));
			ws.addCell(new jxl.write.Label(20, i, pt.getClType(), wc));
			if (pt.getThisLength() != null) {
				ws.addCell(new jxl.write.Label(21, i, pt.getThisLength() + "",
						wc));
			}
			if (pt.getThisWidth() != null) {
				ws.addCell(new jxl.write.Label(22, i, pt.getThisWidth() + "",
						wc));
			}
			if (pt.getThisHight() != null) {
				ws.addCell(new jxl.write.Label(23, i, pt.getThisHight() + "",
						wc));
			}
			ws.addCell(new jxl.write.Label(25, i, pt.getWgType(), wc));
			ws.addCell(new jxl.write.Label(26, i, pt.getSpecification(), wc));
			ws.addCell(new jxl.write.Label(27, i, pt.getProcardStyle(), wc));
			if (pt.getProcessTemplate() == null
					|| pt.getProcessTemplate().size() == 0) {
				ws.addCell(new Label(28, i, "否", wc));// 没有工序
			} else {
				ws.addCell(new Label(28, i, "是", wc));// 有工序
			}
			ws.addCell(new Label(29, i, pt.getBzStatus(), wc));// 有工序
			// 图纸
			String addSql2 = null;
			if (pt.getProductStyle() == null) {
				pt.setProductStyle("批产");
			}
			if (pt.getProductStyle().equals("批产")) {
				addSql2 = " and (productStyle is null or productStyle !='试制') ";
			} else {
				addSql2 = " and glId='" + pt.getId()
						+ "' and processNO is null  ";
			}
			if (pt.getBanci() == null) {
				addSql2 += " and (banci is null or banci=0)";
			} else {
				addSql2 += " and banci=" + pt.getBanci();
			}
			Float zktfileCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessTemplateFile where markId=? and oldfileName like '%展开图%'"
									+ addSql2, pt.getMarkId());
			if (zktfileCount != null && zktfileCount > 0) {
				ws.addCell(new Label(30, i, "是", wc));// 已有展开图
			} else {
				ws.addCell(new Label(30, i, "否", wc));// 已有展开图
			}
			i++;
			// if (!pt.getProcardStyle().equals("外购") && pt.getTrademark() !=
			// null
			// && pt.getTrademark().length() > 0) {
			// ws.addCell(new jxl.write.Label(0, i, (i - 2) + "", wc));
			// ws.addCell(new jxl.write.Label(1, i, "", wc));
			// ws.addCell(new jxl.write.Label(3, i, pt.getTrademark(), wc));
			// // ws.addCell(new jxl.write.Label(3, i, pt.getYtuhao(), wc));
			// ws.addCell(new jxl.write.Label(4, i, pt.getBanBenNumber(), wc));
			// ws.addCell(new jxl.write.Label(5, i, pt.getYuanName(), wc));
			// if (pt.getQuanzi2() != null && pt.getQuanzi1() != null
			// && pt.getQuanzi1() != 0) {
			// ws.addCell(new jxl.write.Label(6 + pt.getBelongLayer(), i,
			// pt.getQuanzi2() / pt.getQuanzi1() + "", wc));
			// }
			// ws.addCell(new jxl.write.Label(16, i, "原材料", wc));
			// if (pt.getQuanzi2() != null && pt.getQuanzi1() != null
			// && pt.getQuanzi1() != 0) {
			// ws.addCell(new jxl.write.Label(17, i, pt.getQuanzi2()
			// / pt.getQuanzi1() + "", wc));
			// }
			// ws.addCell(new jxl.write.Label(18, i, pt.getYuanUnit(), wc));
			// ws.addCell(new jxl.write.Label(19, i, pt.getBiaochu(), wc));
			// ws.addCell(new jxl.write.Label(20, i, pt.getClType(), wc));
			// ws
			// .addCell(new jxl.write.Label(24, i, pt
			// .getSpecification(), wc));
			// ws.addCell(new jxl.write.Label(25, i, pt.getWgType(), wc));
			// ws.addCell(new jxl.write.Label(26, i, "原材料", wc));
			// ws.addCell(new jxl.write.Label(27, i, "", wc));// 已有原图
			// ws.addCell(new jxl.write.Label(28, i, "", wc));// 已有展开图
			// ws.addCell(new jxl.write.Label(29, i, "", wc));// 已有工艺卡
			// i++;
			// }
			List<ProcardTemplate> sonSet = totalDao
					.query(
							" from ProcardTemplate where "
									+ " procardTemplate.id = ? and (dataStatus is null or dataStatus <> '删除')",
							pt.getId());
			if (sonSet != null && sonSet.size() > 0) {
				for (ProcardTemplate son : sonSet) {
					if (son.getBanbenStatus() == null
							|| son.getBanbenStatus().equals("默认")) {
						i = daoChuHWBom2(son, i, ws, wc);
					}
				}
			}
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 材质
		return i;
	}

	/**
	 * BOM导入后将历史存在的复制
	 * 
	 * @param pt1
	 *            父
	 * @param pt2
	 *            子
	 * @return
	 */
	public boolean saveCopyProcard(ProcardTemplate pt1, ProcardTemplate pt2,
			String productStyle) {
		Users user = Util.getLoginUser();
		String time = Util.getDateTime();
		if (pt1 != null && pt2 != null) {
			// 获取主副两个对象的持久层
			ProcardTemplate oldpt1 = pt1;
			ProcardTemplate oldpt2 = pt2;
			boolean b = true;
			if (oldpt1 != null && oldpt2 != null) {
				// 复制procardTemplate
				ProcardTemplate newpt2 = new ProcardTemplate();
				// newpt2.setMarkId("123");
				BeanUtils.copyProperties(pt2, newpt2, new String[] { "id",
						"procardTemplate", "procardTSet", "processTemplate",
						"zhUsers", "ywMarkId", "rootMarkId", "productStyle" });
				if (pt2.getProcardStyle().equals("总成")) {
					newpt2.setProcardStyle("自制");
				}
				newpt2.setId(null);
				newpt2.setProductStyle(productStyle);
				newpt2.setRootId(oldpt1.getRootId());
				newpt2.setFatherId(oldpt1.getId());
				newpt2.setYwMarkId(oldpt1.getYwMarkId());
				newpt2.setAddcode(user.getCode());
				newpt2.setAdduser(user.getName());
				newpt2.setAddtime(Util.getDateTime());
				if (oldpt1.getBelongLayer() != null) {
					newpt2.setBelongLayer(oldpt1.getBelongLayer() + 1);
				}
				List<ProcardTemplate> ptSet = totalDao
						.query(
								"from ProcardTemplate where procardTemplate.id=? and (banbenStatus!='历史' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by id asc",
								oldpt2.getId());
				// Set<ProcardTemplate> ptSet2 = new
				// HashSet<ProcardTemplate>();
				// if (ptSet.size() > 0) {
				// for (ProcardTemplate pt : ptSet) {
				// ptSet2.add(pt);
				// }
				// }
				// 复制process
				Set<ProcessTemplate> processSet = oldpt2.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					Set<ProcessTemplate> newprocessSet = new HashSet<ProcessTemplate>();
					for (ProcessTemplate process : processSet) {
						ProcessTemplate p = new ProcessTemplate();
						BeanUtils.copyProperties(process, p, new String[] {
								"id", "procardTemplate", "processPRNScore",
								"taSopGongwei", "processFuLiaoTemplate" });
						// -----辅料开始---------
						if (p.getIsNeedFuliao() != null
								&& p.getIsNeedFuliao().equals("yes")) {
							Set<ProcessFuLiaoTemplate> fltmpSet = process
									.getProcessFuLiaoTemplate();
							if (fltmpSet.size() > 0) {
								Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
									ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
									BeanUtils.copyProperties(fltmp, pinfoFl,
											new String[] { "id",
													"processTemplate" });
									if (pinfoFl.getQuanzhi1() == null) {
										pinfoFl.setQuanzhi1(1f);
									}
									if (pinfoFl.getQuanzhi2() == null) {
										pinfoFl.setQuanzhi2(1f);
									}
									pinfoFl.setProcessTemplate(p);
									pinfoFlSet.add(pinfoFl);
								}
								p.setProcessFuLiaoTemplate(pinfoFlSet);
							}
						}
						// -----辅料结束---------
						newprocessSet.add(p);
						totalDao.save(p);
						if (productStyle.equals("试制")) {// 试制图纸复制
							String tzSql = null;
							if (pt2.getProductStyle().equals("试制")) {
								tzSql = "from ProcessTemplateFile where glId="
										+ process.getId()
										+ " and processNO is not null";
								if (pt2.getBanci() == null
										|| pt2.getBanci() == 0) {
									tzSql += " and (banci is null or banci=0)";
								} else {
									tzSql += " and banci=" + pt2.getBanci();
								}
							} else {
								tzSql = "from ProcessTemplateFile where glId is null and processNO ='"
										+ process.getProcessNO()
										+ "' and markId='"
										+ newpt2.getMarkId()
										+ "'";
								if (pt2.getBanci() == null
										|| pt2.getBanci() == 0) {
									tzSql += " and (banci is null or banci=0)";
								} else {
									tzSql += " and banci=" + pt2.getBanci();
								}
							}
							List<ProcessTemplateFile> processFileList = totalDao
									.query(tzSql);
							if (processFileList != null
									&& processFileList.size() > 0) {
								for (ProcessTemplateFile processFile : processFileList) {
									if (processFile.getBanci() == null) {
										processFile.setBanci(0);
									}
									ProcessTemplateFile newfile = new ProcessTemplateFile();
									BeanUtils.copyProperties(processFile,
											newfile, new String[] { "id" });
									newfile.setAddTime(time);
									newfile.setStatus("默认");
									newfile.setGlId(p.getId());
									newfile.setProductStyle("试制");

									// //全尺寸测量章
									// String icon_fileRealPath =
									// ServletActionContext
									// .getServletContext().getRealPath(
									// "/upload/file/yz/icon_cl.png");
									// // 生成加章文件
									//
									// String month =
									// Util.getDateTime("yyyy-MM");
									// String
									// path=ServletActionContext.getServletContext().getRealPath("/upload/file/processTz/"
									// + newfile.getMonth());
									// Util.markPDFByIcon(icon_fileRealPath,
									// path + "/"
									// + newfile.getFileName2(), path + "/" +
									// newfile.getFileName2(), 1.7f, 1.14f,
									// 2.5f, 9f);

									totalDao.save(newfile);

								}
							}
						}
					}
					newpt2.setProcessTemplate(newprocessSet);
				}
				newpt2.setProcardTemplate(oldpt1);
				Set<ProcardTemplate> oldpt1ptSet = oldpt1.getProcardTSet();
				if (oldpt1ptSet != null && oldpt1ptSet.size() > 0) {
					for (ProcardTemplate p : oldpt1ptSet) {
						if (p.getMarkId() != null && newpt2.getMarkId() != null
								&& p.getMarkId().equals(newpt2.getMarkId())) {
							// 原流水零件模板下已含有被复制的流水零件模板
							return true;
						}
					}
					oldpt1ptSet.add(newpt2);
				}
				oldpt1.setProcardTSet(oldpt1ptSet);
				b = b & totalDao.save(newpt2);
				if (productStyle.equals("试制")) {// 试制图纸复制
					String tzSql = null;
					if (pt2.getProductStyle().equals("试制")) {
						tzSql = "from ProcessTemplateFile where glId="
								+ pt2.getId() + " and processNO is  null";
						if (pt2.getBanci() == null || pt2.getBanci() == 0) {
							tzSql += " and (banci is null or banci=0)";
						} else {
							tzSql += " and banci=" + pt2.getBanci();
						}
					} else {
						tzSql = "from ProcessTemplateFile where glId is null and processNO is null and markId='"
								+ newpt2.getMarkId() + "'";
						if (pt2.getBanci() == null || pt2.getBanci() == 0) {
							tzSql += " and (banci is null or banci=0)";
						} else {
							tzSql += " and banci=" + pt2.getBanci();
						}
					}
					List<ProcessTemplateFile> processFileList = totalDao
							.query(tzSql);
					if (processFileList != null && processFileList.size() > 0) {
						for (ProcessTemplateFile processFile : processFileList) {
							if (processFile.getBanci() == null) {
								processFile.setBanci(0);
							}
							ProcessTemplateFile newfile = new ProcessTemplateFile();
							BeanUtils.copyProperties(processFile, newfile,
									new String[] { "id" });
							newfile.setAddTime(time);
							newfile.setStatus("默认");
							newfile.setGlId(newpt2.getId());
							newfile.setProductStyle("试制");

							// //全尺寸测量章
							// String icon_fileRealPath = ServletActionContext
							// .getServletContext().getRealPath(
							// "/upload/file/yz/icon_cl.png");
							// // 生成加章文件
							//
							// String month = Util.getDateTime("yyyy-MM");
							// String
							// path=ServletActionContext.getServletContext().getRealPath("/upload/file/processTz/"
							// + newfile.getMonth());
							// Util.markPDFByIcon(icon_fileRealPath, path + "/"
							// + newfile.getFileName2(), path + "/" +
							// newfile.getFileName2(), 1.7f, 1.14f, 2.5f, 9f);
							totalDao.save(newfile);
						}
					}
				}
				if (b & ptSet.size() > 0) {
					for (ProcardTemplate pt : ptSet) {
						saveCopyProcard(newpt2, pt, productStyle);
					}
				}
				return b;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String checkAndUpdateTz(Integer id, String path, String path2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		Float tqCount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardTemplatePrivilege where markId=? or markId=? ",
						pt.getMarkId(), pt.getYwMarkId() == null ? "" : pt
								.getYwMarkId());
		List<ProcardTemplate> ptList = totalDao.query(
				"from ProcardTemplate where rootId=?", id);
		if (pt != null || ptList.size() >= 0) {
			// boolean fg=false;
			boolean fg = true;
			// if(pt.getBianzhiId()!=null&&pt.getBianzhiId().equals(user.getId())){
			// fg=true;
			// }

			String month = Util.getDateTime("yyyy-MM");
			if (pt.getYwMarkId() != null || pt.getMarkId() != null) {
				// 件号查找
				File file = null;
				if (pt.getYwMarkId() == null || pt.getYwMarkId().length() == 0) {
					file = new File(path + "\\" + pt.getMarkId().trim());
				} else {
					file = new File(path + "\\" + pt.getYwMarkId().trim());
					if (!file.exists()) {
						file = new File(path + "\\" + pt.getMarkId().trim());
					}
				}
				if (!file.exists()) {
					return "对不起没有找到对应的文件";
				}
				File[] sonList1 = file.listFiles();
				if (sonList1 != null && sonList1.length > 0) {
					// 将文件放入特定位置
					File cfdz = new File(path2 + "/upload/file/processTz/"
							+ month);// 存放图纸的文件夹
					if (!cfdz.exists()) {
						cfdz.mkdirs();// 如果不存在文件夹就创建
					}
					StringBuffer drmes = new StringBuffer();
					StringBuffer errMsgSb = new StringBuffer();
					for (File son1 : sonList1) {
						if (son1.isDirectory() && son1.getName() != null) {
							if (son1.getName().equals("3D文件")) {
								sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(id,
														son2.getName(), "3D文件",
														null, son2, cfdz,
														month, user, tqCount);
											} else {
												msg = checkAndUpdateTz2(id,
														son2.getName(), "3D文件",
														null, son2, cfdz,
														month, user, tqCount);
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功3D文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("3D模型")) {
								sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (son2.getName() != null) {
											String modelpath = path2
													+ "/upload/file/processTz/3Dmodel";
											File modelDir = new File(modelpath);// 存放图纸的文件夹
											if (!modelDir.exists()) {
												modelDir.mkdirs();// 如果不存在文件夹就创建
											}
											String fileName = son2.getName();
											String markId = null;
											// if(fileName.lastIndexOf(".")>0){
											// markId = fileName.substring(0,
											// fileName.lastIndexOf("."));
											// }else{
											markId = fileName;
											// }
											ProcardTemplate pt2 = (ProcardTemplate) totalDao
													.getObjectByCondition(
															"from ProcardTemplate where id=? and (markId=? or ywMarkId=?) and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
															id, markId, markId);// 3D模型只管总成

											if (pt2 == null) {
												errMsgSb.append(fileName
														+ "没有找到对应的零件!<br/>");
											} else {
												// if
												// (pt.getBzStatus().equals("已批准"))
												// {
												// return typeName + fileName +
												// "对应的零件状态为已批准不能导入图纸!<br/>";
												// }
												markId = pt2.getMarkId();
												Integer banci = pt2.getBanci() == null ? 0
														: pt2.getBanci();
												String producttag = null;
												if ("试制".equals(pt
														.getProductStyle())) {
													producttag = "sz";
												} else {
													producttag = "pc";
												}
												String ywMarkId = null;
												if (pt2.getYwMarkId() == null
														|| pt2.getYwMarkId()
																.length() == 0) {
													ywMarkId = pt2.getMarkId();
												} else {
													ywMarkId = pt2
															.getYwMarkId();
												}
												String banben = pt2
														.getBanBenNumber() == null ? ""
														: pt2.getBanBenNumber();
												String realFileName = ywMarkId
														+ "-" + producttag
														+ "-" + banben;
												// + "-" + banci;//不管版次了版本通用
												String modelpath2 = path2
														+ "/upload/file/processTz/3Dmodel/"
														+ realFileName;
												// 删除文件夹下的文件
												Util.delAllFile(modelpath2);
												// 删除文件架
												Util.delFolder(modelpath2);
												File modelDir2 = new File(
														modelpath2);// 存放图纸的文件夹
												// 覆盖之前存在的
												String bancisql = null;
												if (pt2.getBanci() == null
														|| pt2.getBanci() == 0) {
													bancisql = " and (banci is null or banci=0)";
												} else {
													bancisql = " and banci="
															+ pt2.getBanci();
												}

												String sqlhadtz = "from ProcessTemplateFile where  type='3D模型' and markId=?"
														+ bancisql;
												List<ProcessTemplateFile> hadFileList = totalDao
														.query(sqlhadtz, pt2
																.getMarkId());
												if (hadFileList != null
														&& hadFileList.size() > 0) {
													int logn = 0;
													for (ProcessTemplateFile hadfile : hadFileList) {
														// if
														// (!fg&&hadfile.getUserCode()
														// != null
														// && !hadfile
														// .getUserCode()
														// .equals(
														// user
														// .getCode())) {
														// errMsgSb
														// .append("3D文件"
														// + fileName
														// +
														// "重复,不是同一人且不是编制人上传!<br/>");
														// b = false;
														// continue;
														// }
														// 找到文件删除
														if (logn == 0) {
															ProcardTemplateChangeLog
																	.addchangeLog(
																			totalDao,
																			pt,
																			hadfile,
																			"图纸",
																			"删除",
																			user,
																			Util
																					.getDateTime());
														}
														logn++;
														totalDao
																.delete(hadfile);
													}
												}
												if (!modelDir2.exists()) {
													modelDir2.mkdirs();// 如果不存在文件夹就创建
												}
												ProcessTemplateFile ptFile = new ProcessTemplateFile();
												ptFile.setProductStyle(pt
														.getProductStyle());
												ptFile.setBanci(banci);
												ptFile.setMarkId(markId);

												String realFileName2 = null;
												ptFile
														.setFileName(realFileName);// 文件名
												ptFile
														.setFileName2(realFileName2);// 文件名
												ptFile.setOldfileName(fileName);// 文件原名称
												ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
												ptFile.setType("3D模型");// 文件类型(视频文件，工艺规范)
												ptFile.setStatus("默认");// 默认,历史
												ptFile.setUserName(user
														.getName());// 上传人姓名
												ptFile.setUserCode(user
														.getCode());// 上传人工号
												ptFile
														.setAddTime(Util
																.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
												ptFile.setProcessName(pt
														.getProName());
												ptFile.setProcessNO(null);
												if (pt.getProductStyle()
														.equals("试制")) {
													ptFile.setGlId(pt.getId());
													ptFile
															.setProductStyle("试制");
												}
												totalDao.save(ptFile);
												ProcardTemplateChangeLog
														.addchangeLog(
																totalDao,
																pt,
																ptFile,
																"图纸",
																"添加",
																user,
																Util
																		.getDateTime());
												// 遍历添加下层文件及文件夹
												addModelSonFil(son2, path2,
														"/upload/file/processTz/3Dmodel/"
																+ realFileName);
												sdnum++;

												// String msg =
												// checkAndUpdateTz2(id,
												// son2.getName(), "3D模型",
												// null, son2, cfdz, month,
												// user);
												// if (!msg.equals("true")) {
												// errMsgSb.append(msg);
												// }
											}
										}
									}
								}
								drmes.append("导入成功3D模型:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("视频文件")
									|| son1.getName().equals("视频")) {
								sdnum = 0;// 上传成功视频文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(id,
														son2.getName(), "视频文件",
														null, son2, cfdz,
														month, user, tqCount);
											} else {
												msg = checkAndUpdateTz2(id,
														son2.getName(), "视频文件",
														null, son2, cfdz,
														month, user, tqCount);
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功视频文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("工艺规范")) {
								sdnum = 0;// 上传成功工艺规范文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {

									for (File son2 : sonList2) {

										if (son2.getName() != null
												&& !"Thumbs.db".equals(son2
														.getName())) {
											if (son2.isDirectory()
													&& (son2.getName().equals(
															"工艺卡")
															|| son2
																	.getName()
																	.equals(
																			"原图") || son2
															.getName().equals(
																	"展开图"))) {
												File[] sonList3 = son2
														.listFiles();
												if (sonList3 != null
														&& sonList3.length > 0) {
													int count = 0;
													for (File son3 : sonList3) {
														count++;
														if (!son3.isDirectory()
																&& son3
																		.getName() != null
																&& !"Thumbs.db"
																		.equals(son3
																				.getName())) {
															String msg = null;
															if (pt
																	.getProductStyle()
																	.equals(
																			"试制")) {
																msg = checkAndUpdateTz3(
																		id,
																		son3
																				.getName(),
																		"工艺规范",
																		son2,
																		son3,
																		cfdz,
																		month,
																		user,
																		tqCount);
															} else {
																msg = checkAndUpdateTz2(
																		id,
																		son3
																				.getName(),
																		"工艺规范",
																		son2,
																		son3,
																		cfdz,
																		month,
																		user,
																		tqCount);
															}
															if (!msg
																	.equals("true")) {
																errMsgSb
																		.append(msg);
															}
														}
														if (count >= 20) {
															totalDao.clear();
															count = 0;
														}
													}
												}
											} else {
												int count = 0;
												for (File son3 : sonList2) {
													count++;
													if (!son3.isDirectory()
															&& son3.getName() != null
															&& !"Thumbs.db"
																	.equals(son3
																			.getName())) {
														String msg = null;
														if (pt
																.getProductStyle()
																.equals("试制")) {
															msg = checkAndUpdateTz3(
																	id,
																	son3
																			.getName(),
																	"工艺规范",
																	son2, son3,
																	cfdz,
																	month,
																	user,
																	tqCount);
														} else {
															msg = checkAndUpdateTz2(
																	id,
																	son3
																			.getName(),
																	"工艺规范",
																	son2, son3,
																	cfdz,
																	month,
																	user,
																	tqCount);
														}
														if (!msg.equals("true")) {
															errMsgSb
																	.append(msg);
														}
													}
													if (count >= 20) {
														totalDao.clear();
														count = 0;
													}
												}
											}
										}
									}
								}
								drmes.append("导入成功工艺规范文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("成型图")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(id,
														son2.getName(), "成型图",
														null, son2, cfdz,
														month, user, tqCount);
											} else {
												msg = checkAndUpdateTz2(id,
														son2.getName(), "成型图",
														null, son2, cfdz,
														month, user, tqCount);
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功成型图:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("标签文件")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(id,
														son2.getName(), "标签文件",
														null, son2, cfdz,
														month, user, tqCount);
											} else {
												msg = checkAndUpdateTz2(id,
														son2.getName(), "标签文件",
														null, son2, cfdz,
														month, user, tqCount);
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功标签文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("编程文件")) {
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (son2.getName().equals("NC数冲")
												|| son2.getName().equals("镭射")) {
											sdnum = 0;// 上传成功成型图数量
											File[] sonList3 = son2.listFiles();
											if (sonList3 != null
													&& sonList3.length > 0) {
												for (File son3 : sonList3) {
													if (!son2.isDirectory()
															&& son2.getName() != null) {
														String msg = null;
														if (pt
																.getProductStyle()
																.equals("试制")) {
															msg = checkAndUpdateTz3(
																	id,
																	son2
																			.getName(),
																	son2
																			.getName(),
																	null, son2,
																	cfdz,
																	month,
																	user,
																	tqCount);
														} else {
															msg = checkAndUpdateTz2(
																	id,
																	son2
																			.getName(),
																	son2
																			.getName(),
																	null, son2,
																	cfdz,
																	month,
																	user,
																	tqCount);
														}
														if (!msg.equals("true")) {
															errMsgSb
																	.append(msg);
														}
													}
												}
											}
											drmes.append("导入成功"
													+ son2.getName() + ":"
													+ sdnum + "份<br/>");
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}

									}
								}
								// return "导入成功！";
							}
							if (son1.getName().equalsIgnoreCase("SOP")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(id,
														son2.getName(), "SOP",
														null, son2, cfdz,
														month, user, tqCount);
											} else {
												msg = checkAndUpdateTz2(id,
														son2.getName(), "SOP",
														null, son2, cfdz,
														month, user, tqCount);
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功SOP文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equalsIgnoreCase("SAP")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											if (pt.getProductStyle().equals(
													"试制")) {
												msg = checkAndUpdateTz3(id,
														son2.getName(), "SAP",
														null, son2, cfdz,
														month, user, tqCount);
											} else {
												msg = checkAndUpdateTz2(id,
														son2.getName(), "SAP",
														null, son2, cfdz,
														month, user, tqCount);
											}
											if (!msg.equals("true")) {
												errMsgSb.append(msg);
											}
										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功SAP文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("其它")
									|| son1.getName().equals("其它文件")) {
								sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									int count = 0;
									for (File son2 : sonList2) {
										count++;
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String msg = null;
											try {
												if (pt.getProductStyle()
														.equals("试制")) {
													msg = checkAndUpdateTz3(id,
															son2.getName(),
															"其它文件", null, son2,
															cfdz, month, user,
															tqCount);
												} else {
													msg = checkAndUpdateTz2(id,
															son2.getName(),
															"其它文件", null, son2,
															cfdz, month, user,
															tqCount);
												}
												if (!msg.equals("true")) {
													errMsgSb.append(msg);
												}
											} catch (Exception e) {
												// TODO: handle exception
												errMsgSb.append(e.getMessage());
											}

										}
										if (count >= 20) {
											totalDao.clear();
											count = 0;
										}
									}
								}
								drmes.append("导入成功其它文件:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
						}
						totalDao.clear();
					}
					return drmes.toString() + "<br/>" + errMsgSb.toString();
				}
			}
		}
		return "对不起没有找到目标BOM";
	}

	private void addModelSonFil(File file, String before, String savePath) {
		// TODO Auto-generated method stub
		File[] sonList = file.listFiles();
		for (File sonFile : sonList) {
			if (sonFile.isDirectory()) {
				File directory = new File(before + savePath + "/"
						+ sonFile.getName());
				if (!directory.exists()) {
					directory.mkdirs();
				}
				addModelSonFil(sonFile, before, savePath + "/"
						+ sonFile.getName());
			} else if (sonFile.isFile()) {
				Upload upload = new Upload();
				upload.UploadFile(sonFile, null, sonFile.getName(), savePath,
						null);
			}
		}
	}

	/**
	 * 
	 * @param rootId
	 *            总成id
	 * @param fileName
	 *            文件名称
	 * @param typeName
	 *            文件类型
	 * @param up
	 *            上层文件夹（工艺规范类型专用）
	 * @param son2
	 *            要导入的文件
	 * @param cfdz
	 *            地址前缀
	 * @param month
	 *            保存文件的文件夹月份名
	 * @param user
	 *            操作用户
	 * @return
	 */
	public String checkAndUpdateTz2(Integer rootId, String fileName,
			String typeName, File up, File son2, File cfdz, String month,
			Users user, Float tqCount) {
		String markId = fileName.substring(0, fileName.lastIndexOf("."));
		if (markId.indexOf("-原图") > 0) {
			markId = markId.substring(0, markId.indexOf("-原图"));
		} else if (markId.indexOf("-展开图") > 0) {
			markId = markId.substring(0, markId.indexOf("-展开图"));
		} else if (markId.indexOf("-工艺卡") > 0) {
			markId = markId.substring(0, markId.indexOf("-工艺卡"));
		} else if (markId.indexOf("-成型图") > 0) {
			markId = markId.substring(0, markId.indexOf("-成型图"));
		}
		ProcardTemplate pt = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where rootId=? and markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
						rootId, markId);
		if (pt == null) {
			return typeName + fileName + "没有找到对应的零件!<br/>";
		}
		String bltzhql = "select valueCode from CodeTranslation where type = 'sys' and keyCode='已批准零件补漏图纸'";
		String bltz = (String) totalDao.getObjectByCondition(bltzhql);
		if (bltz == null || !bltz.equals("是")) {
			if (pt.getBzStatus() != null
					&& (pt.getBzStatus().equals("已批准") || pt.getBzStatus()
							.equals("设变发起中"))
					&& (tqCount == null || tqCount == 0)) {
				return typeName + fileName + "对应的零件编制状态为:" + pt.getBzStatus()
						+ "无法上传图纸!<br/>";
			}
		}
		boolean b = true;
		String type = fileName.substring(fileName.lastIndexOf("."), fileName
				.length());
		// 覆盖之前存在的
		String sqlhadtz = "from ProcessTemplateFile where productStyle=? and type='"
				+ typeName + "'";
		if (pt.getBanci() == null || pt.getBanci() == 0) {
			sqlhadtz += " and (banci is null or banci =0)";
		} else {
			sqlhadtz += " and banci =" + pt.getBanci();
		}
		if (type.equalsIgnoreCase(".jpeg")) {
			String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
			String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".jpg")) {
			String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
			String fileName2 = fileName.replaceAll(".jpg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".PDF")) {
			fileName = fileName.replaceAll(".pdf", ".PDF");
			String fileName1 = fileName.replaceAll(".PDF", ".jpg");
			String fileName2 = fileName.replaceAll(".PDF", ".jpeg");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else {
			sqlhadtz += " and oldfileName='" + fileName + "'";
		}
		List<ProcessTemplateFile> hadFileList = totalDao.query(sqlhadtz, pt
				.getProductStyle());
		if (hadFileList != null && hadFileList.size() > 0) {
			if (pt.getBzStatus() != null
					&& (pt.getBzStatus().equals("已批准") || pt.getBzStatus()
							.equals("设变发起中"))) {
				return typeName + fileName + "对应的零件编制状态为:" + pt.getBzStatus()
						+ "无法覆盖图纸!<br/>";
			}
			int logn = 0;
			for (ProcessTemplateFile hadfile : hadFileList) {
				if (logn == 0) {
					ProcardTemplateChangeLog.addchangeLog(totalDao, pt,
							hadfile, "图纸", "删除", user, Util.getDateTime());
				}
				logn++;
				totalDao.delete(hadfile);
			}
		}
		if (b) {
			Integer banci = pt.getBanci();
			if (banci == null) {
				banci = 0;
			}
			ProcessTemplateFile ptFile = new ProcessTemplateFile();
			ptFile.setProductStyle(pt.getProductStyle());
			ptFile.setBanBenNumber(pt.getBanBenNumber());
			ptFile.setBanci(banci);
			ptFile.setMarkId(markId);
			String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS") + type;
			String realFileName2 = null;
			if (type.equalsIgnoreCase(".bmp") || type.equalsIgnoreCase(".dib")
					|| type.equalsIgnoreCase(".gif")
					|| type.equalsIgnoreCase(".jfif")
					|| type.equalsIgnoreCase(".jpe")
					|| type.equalsIgnoreCase(".jpeg")
					|| type.equalsIgnoreCase(".jpg")
					|| type.equalsIgnoreCase(".png")
					|| type.equalsIgnoreCase(".tif")
					|| type.equalsIgnoreCase(".tiff")
					|| type.equalsIgnoreCase(".ico")
					|| type.equalsIgnoreCase(".pdf")) {
				realFileName2 = "jz_" + realFileName;
			}

			ptFile.setFileName(realFileName);// 文件名
			ptFile.setFileName2(realFileName2);// 文件名
			ptFile.setOldfileName(fileName);// 文件原名称
			ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
			ptFile.setType(typeName);// 文件类型(视频文件，工艺规范)
			ptFile.setStatus("默认");// 默认,历史
			ptFile.setUserName(user.getName());// 上传人姓名
			ptFile.setUserCode(user.getCode());// 上传人工号
			ptFile.setAddTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
			ptFile.setProcessName(pt.getProName());
			ptFile.setProcessNO(null);
			Upload upload = new Upload();
			String msg = upload.UploadFile(son2, fileName, realFileName,
					"/upload/file/processTz/" + month, null);
			if (realFileName2 != null) {
				if (type.equalsIgnoreCase(".bmp")
						|| type.equalsIgnoreCase(".dib")
						|| type.equalsIgnoreCase(".gif")
						|| type.equalsIgnoreCase(".jfif")
						|| type.equalsIgnoreCase(".jpe")
						|| type.equalsIgnoreCase(".jpeg")
						|| type.equalsIgnoreCase(".jpg")
						|| type.equalsIgnoreCase(".png")
						|| type.equalsIgnoreCase(".tif")
						|| type.equalsIgnoreCase(".tiff")
						|| type.equalsIgnoreCase(".ico")) {
					// 将图纸加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_ytwrq.png");
					// 生成加章文件
					Util.markImageByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				} else if (type.equalsIgnoreCase(".pdf")) {
					// 将PDF加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_sk.png");
					// 生成加章文件
					try {
						Util.markPDFByIcon(icon_fileRealPath, cfdz + "/"
								+ realFileName, cfdz + "/" + realFileName2);
					} catch (Exception e) {
						// TODO: handle exception
						throw new RuntimeException(fileName + "文件有问题!");
					}
				}
				if (pt.getBzStatus().equals("已批准")) {// 直接加章
					ptFile.setFileName(realFileName2);// 文件名
					ptFile.setFileName2(realFileName);// 文件名
				}
			}
			totalDao.save(ptFile);
			ProcardTemplateChangeLog.addchangeLog(totalDao, pt, ptFile, "图纸",
					"添加", user, Util.getDateTime());
			if (typeName.equals("工艺规范") || typeName.equals("3D文件")) {// 工艺规范或者3D文件下发工序
				Set<ProcessTemplate> processSet = pt.getProcessTemplate();
				if (processSet != null && processSet.size() > 0) {
					for (ProcessTemplate process : processSet) {
						if (process.getDataStatus() != null
								&& process.getDataStatus().equals("删除")) {
							continue;
						}
						ProcessTemplateFile processFile = new ProcessTemplateFile();
						processFile.setBanBenNumber(pt.getBanBenNumber());
						processFile.setBanci(banci);
						processFile.setProductStyle(pt.getProductStyle());
						processFile.setMarkId(markId);
						processFile.setFileName(ptFile.getFileName());// 文件名
						processFile.setFileName2(ptFile.getFileName2());// 文件名
						processFile.setOldfileName(fileName);// 文件原名称
						processFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
						processFile.setType(typeName);// 文件类型(视频文件，工艺规范)
						processFile.setStatus("默认");// 默认,历史
						processFile.setUserName(user.getName());// 上传人姓名
						processFile.setUserCode(user.getCode());// 上传人工号
						processFile.setAddTime(Util
								.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
						processFile.setProcessName(process.getProcessName());
						processFile.setProcessNO(process.getProcessNO());
						totalDao.save(processFile);
					}
				}
			}
			sdnum++;
		}
		return b + "";
	}

	/**
	 * 
	 * @param rootId
	 *            总成id
	 * @param fileName
	 *            文件名称
	 * @param typeName
	 *            文件类型
	 * @param up
	 *            上层文件夹（工艺规范类型专用）
	 * @param son2
	 *            要导入的文件
	 * @param cfdz
	 *            地址前缀
	 * @param month
	 *            保存文件的文件夹月份名
	 * @param user
	 *            操作用户
	 * @param tqcount
	 *            特权
	 * @return
	 */
	public String checkAndUpdateTz3(Integer rootId, String fileName,
			String typeName, File up, File son2, File cfdz, String month,
			Users user, Float tqCount) {
		String markId = fileName.substring(0, fileName.lastIndexOf("."));
		if (typeName.equals("NC数冲") || typeName.equals("镭射")) {
			if (markId.startsWith("YT") || markId.startsWith("yt")) {
				markId = markId.substring(0, 3) + "." + markId.substring(3, 6)
						+ "." + markId.substring(6, markId.length());
			} else if (markId.startsWith("DKBA") || markId.startsWith("dkba")) {
				markId = markId.substring(0, 5) + "." + markId.substring(5, 8)
						+ "." + markId.substring(8, markId.length());
			}
		}
		if (markId.indexOf("-原图") > 0) {
			markId = markId.substring(0, markId.indexOf("-原图"));
		} else if (markId.indexOf("-展开图") > 0) {
			markId = markId.substring(0, markId.indexOf("-展开图"));
		} else if (markId.indexOf("-工艺卡") > 0) {
			markId = markId.substring(0, markId.indexOf("-工艺卡"));
		} else if (markId.indexOf("-成型图") > 0) {
			markId = markId.substring(0, markId.indexOf("-成型图"));
		}
		String banbenNumber = (String) totalDao
				.getObjectByCondition(
						"select banBenNumber from ProcardTemplate where rootId=? and markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
						rootId, markId);
		String banbenSql = null;
		if (banbenNumber == null || banbenNumber.length() == 0) {
			banbenSql = " and (banBenNumber is null or banBenNumber='')";
		} else {
			banbenSql = " and banBenNumber = '" + banbenNumber + "'";
		}
		List<ProcardTemplate> ptList = (List<ProcardTemplate>) totalDao
				.query(
						"from ProcardTemplate where   markId=? and productStyle='试制'  and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') "
								+ banbenSql, markId);
		if (ptList == null || ptList.size() == 0) {
			return typeName + fileName + "没有找到对应的零件!<br/>";
			// } else if (pt.getBzStatus().equals("已批准")) {
			// return typeName + fileName + "对应的零件状态为已批准不能导入图纸!<br/>";
		} else {
			ProcardTemplate pt = ptList.get(0);
			if (pt.getBzStatus() != null
					&& (pt.getBzStatus().equals("已批准") || pt.getBzStatus()
							.equals("设变发起中"))
					&& (tqCount == null || tqCount == 0)) {
				return typeName + fileName + "对应的零件编制状态为:" + pt.getBzStatus()
						+ "无法上传图纸!<br/>";
			}
		}
		boolean b = true;
		String type = fileName.substring(fileName.lastIndexOf("."), fileName
				.length());
		// 覆盖之前存在的
		String sqlhadtz = "from ProcessTemplateFile where productStyle=? and type='"
				+ typeName + "'";
		if (ptList.get(0).getBanci() == null || ptList.get(0).getBanci() == 0) {
			sqlhadtz += " and (banci is null or banci =0)";
		} else {
			sqlhadtz += " and banci =" + ptList.get(0).getBanci();
		}
		if (type.equalsIgnoreCase(".jpeg")) {
			String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
			String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".jpg")) {
			String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
			String fileName2 = fileName.replaceAll(".jpg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".PDF")) {
			fileName = fileName.replaceAll(".pdf", ".PDF");
			String fileName1 = fileName.replaceAll(".PDF", ".jpg");
			String fileName2 = fileName.replaceAll(".PDF", ".jpeg");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else {
			sqlhadtz += " and oldfileName='" + fileName + "'";
		}

		sqlhadtz += " and ((glId in(select id from ProcardTemplate where  markId='"
				+ markId
				+ "'"
				+ banbenSql
				+ " and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')) "
				+ " and processNO is null) or (processNO is not null and glId in(select id from ProcessTemplate where procardTemplate.id in "
				+ "(select id from ProcardTemplate where markId='"
				+ markId
				+ "' "
				+ banbenSql
				+ "and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')))))";
		List<ProcessTemplateFile> hadFileList = totalDao.query(sqlhadtz, "试制");
		if (hadFileList != null && hadFileList.size() > 0) {
			int logn = 0;
			for (ProcessTemplateFile hadfile : hadFileList) {
				if (logn == 0) {
					ProcardTemplateChangeLog.addchangeLog(totalDao, ptList
							.get(0), hadfile, "图纸", "删除", user, Util
							.getDateTime());
				}
				logn++;
				totalDao.delete(hadfile);
			}
		}
		if (b) {
			Integer banci = ptList.get(0).getBanci();
			if (banci == null) {
				banci = 0;
			}
			ProcessTemplateFile ptFile = new ProcessTemplateFile();
			ptFile.setProductStyle(ptList.get(0).getProductStyle());
			ptFile.setBanBenNumber(ptList.get(0).getBanBenNumber());
			ptFile.setBanci(banci);
			ptFile.setMarkId(markId);
			String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS") + type;
			String realFileName2 = null;
			if (type.equalsIgnoreCase(".bmp") || type.equalsIgnoreCase(".dib")
					|| type.equalsIgnoreCase(".gif")
					|| type.equalsIgnoreCase(".jfif")
					|| type.equalsIgnoreCase(".jpe")
					|| type.equalsIgnoreCase(".jpeg")
					|| type.equalsIgnoreCase(".jpg")
					|| type.equalsIgnoreCase(".png")
					|| type.equalsIgnoreCase(".tif")
					|| type.equalsIgnoreCase(".tiff")
					|| type.equalsIgnoreCase(".ico")
					|| type.equalsIgnoreCase(".pdf")) {
				realFileName2 = "jz_" + realFileName;
			}

			ptFile.setFileName(realFileName);// 文件名
			ptFile.setFileName2(realFileName2);// 文件名
			ptFile.setOldfileName(fileName);// 文件原名称
			ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
			ptFile.setType(typeName);// 文件类型(视频文件，工艺规范)
			ptFile.setStatus("默认");// 默认,历史
			ptFile.setUserName(user.getName());// 上传人姓名
			ptFile.setUserCode(user.getCode());// 上传人工号
			ptFile.setAddTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
			ptFile.setProcessName(ptList.get(0).getProName());
			ptFile.setProcessNO(null);
			Upload upload = new Upload();
			upload.UploadFile(son2, fileName, realFileName,
					"/upload/file/processTz/" + month, null);
			if (realFileName2 != null) {
				if (type.equalsIgnoreCase(".bmp")
						|| type.equalsIgnoreCase(".dib")
						|| type.equalsIgnoreCase(".gif")
						|| type.equalsIgnoreCase(".jfif")
						|| type.equalsIgnoreCase(".jpe")
						|| type.equalsIgnoreCase(".jpeg")
						|| type.equalsIgnoreCase(".jpg")
						|| type.equalsIgnoreCase(".png")
						|| type.equalsIgnoreCase(".tif")
						|| type.equalsIgnoreCase(".tiff")
						|| type.equalsIgnoreCase(".ico")) {
					// 将图纸加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_ytwrq.png");
					// 生成加章文件
					Util.markImageByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				} else if (type.equalsIgnoreCase(".pdf")) {
					// 将PDF加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_sk.png");
					// 生成加章文件
					Util.markPDFByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				}
				if (ptList.get(0).getBzStatus().equals("已批准")) {// 直接加章
					ptFile.setFileName(realFileName2);// 文件名
					ptFile.setFileName2(realFileName);// 文件名
				}
			}
			for (ProcardTemplate pt : ptList) {
				ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
				BeanUtils.copyProperties(ptFile, ptFile2);
				ptFile2.setGlId(pt.getId());
				ptFile2.setProductStyle("试制");
				totalDao.save(ptFile2);
			}
			ProcardTemplateChangeLog.addchangeLog(totalDao, ptList.get(0),
					ptFile, "图纸", "添加", user, Util.getDateTime());
			if (typeName.equals("工艺规范") || typeName.equals("3D文件")) {// 工艺规范或者3D文件下发工序
				for (ProcardTemplate pt : ptList) {
					Set<ProcessTemplate> processSet = pt.getProcessTemplate();
					if (processSet != null && processSet.size() > 0) {
						for (ProcessTemplate process : processSet) {
							if (process.getDataStatus() != null
									&& process.getDataStatus().equals("删除")) {
								continue;
							}
							ProcessTemplateFile processFile = new ProcessTemplateFile();
							processFile.setBanBenNumber(pt.getBanBenNumber());
							processFile.setBanci(banci);
							processFile.setProductStyle(pt.getProductStyle());
							processFile.setMarkId(markId);
							processFile.setFileName(ptFile.getFileName());// 文件名
							processFile.setFileName2(ptFile.getFileName2());// 文件名
							processFile.setOldfileName(fileName);// 文件原名称
							processFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
							processFile.setType(typeName);// 文件类型(视频文件，工艺规范)
							processFile.setStatus("默认");// 默认,历史
							processFile.setUserName(user.getName());// 上传人姓名
							processFile.setUserCode(user.getCode());// 上传人工号
							processFile.setAddTime(Util
									.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
							processFile
									.setProcessName(process.getProcessName());
							processFile.setProcessNO(process.getProcessNO());
							processFile.setGlId(process.getId());
							processFile.setProductStyle("试制");
							totalDao.save(processFile);
						}
					}
				}
			}
			sdnum++;
		}
		return b + "";
	}

	@Override
	public String zpbz(ProcardTemplate procardTem, Integer type) {
		// TODO Auto-generated method stub
		Integer msgId = null;
		ProcardTemplate old = getProcardTemplateById(procardTem.getId());
		if (procardTem.getBianzhiId() != null && procardTem.getBianzhiId() > 0) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getBianzhiId());
			if (name == null) {
				return "没有找到编制人!";
			}
			old.setBianzhiId(procardTem.getBianzhiId());
			old.setBianzhiName(name);
			if (old.getBzStatus().equals("初始")) {
				old.setBzStatus("待编制");
				msgId = procardTem.getBianzhiId();
			}
			// old.setBianzhiDate(Util.getDateTime());// 编制时间
		}
		if (procardTem.getJiaoduiId() != null && procardTem.getJiaoduiId() > 0) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getJiaoduiId());
			if (name == null) {
				return "没有找到校对人!";
			}
			old.setJiaoduiId(procardTem.getJiaoduiId());
			old.setJiaoduiName(name);
			// old.setJiaoduiDate(Util.getDateTime());// 校对时间
		}
		if (procardTem.getShenheId() != null && procardTem.getShenheId() > 0) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getShenheId());
			if (name == null) {
				return "没有找到审核人!";
			}
			old.setShenheId(procardTem.getShenheId());
			// old.setShenheDate(Util.getDateTime());// 审核时间
			old.setShenheName(name);
		}
		if (procardTem.getPizhunId() != null && procardTem.getPizhunId() > 0) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getPizhunId());
			if (name == null) {
				return "没有找到批准人!";
			}
			old.setPizhunId(procardTem.getPizhunId());
			old.setPizhunName(name);
			// old.setPizhunDate(Util.getDateTime());// 批准时间
		}
		totalDao.update(old);
		List<ProcardTemplate> sameMarkIdList = null;
		sameMarkIdList = totalDao
				.query(
						"from ProcardTemplate where markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and id<>?",
						old.getMarkId(), old.getId());
		if (sameMarkIdList != null && sameMarkIdList.size() > 0) {
			for (ProcardTemplate sameCard : sameMarkIdList) {
				sameCard.setBianzhiName(old.getBianzhiName());// 编制人
				sameCard.setBianzhiId(old.getBianzhiId());// 编制ID
				// sameCard.setBianzhiDate(old.getBianzhiDate());// 编制时间
				sameCard.setJiaoduiName(old.getJiaoduiName());// 校对人
				sameCard.setJiaoduiId(old.getJiaoduiId());// 校对ID
				// sameCard.setJiaoduiDate(old.getJiaoduiDate());// 校对时间
				sameCard.setShenheName(old.getShenheName());// 审核人
				sameCard.setShenheId(old.getShenheId());// 审核ID
				// sameCard.setShenheDate(old.getShenheDate());// 审核时间
				sameCard.setPizhunName(old.getPizhunName());// 批准人
				sameCard.setPizhunId(old.getPizhunId());// 批准ID
				// sameCard.setPizhunDate(old.getPizhunDate());// 批准时间
				totalDao.update(sameCard);
			}
		}
		if (msgId != null) {
			if (type == 0) {
				AlertMessagesServerImpl.addAlertMessages("BOM编制提醒", "件号"
						+ procardTem.getMarkId() + "需要您前往编制,请及时处理,谢谢!",
						new Integer[] { procardTem.getBianzhiId() },
						"procardTemplateGyAction_findSonsForBz.action?id="
								+ procardTem.getRootId(), true);
			} else {
				return procardTem.getBianzhiId() + "";
			}
		}
		return "true";
	}

	@Override
	public String zpBOmTree(ProcardTemplate procardTem) {
		// TODO Auto-generated method stub
		Integer msgId = null;
		ProcardTemplate old = getProcardTemplateById(procardTem.getId());
		if (procardTem.getJiaoduiId2() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getJiaoduiId2());
			if (name == null) {
				return "没有找到校对人!";
			}
			old.setJiaoduiId2(procardTem.getJiaoduiId2());
			old.setJiaoduiName2(name);
			old.setJiaoduiDate2(Util.getDateTime());// 校对时间
		}
		if (procardTem.getShenheId2() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getShenheId2());
			if (name == null) {
				return "没有找到审核人!";
			}
			old.setShenheId2(procardTem.getShenheId2());
			old.setShenheDate2(Util.getDateTime());// 审核时间
			old.setShenheName2(name);
		}
		if (procardTem.getPizhunId2() != null) {
			String name = (String) totalDao.getObjectByCondition(
					"select name from Users where id =?", procardTem
							.getPizhunId2());
			if (name == null) {
				return "没有找到批准人!";
			}
			old.setPizhunId2(procardTem.getPizhunId2());
			old.setPizhunName2(name);
			old.setPizhunDate2(Util.getDateTime());// 批准时间
		}
		totalDao.update(old);
		return "true";
	}

	@Override
	public List<ProcardTemplate> findPtListByFatherId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao
				.query(
						"from ProcardTemplate where fatherId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by xuhao",
						id);
	}

	@Override
	public String sonMoveStatus(List<ProcardTemplate> procardTemplateList,
			Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplate father = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (father == null) {
			return "没有找到目标上层零件!";
		}
		List<ProcardTemplate> otherList = totalDao
				.query(
						"from ProcardTemplate where (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and procardTemplate.id in"
								+ "(select id from ProcardTemplate where (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') and id!=? and markId=(select markId from ProcardTemplate where id=?)) order by markId",
						id, id);
		String bzStatus = father.getBzStatus();
		if (procardTemplateList != null && procardTemplateList.size() > 0) {
			String nowtime = Util.getDateTime();
			for (ProcardTemplate pt : procardTemplateList) {
				if (pt != null && pt.getId() != null) {
					ProcardTemplate old = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, pt.getId());
					boolean b1 = false;
					if (bzStatus == null || !bzStatus.equals("已批准")) {
						if (old.getProcardStyle().equals("外购")) {
							if (!isEquals(old.getQuanzi1(), pt.getQuanzi1())
									|| !isEquals(old.getQuanzi2(), pt
											.getQuanzi2())) {
								old.setHasChange("是");
								b1 = true;
								ProcardTemplateChangeLog.addchangeLog(totalDao,
										father, old, "子件", "修改", user, nowtime);
							}
							old.setQuanzi1(pt.getQuanzi1());
							old.setQuanzi2(pt.getQuanzi2());
						} else {
							if (!isEquals(old.getCorrCount(), pt.getCorrCount())) {
								old.setHasChange("是");
								b1 = true;
								ProcardTemplateChangeLog.addchangeLog(totalDao,
										father, old, "子件", "修改", user, nowtime);
							}
							old.setCorrCount(pt.getCorrCount());
						}
						if (isEquals(old.getLingliaostatus(), pt
								.getLingliaostatus())) {
							old.setHasChange("是");
							if (b1) {
								ProcardTemplateChangeLog.addchangeLog(totalDao,
										father, old, "子件", "修改", user, nowtime);
							}
						}
						old.setLingliaostatus(pt.getLingliaostatus());
					}
					old.setXuhao(pt.getXuhao());
					totalDao.update(old);
					if (otherList != null && otherList.size() > 0) {
						boolean b = false;// 开始同步标记
						for (ProcardTemplate other : otherList) {
							if (other.getMarkId().equals(old.getMarkId())) {
								b = true;
								if (bzStatus == null || !bzStatus.equals("已批准")) {
									if (other.getProcardStyle().equals("外购")) {
										if (!isEquals(other.getQuanzi1(), pt
												.getQuanzi1())
												|| !isEquals(
														other.getQuanzi2(), pt
																.getQuanzi2())) {
											old.setHasChange("是");
										}
										other.setQuanzi1(pt.getQuanzi1());
										other.setQuanzi2(pt.getQuanzi2());
									} else {
										if (isEquals(other.getCorrCount(), pt
												.getCorrCount())) {
											old.setHasChange("是");
										}
										other.setCorrCount(pt.getCorrCount());
									}
									if (isEquals(other.getLingliaostatus(), pt
											.getLingliaostatus())) {
										other.setHasChange("是");
									}
									other.setLingliaostatus(pt
											.getLingliaostatus());
								}
								other.setXuhao(pt.getXuhao());
								totalDao.update(other);
							} else if (b) {
								// 已经开始同步了，但是件号已经不同表示这个件号已经被遍历完了
								break;
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public String daoRuHwBom(File bomTree, String bomTreeFileName, Integer id) {
		// TODO Auto-generated mSethod stub
		String jymsg = "";
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + bomTreeFileName);
		try {
			FileCopyUtils.copy(bomTree, file2);

			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			jxl.Workbook rwb = Workbook.getWorkbook(is);// 创建workBook
			Sheet rs = null;
			try {
				rs = rwb.getSheet(1);
			} catch (IndexOutOfBoundsException e2) {
				rs = rwb.getSheet(0);
				e2.printStackTrace();
			}
			if (rs == null) {
				return "未找到需要处理的Sheet文件!";
			}
			int excelcolumns = rs.getRows();// 获得总行数
			if (excelcolumns > 2) {
				String hql_wgType = "select name from Category where type = '物料' and id not in (select fatherId from Category where fatherId  is not NULL)";
				List<String> umList = totalDao.query(hql_wgType);
				StringBuffer sb = new StringBuffer();
				if (umList != null && umList.size() > 0) {
					for (String um : umList) {
						sb.append("," + um);
					}
					sb.append(",");
				}
				String umStr = sb.toString();
				// Cell[] cellsAll = rs.getRow(1);
				// String ywMarkId = cellsAll[5].getContents();// 对外件号
				// ywMarkId = ywMarkId.replaceAll(" ", "");
				// ywMarkId = ywMarkId.replaceAll("	", "");
				// String createUser = cellsAll[6].getContents();// 创建人
				// createUser = createUser.replaceAll(" ", "");
				// createUser = createUser.replaceAll("	", "");
				// String createTime = cellsAll[7].getContents();// 创建时间
				// createTime = createTime.replaceAll(" ", "");
				// createTime = createTime.replaceAll("	", "");
				// String lastUser = cellsAll[8].getContents();// 最后修改人
				// lastUser = lastUser.replaceAll(" ", "");
				// lastUser = lastUser.replaceAll("	", "");
				Cell[] ywcells = rs.getRow(3);
				String ywMarkId = ywcells[4].getContents();
				if (ywMarkId != null && ywMarkId.length() > 0) {
					ywMarkId = ywMarkId.replaceAll(" ", "");
					ywMarkId = ywMarkId.replaceAll("：", ":");
					// ywMarkId = ywMarkId.replaceAll("（", "(");
					// ywMarkId = ywMarkId.replaceAll("）", ")");
					// ywMarkId = ywMarkId.replaceAll("x", "X");
					String[] ywMarkIds = ywMarkId.split(":");
					if (ywMarkIds != null && ywMarkIds.length == 2) {
						ywMarkId = ywMarkIds[1];
					} else {
						ywMarkId = null;
					}
				}
				List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
				boolean isbegin = false;
				int base = 0;
				// key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				Map<String, String> markIdAndProcardStyleMap = new HashMap<String, String>();
				for (int i = base; i < excelcolumns; i++) {
					ProcardTemplate pt = new ProcardTemplate();
					pt.setYwMarkId(ywMarkId);
					Cell[] cells = rs.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String number = "";// 序号
					try {
						number = cells[0].getContents();// 
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					number = number.replaceAll(" ", "");
					number = number.replaceAll("	", "");
					// if (number.length() == 0) {
					// if (isbegin) {
					// break;
					// } else {
					// base++;
					// continue;
					// }
					// }else if(number.equals("序号")){
					// base++;
					// continue;
					// }
					try {
						Integer num = Integer.parseInt(number);
						isbegin = true;
					} catch (Exception e) {
						// TODO: handle exception
						if (isbegin) {
							break;
						} else {
							base++;
							continue;
						}
					}
					String markId = cells[3].getContents();// 件号
					if (markId != null) {
						markId = markId.replaceAll(" ", "");
						markId = markId.replaceAll("	", "");
						markId = markId.replaceAll("_", "-");
						markId = markId.replaceAll("_s", "-S");
						markId = markId.replaceAll("-s", "-S");
						markId = markId.replaceAll("dkba", "DKBA");
						// markId = markId.replaceAll("（", "(");
						// markId = markId.replaceAll("）", ")");
						// markId = markId.replaceAll("x", "X");
					}
					String hwdrMarkId = cells[2].getContents();// 件号
					if (hwdrMarkId != null) {
						hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
						hwdrMarkId = hwdrMarkId.replaceAll("	", "");
						hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
						hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
						// hwdrMarkId = hwdrMarkId.replaceAll("（", "(");
						// hwdrMarkId = hwdrMarkId.replaceAll("）", ")");
						// hwdrMarkId = hwdrMarkId.replaceAll("x", "X");
					}
					boolean zouzui = true;
					if (markId == null || markId.length() == 0) {
						// markId="test"+i;
						jymsg += "第" + (i + 1) + "行,件号不能为空!</br>";
						// if (hwdrMarkId==null||hwdrMarkId.length() == 0) {
						// throw new RuntimeException("件号不能为空,第" + (i + 1)
						// + "行错误!");
						// }else{
						// markId=hwdrMarkId;
						// }
					} else {
						if (markId.startsWith("DKBA") && markId.length() >= 12
								&& !markId.equals("DKBA3359")
								&& markId.indexOf(".") < 0) {
							markId = markId.substring(0, 5) + "."
									+ markId.substring(5, 8) + "."
									+ markId.substring(8, markId.length());
						}
						if ((markId.equals("DKBA0.480.0128")
								|| markId.equals("DKBA0.480.0251")
								|| markId.equals("DKBA0.480.0236")
								|| markId.equals("DKBA0.480.0345")
								|| markId.equals("DKBA0.480.1381") || markId
								.equals("*"))
								&& hwdrMarkId != null
								&& hwdrMarkId.length() > 0) {
							zouzui = false;
							markId = hwdrMarkId;
						}
					}
					String banben = "";
					try {
						banben = cells[4].getContents();// 版本
					} catch (Exception e1) {
						// e1.printStackTrace();
					}
					if (!zouzui) {
						banben = null;
					}
					String proName = cells[5].getContents();// 名称
					proName = proName.replaceAll(" ", "");
					proName = proName.replaceAll("	", "");
					// proName = proName.replaceAll("（", "(");
					// proName = proName.replaceAll("）", ")");
					// proName = proName.replaceAll("x", "X");
					Integer belongLayer = null;// 层数
					Float corrCount = null;// 消耗量
					for (int jc = 6; jc <= 15; jc++) {
						String xiaohao = cells[jc].getContents();
						if (xiaohao != null && xiaohao.length() > 0) {
							try {
								corrCount = Float.parseFloat(xiaohao);
								belongLayer = jc - 5;
								break;
							} catch (Exception e) {
								jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
							}
						}
						// else if(xiaohao==null||xiaohao.length()==0){
						// jymsg+= "消耗量有异常,第" + (i + 1)
						// + "行错误!</br>";
						// }
					}
					if (corrCount == null || corrCount == 0) {
						jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
					}
					String source = cells[16].getContents();// 来源
					String unit = cells[18].getContents();// 单位
					unit = unit.replaceAll(" ", "");
					unit = unit.replaceAll("	", "");
					String biaochu = "";
					try {
						biaochu = cells[19].getContents();// 表处
						if ("A000".equals(biaochu)) {
							biaochu = "";
						}
						// biaochu = biaochu.replaceAll("）", ")");
						// biaochu = biaochu.replaceAll("x", "X");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String clType = "";
					try {
						clType = cells[20].getContents();// 材料类别（材质）
						// if ("DKBA8.052.6323".equals(markId)) {
						// System.out
						// .println(markId + " ====================");
						// }
						// clType = clType.replaceAll("（", "(");
						// clType = clType.replaceAll("）", ")");
						// clType = clType.replaceAll("x", "X");
					} catch (Exception e) {
						e.printStackTrace();
					}
					clType = clType.replaceAll(" ", "");
					clType = clType.replaceAll("	", "");
					String thislengthStr = cells[21].getContents();
					String thiswidthStr = cells[22].getContents();
					String thishigthStr = cells[23].getContents();
					Float thislength = null;
					Float thiswidth = null;
					Float thishigth = null;
					try {
						thislength = Float.parseFloat(thislengthStr);

					} catch (Exception e) {
					}
					try {
						thiswidth = Float.parseFloat(thiswidthStr);
					} catch (Exception e) {
					}
					try {
						thishigth = Float.parseFloat(thishigthStr);
					} catch (Exception e) {
					}
					String loadMarkId = "";
					try {
						loadMarkId = cells[24].getContents();// 初始总成(备注)
					} catch (Exception e) {
						e.printStackTrace();
					}
					loadMarkId = loadMarkId.replaceAll(" ", "");
					loadMarkId = loadMarkId.replaceAll("	", "");
					loadMarkId = loadMarkId.replaceAll("（", "(");
					loadMarkId = loadMarkId.replaceAll("）", ")");
					loadMarkId = loadMarkId.replaceAll("x", "X");
					loadMarkId = loadMarkId.replaceAll("_", "-");
					String wgType = null;// 外购件类型
					try {
						wgType = cells[25].getContents();// 外购件类型
						wgType = wgType.replaceAll(" ", "");
						wgType = wgType.replaceAll("	", "");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String specification = null;// 规格
					try {
						specification = cells[26].getContents();// 外购件类型
						specification = specification.replaceAll(" ", "");
						specification = specification.replaceAll("	", "");
						specification = specification.replaceAll("（", "(");
						specification = specification.replaceAll("）", ")");
						specification = specification.replaceAll("x", "X");
						specification = specification.replaceAll("_", "-");
						specification = specification.replaceAll("，", ",");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					for (int n = 10; n < 14; n++) {
						markId = markId
								.replaceAll(String.valueOf((char) n), "");
						proName = proName.replaceAll(String.valueOf((char) n),
								"");
						// tuhao = tuhao.replaceAll(String.valueOf((char) n),
						// "");
						if (banben != null && banben.length() > 0) {
							banben = banben.replaceAll(
									String.valueOf((char) n), "");
						}
						if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
						// type = type.replaceAll(String.valueOf((char) n), "");
					}
					pt.setSpecification(specification);
					pt.setProcardStyle("待定");
					boolean hasTrue = false;
					String useguige = specification;
					if (useguige == null || useguige.length() == 0) {
						useguige = loadMarkId;
					}
					if (markId.startsWith("DKBA0")
							|| markId.startsWith("dkba0")) {// 标准件不启用版本号
						banben = null;
					}
					// 技术规范号查找大类
					String procardStyle = null;
					List<String> sameMax = bmmap.get(markId + useguige);
					if (sameMax != null) {
						proName = sameMax.get(3);
						procardStyle = sameMax.get(0);
						if (sameMax.get(1).length() == 5) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%05d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setSpecification(useguige);
							pt.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setSpecification(useguige);
							pt.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}
					} else {
						CodeTranslation codeTranslation = (CodeTranslation) totalDao
								.getObjectByCondition(
										"from CodeTranslation where keyCode=? and type='技术规范'",
										markId);
						String jsType = null;
						if (codeTranslation != null) {
							jsType = codeTranslation.getValueCode();
						}
						if (jsType != null && jsType.length() > 0) {
							String hasMarkId = (String) totalDao
									.getObjectByCondition("select markId from YuanclAndWaigj where (tuhao ='"
											+ markId
											+ "-"
											+ useguige
											+ "' or tuhao ='"
											+ markId
											+ useguige
											+ "'"
											+ " or (tuhao like '"
											+ markId
											+ "-%' and specification ='"
											+ useguige + "'))");
							if (hasMarkId != null && hasMarkId.length() > 0) {
								pt.setProcardStyle("外购");
								pt.setTuhao(markId + "-" + useguige);
								pt.setSpecification(useguige);
								markId = hasMarkId;
							} else {
								proName = codeTranslation.getValueName();
								if (jsType.length() == 4) {
									jsType = jsType + ".";
								}
								// 先去原材料外购件库里查询
								String dlType = "外购件";
								// if (hasMarkId == null) {
								hasMarkId = (String) totalDao
										.getObjectByCondition("select markId from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and (tuhao ='"
												+ markId
												+ "-"
												+ useguige
												+ "' or tuhao ='"
												+ markId
												+ useguige
												+ "'"
												+ " or (tuhao like '"
												+ markId
												+ "-%' and specification ='"
												+ useguige + "'))");
								// }
								if (hasMarkId != null && hasMarkId.length() > 0) {
									markId = hasMarkId;
									dlType = "自制";
								} else {
									// 需要新增
									Integer maxInteger = 0;
									// 先遍历map
									for (String key : bmmap.keySet()) {
										List<String> samjsType = bmmap.get(key);
										if (samjsType != null
												&& samjsType.get(1).equals(
														jsType)) {
											Integer nomax = Integer
													.parseInt(samjsType.get(2));
											if (maxInteger < nomax) {
												maxInteger = nomax;
											}
											pt.setProcardStyle("外购");
										}
									}
									if (maxInteger == 0) {
										String maxNo1 = (String) totalDao
												.getObjectByCondition("select max(markId) from YuanclAndWaigj where markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null) {
											// maxNo1 = (String) totalDao
											// .getObjectByCondition("select max(trademark) from YuanclAndWaigj where clClass='原材料' and tuhao like'"
											// + jsType + "%'");
											// if (hasMarkId != null) {
											// dlType = "原材料";
											// }
											// } else {
											dlType = "外购件";
											pt.setProcardStyle("外购");
										}
										String maxNo2 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplate where  markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%' ");// 因为当零件类型为待定时原材料外购件库里没有办法查询
										String maxNo3 = (String) totalDao
												.getObjectByCondition("select max(chartNO) from ChartNOSC where  chartNO like'"
														+ jsType
														+ "%' and chartNO not like '"
														+ jsType + "%.%'");
										String maxNo4 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplatesb where sbApplyId "
														+ "in(select id from ProcardTemplateBanBenApply where processStatus not in('设变发起','分发项目组','项目主管初评','资料更新','关联并通知生产','生产后续','关闭','取消'))"
														+ " and markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%' ");
										if (maxNo1 != null
												&& maxNo1.length() > 0) {
											String[] maxStrs = maxNo1
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo2 != null
												&& maxNo2.length() > 0) {
											String[] maxStrs = maxNo2
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo3 != null
												&& maxNo3.length() > 0) {
											String[] maxStrs = maxNo3
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo4 != null
												&& maxNo4.length() > 0) {
											String[] maxStrs = maxNo4
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									maxInteger++;
									List<String> list = new ArrayList<String>();
									if ((dlType == null || dlType.equals(""))
											&& (procardStyle == null || procardStyle
													.equals(""))) {
										procardStyle = "待定";
										list.add("待定");
									} else {
										list.add(dlType);
									}
									pt.setSpecification(useguige);
									pt.setTuhao(markId + "-" + useguige);
									list.add(jsType);
									list.add(maxInteger + "");
									list.add(proName);
									bmmap.put(markId + useguige, list);
									if (jsType.length() == 5) {
										markId = jsType
												+ String.format("%05d",
														maxInteger);

									} else if (jsType.length() == 6) {
										markId = jsType
												+ String.format("%04d",
														maxInteger);
									}

									pt.setShowSize(loadMarkId);
								}
								hasTrue = true;
								// totalDao.clear();
							}
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}

					}
					// if (loadMarkId != null) {
					// if (zouzui
					// & (!markId.equals("DKBA0.480.0128")
					// && !markId.equals("DKBA0.480.0251")
					// && !markId.equals("DKBA0.480.0236")
					// && !markId.equals("DKBA0.480.0345")
					// && !markId.equals("DKBA0.480.1381"))) {
					// if (loadMarkId.contains("Al")
					// && !markId.endsWith("-AL")) {
					// markId += "-AL";
					// loadMarkId = "";
					// } else if ((loadMarkId.contains("stainless") ||
					// loadMarkId
					// .contains("Stainless"))
					// && !markId.endsWith("-S")) {
					// markId += "-S";
					// loadMarkId = "";
					// } else if (loadMarkId.contains("zinc")
					// && loadMarkId.contains("yellow")
					// && !markId.endsWith("-ZC")) {
					// markId += "-ZC";
					// loadMarkId = "";
					// }
					// }
					// }
					if (!hasTrue) {
						// 尝试将国标markId转换为系统使用的markId
						Object[] change = (Object[]) totalDao
								.getObjectByCondition(
										"select valueCode,valueName from CodeTranslation where keyCode=? or valueCode=? and type='国标'",
										markId, markId);
						if (change != null && change.length == 2
								&& change[0] != null
								&& change[0].toString().length() > 0) {
							pt.setTuhao(markId);
							markId = change[0].toString();
							if (change[1] != null
									&& change[1].toString().length() > 0) {
								proName = change[1].toString();
							}
							banben = null;// 国标件不要版本
						}
						if ("外购".equals(source) || "原材料".equals(source)) {
							ChartNOSC chartnosc = (ChartNOSC) totalDao
									.getObjectByCondition(
											" from ChartNOSC where chartNO  =? ",
											markId);
							if (chartnosc == null && isChartNOGzType(markId)) {
								// 同步添加到编码库里
								chartnosc = new ChartNOSC();
								chartnosc.setChartNO(markId);
								chartnosc.setChartcode(Util
										.Formatnumber(markId));
								chartnosc.setIsuse("YES");
								chartnosc.setRemak("BOM导入");
								chartnosc.setSjsqUsers(user.getName());
								totalDao.save(chartnosc);
							}
						}

					}
					if (markId != null
							&& (markId.startsWith("GB") || markId
									.startsWith("gb"))) {
						jymsg += "第" + (i + 1) + "行,国标件:" + markId
								+ "无法在国标编码库中查到!</br>";
					}

					if (markId != null
							&& (markId.startsWith("DKBA0") || markId
									.equals("DKBA3359"))) {

						jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
								+ "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
					}
					pt.setBelongLayer(belongLayer);
					pt.setProName(proName);
					// pt.setSpecification(type);
					if (source != null) {
						if (source.equals("外购") || source.equals("原材料")) {
							pt.setProcardStyle("外购");
						} else if (source.equals("半成品")) {
							pt.setProcardStyle("外购");
							pt.setNeedProcess("yes");
						} else if (source.equals("生产") || source.equals("自制")
								|| source.equals("组合")) {
							pt.setProcardStyle("自制");
						}
					}
					if (procardStyle != null) {
						pt.setProcardStyle(procardStyle);
					}
					if (pt.getProcardStyle() == null
							|| pt.getProcardStyle().length() == 0) {
						pt.setProcardStyle("待定");
					}
					if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, pt
								.getProcardStyle());
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(pt.getProcardStyle())) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + pt.getProcardStyle() + "]类型:["
									+ procardStyle0 + "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
					pt.setMarkId(markId);
					pt.setBanBenNumber(banben);
					pt.setUnit(unit);
					pt.setCorrCount(corrCount);
					if (pt.getTuhao() == null || pt.getTuhao().length() <= 0) {
						pt.setTuhao(hwdrMarkId);
					}

					// if (bizhong != null && bizhong.length() > 0) {
					// pt.setBili(Float.parseFloat(bizhong.toString()));
					// }
					pt.setAddcode(user.getCode());
					pt.setAdduser(user.getName());
					pt.setAddtime(nowTime);
					pt.setClType(clType);
					pt.setBiaochu(biaochu);
					pt.setBzStatus("待编制");
					pt.setBianzhiId(user.getId());
					pt.setBianzhiName(user.getName());
					pt.setProductStyle("批产");
					pt.setCardXiaoHao(1f);
					pt.setLoadMarkId(loadMarkId);
					pt.setXuhao(0f);
					pt.setThisLength(thislength);
					pt.setThisWidth(thiswidth);
					pt.setThisHight(thishigth);
					if (pt.getProcardStyle().equals("外购")
							|| pt.getProcardStyle().equals("半成品")) {
						String addSql = null;
						if (banben == null || banben.length() == 0) {
							addSql = " and (banbenhao is null or banbenhao='')";
						} else {
							addSql = " and banbenhao='" + banben + "'";
						}
						// 库中查询现有类型
						String haswgType = (String) totalDao
								.getObjectByCondition(
										"select wgType from YuanclAndWaigj where  productStyle='批产' and (status is null or status!='禁用') and markId=?  "
												+ addSql, markId);
						if (haswgType != null && haswgType.length() > 0) {
							if (wgType != null && wgType.length() > 0) {
								if (!haswgType.equals(wgType)) {
									jymsg += "第" + (i + 1) + "行错误，物料类别:"
											+ wgType + "与现有物料类别:" + haswgType
											+ "不符<br/>";
								}
							} else {
								wgType = haswgType;
							}
						} else {
							if (wgType == null || wgType.length() == 0) {
								jymsg += "第" + (i + 1) + "行错误，该外购件未填写物料类别<br/>";
							} else if (umStr == null || umStr.length() == 0
									|| !umStr.contains("," + wgType + ",")) {
								jymsg += "第" + (i + 1)
										+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
							}
						}
					}
					pt.setWgType(wgType);
					// 生产节拍 单班时长
					if (wgType != null && wgType.length() > 0) {
						Category category = (Category) totalDao
								.getObjectByCondition(
										" from Category where name = ? and id not in (select fatherId from Category where fatherId  is not NULL) and avgDeliveryTime is not null order by id desc ",
										wgType);
						if (category != null) {
							if (category.getAvgProductionTakt() != null
									&& category.getAvgDeliveryTime() != null) {
								pt.setAvgProductionTakt(category
										.getAvgProductionTakt());
								pt.setAvgDeliveryTime(category
										.getAvgDeliveryTime());
							} else {
								jymsg += "第" + (i + 1) + "行错误，该外购件物料类别("
										+ wgType + "),未填写生产节拍和配送时长!<br/>";
							}
						} else {
							jymsg += "第" + (i + 1)
									+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
						}
					}
					ptList.add(pt);
				}

				if (ptList.size() > 0) {
					ProcardTemplate totalPt = null;
					if (id != null) {// 表示导入子BOM
						totalPt = (ProcardTemplate) totalDao.getObjectById(
								ProcardTemplate.class, id);
						if (totalPt == null) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						if (totalPt.getProductStyle().equals("外购件!")) {
							throw new RuntimeException("上层零件为外购件无法导入子件!</br>");
						}
						if (totalPt.getBanbenStatus() != null
								&& totalPt.getBanbenStatus().equals("历史")) {
							throw new RuntimeException("上层零件为历史件无法导入子件!</br>");
						}
						if (totalPt.getDataStatus() != null
								&& totalPt.getDataStatus().equals("删除")) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						ywMarkId = totalPt.getYwMarkId();
					} else {
						totalPt = ptList.get(0);
						// totalPt.setYwMarkId(ywMarkId);
						// totalPt.setAddDateTime(createTime);
						// totalPt.setCreateDate(createTime);
						// totalPt.setCreateUserName(createUser);
						// totalPt.setLastUser(lastUser);
						totalPt.setProcardStyle("总成");
						totalPt.setCorrCount(1f);
						totalPt.setMaxCount(1000f);
						totalPt.setDaoruDate(new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date()));
						totalPt.setRootMarkId(totalPt.getMarkId());
						ProcardTemplate same = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=? and (dataStatus is null or dataStatus!='删除') and procardStyle='总成' and productStyle='批产'",
										totalPt.getMarkId());
						if (same != null) {
							throw new RuntimeException("该总成件号已存在,导入失败!</br>");
						}
						totalDao.save(totalPt);
						totalPt.setRootId(totalPt.getId());
						// 获取同件号自制件
						String banbenSql = null;
						if (totalPt.getBanBenNumber() == null
								|| totalPt.getBanBenNumber().length() == 0) {
							banbenSql = " and (banBenNumber is null or banBenNumber='')";
						} else {
							banbenSql = " and banBenNumber = '"
									+ totalPt.getBanBenNumber() + "'";
						}
						ProcardTemplate zzSame = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=?  and procardStyle='自制' and productStyle='批产' and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')"
												+ banbenSql, totalPt
												.getMarkId());
						if (zzSame != null) {
							copyjcMsg(zzSame, totalPt, totalPt.getYwMarkId(),
									0, user);
							totalPt.setProcardStyle("总成");
							List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
									.query(
											"from ProcardTemplate where fatherId=?",
											zzSame.getId());
							if (historySonList != null
									&& historySonList.size() > 0) {
								Set<ProcardTemplate> nowSonSet = totalPt
										.getProcardTSet();
								List<String> hadMarkIds = new ArrayList<String>();
								if (nowSonSet != null && nowSonSet.size() > 0) {
									for (ProcardTemplate nowSon : nowSonSet) {
										hadMarkIds.add(nowSon.getMarkId());
									}
								}
								for (ProcardTemplate historySon : historySonList) {
									try {
										if (!hadMarkIds.contains(historySon
												.getMarkId())) {
											saveCopyProcard(totalPt,
													historySon, "批产");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							return "导入成功,此BOM的总成在其他BOM中已存在,直接借用!";
						}
					}
					String msg = "";
					Integer sbCount = ptList.size();
					if (ptList.size() >= 1) {
						// 获取关联ProcardTemplate,关联外购件库数据
						StringBuffer markIdsb = new StringBuffer();
						for (ProcardTemplate nowpt : ptList) {
							if (id != null) {
								nowpt.setBelongLayer(totalPt.getBelongLayer()
										+ nowpt.getBelongLayer());
							}
							if (markIdsb.length() == 0) {
								markIdsb.append("'" + nowpt.getMarkId() + "'");
							} else {
								markIdsb.append(",'" + nowpt.getMarkId() + "'");
							}
						}
						if (markIdsb.length() > 0) {
							aboutptList = totalDao
									.query(" from ProcardTemplate where id in("
											+ "select min(id) from ProcardTemplate where productStyle='批产' and (dataStatus is null or dataStatus !='删除') and markId in("
											// +
											// "select min(id) from ProcardTemplate where productStyle='批产' and  markId in("
											+ markIdsb.toString()
											+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId");
							aboutwgjList = totalDao
									.query("from YuanclAndWaigj where productStyle='批产' and markId in("
											+ markIdsb.toString()
											+ ") and (banbenStatus is null or banbenStatus !='历史' ) order by markId");
						}
						if (id != null) {
							msg += addProcardTemplateTreeK3(totalPt, ptList,
									totalPt.getRootId(), -1, totalPt
											.getRootMarkId(), ywMarkId, base,
									2, user);
						} else {
							msg += addProcardTemplateTreeK3(totalPt, ptList,
									totalPt.getRootId(), 0,
									totalPt.getMarkId(), ywMarkId, base, 2,
									user);

						}
					}

					if (jymsg.length() > 0 || msg.length() > 0) {
						String msg0 = "板材粉末未计算信息:<br/>" + jsbcmsg;
						jsbcmsg = "";
						throw new RuntimeException(jymsg + msg + msg0);
					} else {
						String nowtime = Util.getDateTime();
						for (ProcardTemplate hasHistory : ptList) {
							if (id != null && hasHistory.getId() != null
									&& hasHistory.getFatherId() != null
									&& hasHistory.getFatherId().equals(id)) {
								ProcardTemplateChangeLog.addchangeLog(totalDao,
										totalPt, hasHistory, "子件", "增加", user,
										nowtime);
							}
							hasHistory.setAddcode(user.getCode());
							hasHistory.setAdduser(user.getName());
							hasHistory.setAddtime(nowTime);
							if (hasHistory.getHistoryId() != null) {
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
											.query(
													"from ProcardTemplate where fatherId=? and (dataStatus is null or dataStatus!='删除')",
													hasHistory.getHistoryId());
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplate> nowSonSet = hasHistory
												.getProcardTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplate nowSon : nowSonSet) {
												hadMarkIds.add(nowSon
														.getMarkId());
											}
										}
										for (ProcardTemplate historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcard(hasHistory,
															historySon, "批产");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							} else {
								if (hasHistory.getId() != null) {
									if (hasHistory.getBanci() == null) {
										hasHistory.setBanci(0);
									}
									if (hasHistory.getBanbenStatus() == null) {
										hasHistory.setBanbenStatus("默认");
									}
									if (hasHistory.getBzStatus() == null
											|| hasHistory.getBzStatus().equals(
													"初始")
											|| hasHistory.getBzStatus().equals(
													"待编制")) {
										hasHistory.setBzStatus("待编制");
										hasHistory.setBianzhiId(user.getId());
										hasHistory.setBianzhiName(user
												.getName());
										totalDao.update(hasHistory);
									}
								}
							}
						}
						// if (id != null) {
						// Set<ProcardTemplate> sons = totalPt
						// .getProcardTSet();
						// if (sons != null) {
						// for (ProcardTemplate son : sons) {
						// if (son.getId() >= thisminId) {
						// ProcardTemplateChangeLog.addchangeLog(
						// totalDao, totalPt, son, user,
						// Util.getDateTime());
						// }
						// }
						// }
						// }
						if (id != null) {
							List<ProcardTemplate> sameFatherList = totalDao
									.query(
											" from ProcardTemplate where markId=? and id !=? and productStyle='批产'"
													+ " and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
											totalPt.getMarkId(), totalPt
													.getId());
							if (sameFatherList != null
									&& sameFatherList.size() > 0) {
								for (ProcardTemplate sameFather : sameFatherList) {
									Set<ProcardTemplate> ptsonSet = totalPt
											.getProcardTSet();
									Set<ProcardTemplate> sameptSonSet = sameFather
											.getProcardTSet();
									for (ProcardTemplate ptson : ptsonSet) {
										boolean had = false;
										if (sameptSonSet != null
												&& sameptSonSet.size() > 0) {
											for (ProcardTemplate same : sameptSonSet) {
												if (same.getMarkId().equals(
														ptson.getMarkId())) {
													had = true;
													break;
												}
											}
										}
										if (!had) {
											saveCopyProcard(sameFather, ptson,
													"批产");
										}
									}
								}
							}

						}
						if (jsbcmsg.length() > 0) {
							String msg0 = jsbcmsg;
							jsbcmsg = "";
							return "导入成功,识别了" + sbCount + "条数据!<br/>"
									+ "板材粉末未计算信息:" + msg0;
						}

						return "导入成功,识别了" + sbCount + "条数据!";
					}
				} else if (jymsg.length() > 0) {
					return jymsg;
				}
			} else {
				return "没有数据!";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "文件异常,导入失败!";
		}
		return "导入失败,未读取到有效数据,请核实模板格式是否有误!";
	}

	@Override
	public List<ProcardTemplate> getProcardTemListById(Integer id) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
		ProcardTemplate pt = getProcardTemplateById(id);
		ptList.add(pt);
		List<ProcardTemplate> ptList2 = getSonTreeList(id);
		ptList.addAll(ptList2);
		return ptList;
	}

	public List<ProcardTemplate> getSonTreeList(Integer id) {
		List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
		List<ProcardTemplate> ptList2 = new ArrayList<ProcardTemplate>();
		List<ProcardTemplate> ptList3 = new ArrayList<ProcardTemplate>();
		ptList = totalDao
				.query(
						"from ProcardTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus!='删除')",
						id);
		if (ptList != null && ptList.size() > 0) {
			for (ProcardTemplate pt : ptList) {
				ptList2 = getSonTreeList(pt.getId());
				ptList3.addAll(ptList2);
			}
		}
		ptList.addAll(ptList3);
		return ptList;
	}

	@Override
	public String upptlv(ProcardTemplate procardTemplate) {
		// TODO Auto-generated method stub
		ProcardTemplate old = getProcardTemplateById(procardTemplate.getId());
		if (old == null) {
			return "没有找到目标,申请失败!";
		}
		List<ProcardTBanbenRelation> ptrList = totalDao
				.query(
						"from ProcardTBanbenRelation where (fmarkId=? and fbanben=?) or (smarkId=? and sbanben=?)",
						old.getMarkId(), procardTemplate.getNewBanBenNumber(),
						old.getMarkId(), procardTemplate.getNewBanBenNumber());
		if (ptrList != null && ptrList.size() > 0) {
			return "对不起此新版本号之前已使用过,请重新确认在输入!";
		}
		// 当前将版本号寄存在新版本号字段中，等审批同意在移居到版本号字段中
		old.setNewBanBenNumber(procardTemplate.getNewBanBenNumber());
		totalDao.update(old);
		ProcardTemplateBanBenApply ptbbapply = new ProcardTemplateBanBenApply();
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ptbbapply.setRemark(procardTemplate.getRemark());
		ptbbapply.setApplicantId(user.getId());
		ptbbapply.setApplicantName(user.getName());
		ptbbapply.setApplicantdept(user.getDept());
		ptbbapply.setApplyTime(Util.getDateTime());
		ptbbapply.setMarkId(old.getMarkId());// 件号
		ptbbapply.setProName(old.getProName());// 名称
		ptbbapply.setBanbenNumber(old.getBanBenNumber());// 版本号
		// ptbbapply.setNewBanbenNumber(old.getNewBanBenNumber());// 新版本号
		// ptbbapply.setProcardStyle(old.getProcardStyle());// 零件类型
		totalDao.save(ptbbapply);
		Integer epId = 0;
		try {
			epId = CircuitRunServerImpl.createProcess("BOM模板版本升级",
					ProcardTemplateBanBenApply.class, ptbbapply.getId(),
					"status", "id",
					"procardTemplateGyAction_bbApplydetail.action?id="
							+ ptbbapply.getId(), "BOM模板版本升级申请审批", true);
			if (epId != null) {
				ptbbapply.setEpId(epId);
				// ptbbapply.setStatus("未审批");
				return totalDao.update(ptbbapply) + "";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("审批流程异常，请联系管理员!");
		}
		return null;
	}

	@Override
	public ProcardTemplateBanBenApply getBanBenApplyByptId(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = getProcardTemplateById(id);
		if (pt != null) {
			if (pt.getBanBenNumber() == null
					|| pt.getBanBenNumber().length() == 0) {
				return (ProcardTemplateBanBenApply) totalDao
						.getObjectByCondition(
								"from ProcardTemplateBanBenApply where status in('未审批','审批中') and markId=? and (banbenNumber is null or banbenNumber='')",
								pt.getMarkId());
			} else {
				return (ProcardTemplateBanBenApply) totalDao
						.getObjectByCondition(
								"from ProcardTemplateBanBenApply where status in('未审批','审批中') and markId=? and banbenNumber=?",
								pt.getMarkId(), pt.getBanBenNumber());
			}
		} else {
			return null;
		}
	}

	@Override
	public Map<Integer, Object> findPtbbRelationsByCondition(
			ProcardTBanbenRelation ptbbRelation, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		if (ptbbRelation == null) {
			ptbbRelation = new ProcardTBanbenRelation();
		}
		String hql = totalDao.criteriaQueries(ptbbRelation, null, null);
		int count = totalDao.getCount(hql);
		List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, objs);
		map.put(2, count);
		return map;
	}

	@Override
	public String stoprealtion(Integer id) {
		// TODO Auto-generated method stub
		ProcardTBanbenRelation ptbbr = (ProcardTBanbenRelation) totalDao
				.getObjectById(ProcardTBanbenRelation.class, id);
		if (ptbbr != null) {
			try {
				if (ptbbr.getApplyStatus() != null
						&& ptbbr.getApplyStatus().equals("打回")) {// 再次申请
					CircuitRunServerImpl.updateCircuitRun(ptbbr.getEpId());
				} else {// 头次申请
					Integer epId;
					epId = CircuitRunServerImpl.createProcess("物料版本关系停用申请",
							ProcardTBanbenRelation.class, ptbbr.getId(),
							"status", "id", "物料版本关系停用申请审批", true);
					if (epId != null) {
						ptbbr.setEpId(epId);
					} else {
						return "审批流程有误，申请失败!";
					}
				}
				ptbbr.setApplyStatus("未审批");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return "审批流程有误，申请失败!";
			}
			return "true";
		}
		return "没有找到目标申请失败!";
	}

	@Override
	public String applySb(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (pt == null) {
			return "没有找到目标!";
		}
		if (pt.getSbStatus() != null
				&& (pt.getSbStatus().equals("未审批") || pt.getSbStatus().equals(
						"审批中"))) {
			return "已在申请中!";
		}
		try {
			if (pt.getSbStatus() != null && pt.getSbStatus().equals("打回")) {// 再次申请
				CircuitRunServerImpl.updateCircuitRun(pt.getEpId());
			} else {// 头次申请
				Integer epId;
				epId = CircuitRunServerImpl.createProcess("BOM设变申请",
						ProcardTemplate.class, pt.getId(), "sbStatus", "id",
						"BOM设变申请审批", true);
				if (epId != null) {
					pt.setEpId(epId);
				} else {
					return "审批流程有误，申请失败!";
				}
			}
			pt.setSbStatus("未审批");
			return totalDao.update(pt) + "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "审批流程有误，申请失败!";
		}
	}

	@Override
	public String applyBomTree(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (pt == null) {
			return "没有找到目标!";
		}
		if (pt.getBomApplyStatus() != null
				&& (pt.getBomApplyStatus().equals("未审批") || pt
						.getBomApplyStatus().equals("审批中"))) {
			return "已在申请中!";
		}
		String uIds = "";
		if (pt.getJiaoduiId2() != null) {
			if (uIds.length() == 0) {
				uIds += pt.getJiaoduiId2();
			} else {
				uIds += "," + pt.getJiaoduiId2();
			}
		}
		if (pt.getShenheId2() != null) {
			if (uIds.length() == 0) {
				uIds += pt.getShenheId2();
			} else {
				uIds += "," + pt.getShenheId2();
			}
		}
		if (pt.getPizhunId2() != null) {
			if (uIds.length() == 0) {
				uIds += pt.getPizhunId2();
			} else {
				uIds += "," + pt.getPizhunId2();
			}
		}
		if (uIds.length() == 0) {
			return "请先指定BOM结构审批人员!";
		}
		try {
			if (pt.getBomApplyStatus() != null
					&& pt.getBomApplyStatus().equals("打回")) {// 再次申请
				CircuitRunServerImpl.updateCircuitRun(pt.getEpId());
			} else {// 头次申请
				Integer epId2;
				epId2 = CircuitRunServerImpl.createProcessbf("BOM结构审批申请",
						ProcardTemplate.class, pt.getId(), "bomApplyStatus",
						"id", "procardTemplateGyAction_showBOMasExl.action?id="
								+ id, "BOM结构申请审批", true, uIds);
				if (epId2 != null) {
					pt.setEpId2(epId2);
				} else {
					return "审批流程有误，申请失败!";
				}
			}
			pt.setBomApplyStatus("未审批");
			return totalDao.update(pt) + "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "审批流程有误，申请失败!";
		}
	}

	@Override
	public List<ProcardTemplate> findHistoryList(Integer id) {
		// TODO Auto-generated method stub
		return totalDao
				.query(
						"from ProcardTemplate where markId=(select markId from ProcardTemplate where id=?) and banbenStatus='历史'",
						id);
	}

	@Override
	public List<ProcardTemplate> findBOMasExl(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate totalPt = getProcardTemplateById(id);
		if (totalPt == null) {
			return null;
		}
		List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
		ptList.add(totalPt);
		List<ProcardTemplate> ptList2 = findBOMasExl2(totalPt);
		if (ptList2 != null && ptList2.size() > 0) {
			ptList.addAll(ptList2);
		}
		// 判断外购件半成品
		for (int i = 0; i < ptList.size(); i++) {
			if ("外购".equals(ptList.get(i).getProcardStyle())
					&& "no".equals(ptList.get(i).getNeedProcess())) {
				ptList.remove(i);
			}
		}
		return ptList;
	}

	public List<ProcardTemplate> findBOMasExl2(ProcardTemplate father) {
		Set<ProcardTemplate> sonSet = father.getProcardTSet();
		if (sonSet == null || sonSet.size() == 0) {
			return null;
		}
		List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
		for (ProcardTemplate pt : sonSet) {
			if (pt.getBanbenStatus() == null
					|| pt.getBanbenStatus().equals("默认")) {
				ptList.add(pt);
				if (pt.getProcardTSet() != null) {
					List<ProcardTemplate> ptList2 = findBOMasExl2(pt);
					if (ptList2 != null && ptList2.size() > 0) {
						ptList.addAll(ptList2);
					}
				}
			}
		}
		return ptList;
	}

	@Override
	public List<ProcardTemplateBanBenApply> getHistoryBanBenApplyByptId(
			Integer id, String type) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = getProcardTemplateById(id);
		if (pt == null) {
			return null;
		}
		if (type == null || type.equals("all")) {
			if (pt.getBanBenNumber() == null
					|| pt.getBanBenNumber().length() == 0) {
				return totalDao
						.query(
								"from ProcardTemplateBanBenApply where markId=? and (banbenNumber is null or banbenNumber='')",
								pt.getMarkId());
			} else {
				return totalDao
						.query(
								"from ProcardTemplateBanBenApply where markId=? and banbenNumber=?",
								pt.getMarkId(), pt.getBanBenNumber());
			}
		} else if (type.equals("agree")) {
			if (pt.getBanBenNumber() == null
					|| pt.getBanBenNumber().length() == 0) {
				return totalDao
						.query(
								"from ProcardTemplateBanBenApply where status in('同意','打回') and markId=? and (banbenNumber is null or banbenNumber='')",
								pt.getMarkId());
			} else {
				return totalDao
						.query(
								"from ProcardTemplateBanBenApply where status in('同意','打回') and markId=? and banbenNumber=?",
								pt.getMarkId(), pt.getBanBenNumber());
			}
		}
		return null;
	}

	@Override
	public List<ProcardTemplate> findUPtotalPtList(String markId) {
		// TODO Auto-generated method stub
		return totalDao
				.query(
						"from ProcardTemplate where id in(select rootId from ProcardTemplate where markId =? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')) and (dataStatus is null or dataStatus!='删除')",
						markId);
	}

	@Override
	public List<ProcardTemplate> findSonToUP(int[] checkboxs, Integer id) {
		// TODO Auto-generated method stub
		if (checkboxs != null && checkboxs.length > 0) {
			List<ProcardTemplate> returnPtList = new ArrayList<ProcardTemplate>();
			List<ProcardTemplate> likePtList = totalDao
					.query(
							"from ProcardTemplate where (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除') and markId=(select markId ProcardTemplate where id=?)",
							id);
			if (likePtList == null && likePtList.size() > 0) {
				int i = 0;
				for (ProcardTemplate like : likePtList) {
					for (int rootId : checkboxs) {
						if (i == 0) {
							returnPtList.add(like);
						}
						if (like.getRootId().equals(rootId)) {
							List<ProcardTemplate> fatherLIst = getFatherList(like);
							if (fatherLIst != null && fatherLIst.size() > 0) {
								returnPtList.addAll(fatherLIst);
							}
						}
					}
					i++;
				}
			}
			return returnPtList;
		}
		return null;
	}

	private List<ProcardTemplate> getFatherList(ProcardTemplate like) {
		// TODO Auto-generated method stub
		ProcardTemplate father = like.getProcardTemplate();
		if (father != null) {
			List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
			ptList.add(father);
			List<ProcardTemplate> ptList2 = getFatherList(father);
			if (ptList2 != null) {
				ptList.addAll(ptList2);
			}
			return ptList;
		}
		return null;
	}

	@Override
	public String batchZpBz(List<ProcardTemplate> procardTemplateList) {
		// TODO Auto-generated method stub
		List<Integer> list = new ArrayList<Integer>();
		String msg = "";
		ProcardTemplate rootCard = null;
		if (procardTemplateList != null && procardTemplateList.size() > 0) {
			for (ProcardTemplate procardTem : procardTemplateList) {
				if (rootCard == null) {
					rootCard = (ProcardTemplate) totalDao
							.getObjectByCondition(
									" from ProcardTemplate where id=(select rootId from ProcardTemplate where id=?) and (dataStatus is null or dataStatus!='删除')",
									procardTem.getId());
				}
				String msg2 = zpbz(procardTem, 1);
				totalDao.clear();
				try {
					Integer alertid = Integer.parseInt(msg2);
					if (!list.contains(alertid)) {
						list.add(alertid);
					}
				} catch (Exception e) {
					// TODO: handle exception
					msg += msg2;
				}
			}

			Integer[] alertIds = new Integer[list.size()];
			for (int len = 0; len < list.size(); len++) {
				alertIds[len] = list.get(len);
			}
			AlertMessagesServerImpl.addAlertMessages("BOM编制提醒", "总成"
					+ rootCard.getMarkId() + "下有需要您前往编制的零件,请及时处理,谢谢!",
					alertIds,
					"procardTemplateGyAction_findSonsForBz.action?id="
							+ rootCard.getRootId(), true);

		}
		return "指派成功";
	}

	@Override
	public String daoRuHwSZBom(File bomTree, String bomTreeFileName, Integer id) {

		// TODO Auto-generated mSethod stub
		String jymsg = "";
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + bomTreeFileName);
		try {
			FileCopyUtils.copy(bomTree, file2);

			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			jxl.Workbook rwb = Workbook.getWorkbook(is);// 创建workBook
			Sheet rs = rwb.getSheet(1);// 获得第二张Sheet表
			int excelcolumns = rs.getRows();// 获得总行数
			if (excelcolumns > 2) {
				Cell[] ywcells = rs.getRow(3);
				String ywMarkId = ywcells[4].getContents();
				if (ywMarkId != null && ywMarkId.length() > 0) {
					ywMarkId = ywMarkId.replaceAll(" ", "");
					ywMarkId = ywMarkId.replaceAll("：", ":");
					String[] ywMarkIds = ywMarkId.split(":");
					if (ywMarkIds != null && ywMarkIds.length == 2) {
						ywMarkId = ywMarkIds[1];
					} else {
						ywMarkId = null;
					}
				}
				List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
				boolean isbegin = false;
				int base = 0;
				// key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				// 获取到的技术规范类的最大下标
				Map<String, Integer> jsgfmap = new HashMap<String, Integer>();
				Map<String, String> markIdAndProcardStyleMap = new HashMap<String, String>();
				for (int i = base; i < excelcolumns; i++) {
					Cell[] cells = rs.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String number = "";// 序号
					try {
						number = cells[0].getContents();// 
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					number = number.replaceAll(" ", "");
					number = number.replaceAll("	", "");
					try {
						Integer num = Integer.parseInt(number);
						isbegin = true;
					} catch (Exception e) {
						// TODO: handle exception
						if (isbegin) {
							break;
						} else {
							base++;
							continue;
						}
					}
					String markId = cells[3].getContents();// 件号
					if (markId != null) {
						markId = markId.replaceAll(" ", "");
						markId = markId.replaceAll("	", "");
						markId = markId.replaceAll("_", "-");
						markId = markId.replaceAll("dkba", "DKBA");
					}
					String hwdrMarkId = cells[2].getContents();// 件号
					if (hwdrMarkId != null) {
						hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
						hwdrMarkId = hwdrMarkId.replaceAll("	", "");
						hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
						hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
					}
					if (markId == null || markId.length() == 0) {
						// markId="test"+i;
						jymsg += "件号不能为空,第" + (i + 1) + "行错误!</br>";
						// if (hwdrMarkId==null||hwdrMarkId.length() == 0) {
						// throw new RuntimeException("件号不能为空,第" + (i + 1)
						// + "行错误!");
						// }else{
						// markId=hwdrMarkId;
						// }
					} else if ((markId.equals("DKBA0.480.0128")
							|| markId.equals("DKBA0.480.0251")
							|| markId.equals("DKBA0.480.0236")
							|| markId.equals("DKBA0.480.0345")
							|| markId.equals("DKBA0.480.1381") || markId
							.equals("*"))
							&& hwdrMarkId != null && hwdrMarkId.length() > 0) {
						markId = hwdrMarkId;
					}
					if (markId.startsWith("DKBA") && !markId.equals("DKBA3359")
							&& markId.length() >= 12 && markId.indexOf(".") < 0) {
						markId = markId.substring(0, 5) + "."
								+ markId.substring(5, 8) + "."
								+ markId.substring(8, markId.length());
					}
					if ("DKBA0.480.1660".equals(markId)
							|| "DKBA0.480.4324".equals(markId)) {
						System.out.println(markId + " --------------");
					}
					String banben = "";
					try {
						banben = cells[4].getContents();// 版本
					} catch (Exception e1) {
						// e1.printStackTrace();
					}
					String proName = cells[5].getContents();// 名称
					proName = proName.replaceAll(" ", "");
					proName = proName.replaceAll("	", "");
					proName = proName.trim();
					Integer belongLayer = null;// 层数
					Float corrCount = null;// 消耗量
					for (int jc = 6; jc <= 15; jc++) {
						String xiaohao = cells[jc].getContents();
						if (xiaohao != null && xiaohao.length() > 0) {
							try {
								corrCount = Float.parseFloat(xiaohao);
								belongLayer = jc - 5;
								break;
							} catch (Exception e) {
								jymsg += "消耗量有异常,第" + (i + 1) + "行错误!</br>";
							}
							// }else if(xiaohao==null||xiaohao.length()==0){
							// jymsg+= "消耗量有异常,第" + (i + 1)
							// + "行错误!</br>";
						}
					}
					if (belongLayer == null) {
						jymsg += "层数有异常,第" + (i + 1) + "行错误!</br>";
					}
					if (corrCount == null || corrCount == 0) {
						jymsg += "消耗量有异常,第" + (i + 1) + "行错误!</br>";
					}
					ProcardTemplate pt = new ProcardTemplate();
					pt.setYwMarkId(ywMarkId);
					String source = cells[16].getContents();// 来源
					String unit = cells[18].getContents();// 单位
					unit = unit.replaceAll(" ", "");
					unit = unit.replaceAll("	", "");
					String biaochu = "";
					try {
						biaochu = cells[19].getContents();// 表处
					} catch (Exception e) {
						e.printStackTrace();
					}
					String clType = "";
					try {
						clType = cells[20].getContents();// 材料类别（材质）
					} catch (Exception e) {
						e.printStackTrace();
					}
					clType = clType.replaceAll(" ", "");
					clType = clType.replaceAll("	", "");
					String thislengthStr = cells[21].getContents();
					String thiswidthStr = cells[22].getContents();
					String thishigthStr = cells[23].getContents();
					Float thislength = null;
					Float thiswidth = null;
					Float thishigth = null;
					try {
						thislength = Float.parseFloat(thislengthStr);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						thiswidth = Float.parseFloat(thiswidthStr);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						thishigth = Float.parseFloat(thishigthStr);
					} catch (Exception e) {
						// TODO: handle exception
					}
					String loadMarkId = "";
					try {
						loadMarkId = cells[24].getContents();// 初始总成
					} catch (Exception e) {
						e.printStackTrace();
					}
					loadMarkId = loadMarkId.replaceAll(" ", "");
					loadMarkId = loadMarkId.replaceAll("	", "");
					loadMarkId = loadMarkId.replaceAll("_", "-");
					loadMarkId = loadMarkId.replaceAll("X", "x");
					String wgType = null;// 外购件类型
					try {
						wgType = cells[25].getContents();// 外购件类型
						wgType = wgType.replaceAll(" ", "");
						wgType = wgType.replaceAll("	", "");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String specification = null;// 规格
					try {
						specification = cells[26].getContents();// 外购件类型
						specification = specification.replaceAll(" ", "");
						specification = specification.replaceAll("	", "");
						specification = specification.replaceAll("（", "(");
						specification = specification.replaceAll("）", ")");
						specification = specification.replaceAll("x", "X");
						specification = specification.replaceAll("_", "-");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					for (int n = 10; n < 14; n++) {
						markId = markId
								.replaceAll(String.valueOf((char) n), "");
						if (proName != null && proName.length() > 0) {
							proName = proName.replaceAll(String
									.valueOf((char) n), "");
						}
						// tuhao = tuhao.replaceAll(String.valueOf((char) n),
						// "");
						if (banben != null && banben.length() > 0) {
							banben = banben.replaceAll(
									String.valueOf((char) n), "");
						}
						if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
						// type = type.replaceAll(String.valueOf((char) n), "");
					}
					pt.setSpecification(specification);
					pt.setProcardStyle("待定");
					boolean hasTrue = false;
					// 技术规范号查找大类
					String procardStyle = null;
					String userguige = specification;
					if (userguige == null || userguige.length() == 0) {
						userguige = loadMarkId;
					}
					if ((markId.startsWith("DKBA0") || markId
							.startsWith("dkba0"))
							|| (specification != null && (specification
									.startsWith("DKBA0") || specification
									.startsWith("dkba0")))) {// 标准件不启用版本号
						banben = null;
					}
					List<String> sameMax = bmmap.get(markId + userguige);
					if (sameMax != null) {
						proName = sameMax.get(3);
						procardStyle = sameMax.get(0);
						if (sameMax.get(1).length() == 5) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%05d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setTuhao(markId + "-" + userguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setTuhao(markId + "-" + userguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }
						}
						hasTrue = true;
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}
					} else {
						CodeTranslation codeTranslation = (CodeTranslation) totalDao
								.getObjectByCondition(
										"from CodeTranslation where keyCode=? and type='技术规范'",
										markId);
						String jsType = null;
						if (codeTranslation != null) {
							jsType = codeTranslation.getValueCode();
						}
						if (jsType != null && jsType.length() > 0) {
							String hasMarkId = (String) totalDao
									.getObjectByCondition("select markId from YuanclAndWaigj where (tuhao ='"
											+ markId
											+ "-"
											+ userguige
											+ "' or tuhao ='"
											+ markId
											+ userguige
											+ "'"
											+ " or (tuhao like '"
											+ markId
											+ "-%' and specification ='"
											+ userguige + "'))");
							if (hasMarkId != null && hasMarkId.length() > 0) {
								pt.setTuhao(markId + "-" + userguige);
								markId = hasMarkId;
								pt.setProcardStyle("外购");
							} else {
								proName = codeTranslation.getValueName();
								if (jsType.length() == 4) {
									jsType = jsType + ".";
								}
								// 先去原材料外购件库里查询
								String dlType = "外购";
								// if (hasMarkId == null) {
								hasMarkId = (String) totalDao
										.getObjectByCondition("select markId from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and "
												+ "(tuhao ='"
												+ markId
												+ "-"
												+ userguige
												+ "' or tuhao ='"
												+ markId
												+ userguige
												+ "'"
												+ " or (tuhao like '"
												+ markId
												+ "-%' and specification ='"
												+ userguige + "'))");
								// }
								if (hasMarkId != null && hasMarkId.length() > 0) {
									markId = hasMarkId;
								} else {
									// 需要新增
									Integer maxInteger = 0;
									// 先遍历map
									for (String key : bmmap.keySet()) {
										List<String> samjsType = bmmap.get(key);
										if (samjsType != null
												&& samjsType.get(1).equals(
														jsType)) {
											Integer nomax = Integer
													.parseInt(samjsType.get(2));
											pt.setProcardStyle("外购");
											if (maxInteger < nomax) {
												maxInteger = nomax;
											}
										}
									}
									if (maxInteger == 0) {
										String maxNo1 = (String) totalDao
												.getObjectByCondition("select max(markId) from YuanclAndWaigj where markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null) {
											// maxNo1 = (String) totalDao
											// .getObjectByCondition("select max(trademark) from YuanclAndWaigj where clClass='原材料' and tuhao like'"
											// + jsType + "%'");
											// if (hasMarkId != null) {
											// dlType = "原材料";
											// }
											// } else {
											dlType = "外购";
											pt.setProcardStyle("外购");
										}
										String maxNo2 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplate where  markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");// 因为当零件类型为待定时原材料外购件库里没有办法查询
										String maxNo3 = (String) totalDao
												.getObjectByCondition("select max(chartNO) from ChartNOSC where  chartNO like'"
														+ jsType
														+ "%' and chartNO not like '"
														+ jsType + "%.%' ");
										String maxNo4 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplatesb where sbApplyId "
														+ "in(select id from ProcardTemplateBanBenApply where processStatus not in('设变发起','分发项目组','项目主管初评','资料更新','关联并通知生产','生产后续','关闭','取消'))"
														+ " and markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%' ");
										if (maxNo1 != null
												&& maxNo1.length() > 0) {
											String[] maxStrs = maxNo1
													.split(jsType);
											try {
												Integer nowMax = Integer
														.parseInt(maxStrs[1]);
												if (maxInteger < nowMax) {
													maxInteger = nowMax;
												}
											} catch (Exception e) {
												// TODO: handle exception
											}
										}
										if (maxNo2 != null
												&& maxNo2.length() > 0) {
											String[] maxStrs = maxNo2
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo3 != null
												&& maxNo3.length() > 0) {
											String[] maxStrs = maxNo3
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo4 != null
												&& maxNo4.length() > 0) {
											String[] maxStrs = maxNo4
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									maxInteger++;
									List<String> list = new ArrayList<String>();
									if ((dlType == null || dlType.equals(""))
											&& (procardStyle == null || procardStyle
													.equals(""))) {
										procardStyle = "待定";
										list.add("待定");
									} else {
										list.add(dlType);
									}
									pt.setTuhao(markId + "-" + userguige);
									pt.setSpecification(userguige);
									list.add(jsType);
									list.add(maxInteger + "");
									list.add(proName);
									bmmap.put(markId + userguige, list);
									if (jsType.length() == 5) {
										markId = jsType
												+ String.format("%05d",
														maxInteger);

									} else if (jsType.length() == 6) {
										markId = jsType
												+ String.format("%04d",
														maxInteger);
									}
									pt.setShowSize(loadMarkId);
								}
								hasTrue = true;
								// totalDao.clear();
							}
						}
						// 存放到编码库
						ChartNOSC chartnosc = (ChartNOSC) totalDao
								.getObjectByCondition(
										" from ChartNOSC where chartNO  =? ",
										markId);
						if (chartnosc == null && isChartNOGzType(markId)) {
							// 同步添加到编码库里
							chartnosc = new ChartNOSC();
							chartnosc.setChartNO(markId);
							chartnosc.setChartcode(Util.Formatnumber(markId));
							chartnosc.setIsuse("YES");
							chartnosc.setRemak("BOM导入");
							chartnosc.setSjsqUsers(user.getName());
							totalDao.save(chartnosc);
						}

					}
					// if (loadMarkId != null) {
					// if ((!markId.equals("DKBA0.480.0128")
					// && !markId.equals("DKBA0.480.0251")
					// && !markId.equals("DKBA0.480.0236")
					// && !markId.equals("DKBA0.480.0345") && !markId
					// .equals("DKBA0.480.1381"))) {
					// if (loadMarkId.contains("Al")
					// && !markId.endsWith("-AL")) {
					// markId += "-AL";
					// loadMarkId = "";
					// } else if ((loadMarkId.contains("stainless") ||
					// loadMarkId
					// .contains("Stainless"))
					// && !markId.endsWith("-S")) {
					// markId += "-S";
					// loadMarkId = "";
					// } else if (loadMarkId.contains("zinc")
					// && loadMarkId.contains("yellow")
					// && !markId.endsWith("-ZC")) {
					// markId += "-ZC";
					// loadMarkId = "";
					// }
					// }
					// }
					if (!hasTrue) {
						// 尝试将国标markId转换为系统使用的markId
						Object[] change = (Object[]) totalDao
								.getObjectByCondition(
										"select valueCode,valueName from CodeTranslation where keyCode=? or valueCode=? and type='国标'",
										markId, markId);
						if (change != null && change.length == 2
								&& change[0] != null
								&& change[0].toString().length() > 0) {
							pt.setTuhao(markId);
							markId = change[0].toString();
							if (change[1] != null
									&& change[1].toString().length() > 0) {
								proName = change[1].toString();
							}
							banben = null;// 国标件不要版本
						}
						if ("外购".equals(source) || "原材料".equals(source)) {
							ChartNOSC chartnosc = (ChartNOSC) totalDao
									.getObjectByCondition(
											" from ChartNOSC where chartNO  =? ",
											markId);
							if (chartnosc == null && isChartNOGzType(markId)) {
								// 同步添加到编码库里
								chartnosc = new ChartNOSC();
								chartnosc.setChartNO(markId);
								chartnosc.setChartcode(Util
										.Formatnumber(markId));
								chartnosc.setIsuse("YES");
								chartnosc.setRemak("BOM导入");
								chartnosc.setSjsqUsers(user.getName());
								totalDao.save(chartnosc);
							}
						}

					}
					if (markId != null
							&& (markId.startsWith("GB") || markId
									.startsWith("gb"))) {
						jymsg += "第" + (i + 1) + "行,国标件:" + markId
								+ "无法在国标编码库中查到!</br>";
					}

					if (markId != null
							&& (markId.startsWith("DKBA0") || markId
									.equals("DKBA3359"))) {
						jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
								+ "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
					}
					pt.setBelongLayer(belongLayer);
					pt.setProName(proName);
					// pt.setSpecification(type);

					if (source != null && source.length() > 0) {
						if (source.equals("外购") || source.equals("半成品")) {
							pt.setProcardStyle("外购");
						} else if (source.equals("生产") || source.equals("自制")) {
							pt.setProcardStyle("自制");
						}
					}
					if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, pt
								.getProcardStyle());
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(pt.getProcardStyle())) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + pt.getProcardStyle()
									+ "]类型,:[" + procardStyle0
									+ "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
					pt.setMarkId(markId);
					pt.setBanBenNumber(banben);
					pt.setUnit(unit);
					pt.setCorrCount(corrCount);
					// pt.setTuhao(tuhao);
					// if (bizhong != null && bizhong.length() > 0) {
					// pt.setBili(Float.parseFloat(bizhong.toString()));
					// }
					pt.setAddcode(user.getCode());
					pt.setAdduser(user.getName());
					pt.setAddtime(nowTime);
					pt.setClType(clType);
					pt.setBiaochu(biaochu);
					pt.setBzStatus("初始");
					pt.setProductStyle("试制");
					pt.setCardXiaoHao(1f);
					pt.setLoadMarkId(loadMarkId);
					pt.setXuhao(0f);
					pt.setThisLength(thislength);
					pt.setThisWidth(thiswidth);
					pt.setThisHight(thishigth);
					if ("外购".equals(pt.getProcardStyle())) {
						String wgType2 = (String) totalDao
								.getObjectByCondition(
										"select wgType from ProcardTemplate where markId = ? and wgType is not null and wgType <> '' ",
										markId);
						if (wgType2 != null && wgType2.length() > 0) {
							// if (wgType != null && wgType.length() > 0) {
							// if(!wgType2.equals(wgType)){
							// jymsg += "第" + (i + 1) + "行错误，物料类别:" + wgType
							// + "与现有BOM物料类别:" + wgType2 + "不符<br/>";
							// }
							// } else {
							wgType = wgType2;
							// }
						}
					}
					if (pt.getProcardStyle().equals("外购")
							|| pt.getProcardStyle().equals("半成品")) {
						String addSql = null;
						if (banben == null || banben.length() == 0) {
							addSql = " and (banbenhao is null or banbenhao='')";
						} else {
							addSql = " and banbenhao='" + banben + "'";
						}
						// 库中查询现有类型
						String haswgType = (String) totalDao
								.getObjectByCondition(
										"select wgType from YuanclAndWaigj where  (status is null or status!='禁用') and markId=?  "
												+ addSql, markId);
						if (haswgType != null && haswgType.length() > 0) {
							// if (wgType != null && wgType.length() > 0) {
							// if(!haswgType.equals(wgType)){
							// jymsg += "第" + (i + 1) + "行错误，物料类别:" + wgType
							// + "与现有外购件库中物料类别:" + haswgType + "不符<br/>";
							// }
							// } else {
							wgType = haswgType;
							// }
						} else {
							if (wgType == null || wgType.length() == 0) {
								jymsg += "第" + (i + 1) + "行错误，该外购件未填写物料类别<br/>";
							}
						}
					}
					pt.setWgType(wgType);
					// 生产节拍 单班时长
					if (wgType != null && wgType.length() > 0) {
						Category category = (Category) totalDao
								.getObjectByCondition(
										" from Category where name = ? and id not in (select fatherId from Category where fatherId  is not NULL) order by id desc ",
										wgType);
						if (category != null) {
							if (category.getAvgProductionTakt() != null
									&& category.getAvgDeliveryTime() != null) {
								pt.setAvgProductionTakt(category
										.getAvgProductionTakt());
								pt.setAvgDeliveryTime(category
										.getAvgDeliveryTime());
								// } else {
								// jymsg += "第" + (i + 1) + "行错误，该外购件物料类别("
								// + wgType + "),未填写生产节拍和配送时长!<br/>";
							}
						} else {
							jymsg += "第" + (i + 1) + "行错误，该外购件物料类别(" + wgType
									+ "),不在现有物料类别库中!<br/>";
						}
					}
					ptList.add(pt);
					// 板材粉末计算开始
					// if ("自制".equals(pt.getProcardStyle())) {
					// if ( pt.getClType().length() > 0
					// && pt.getThisLength() != null
					// && pt.getThisLength() > 0
					// && pt.getThisWidth() != null
					// && pt.getThisWidth() > 0
					// && pt.getThisHight() != null
					// && pt.getThisHight() > 0) {
					// if(pt.getClType()!=null && pt.getClType().length()>0){
					// PanelSize panel = null;
					// Float hudu = null;
					// if(pt.getThisHight()!=null && pt.getThisHight()>0){
					// hudu = pt.getThisHight();
					// }else{
					// ProcardTemplate oldpt = (ProcardTemplate)
					// totalDao.getObjectByCondition(" from  ProcardTemplate where markId = ? and thisHight>0",
					// pt.getMarkId());
					// hudu = oldpt.getThisHight();
					// }
					//								 
					// String hql_panel=
					// " from PanelSize where caizhi = ? and  "
					// + hudu+ " BETWEEN  fristThickness AND endThickness ";
					// panel = (PanelSize) totalDao
					// .getObjectByCondition(hql_panel,pt.getClType());
					// if (panel != null) {
					// String specification = hudu+ "x" + panel.getSize();
					// String hql_wgj =
					// " from YuanclAndWaigj where caizhi = ? and specification like '%"
					// + specification + "%'";
					// YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao
					// .getObjectByCondition(hql_wgj, pt
					// .getClType());
					// if (wgj != null) {
					// ProcardTemplate wgpt = new ProcardTemplate();
					// wgpt.setMarkId(wgj.getMarkId());// 件号
					// wgpt.setWgType(wgj.getWgType());// 物料类别
					// wgpt.setCaizhi(wgj.getCaizhi());// 材质
					// wgpt.setProName(wgj.getName());// 名称\
					// wgpt.setBanBenNumber(wgj.getBanbenhao());
					// wgpt.setKgliao(wgj.getKgliao());// 供料属性
					// wgpt
					// .setBelongLayer(pt
					// .getBelongLayer() + 1);// 层数
					// wgpt.setUnit(wgj.getUnit());// 单位
					// wgpt.setProcardStyle("外购");
					// wgpt
					// .setProductStyle(pt
					// .getProductStyle());
					// wgpt.setQuanzi1(1f);
					// Float quanzhi2 = 0f;
					// quanzhi2 = (pt.getThisHight()
					// * pt.getThisLength()
					// * pt.getThisWidth() * wgj
					// .getDensity()) / 1000000;
					// wgpt.setCorrCount(quanzhi2);
					// ptList.add(wgpt);
					// } else {
					// jymsg += "第" + (i+1 ) + "行错误，没有在外购件库找到"
					// + "材质为：" + pt.getClType()
					// + "，规格为:" + specification
					// + "的板材。 !<br/>";
					// }
					// } else {
					// jymsg += "第" + (i+1) + "行错误，没有在板材尺寸库中找到"
					// + "材质为：" + pt.getClType() + "，厚度为:"
					// + hudu + "的板材。 !<br/>";
					//
					// }
					// }
					// if(pt.getBiaochu()!=null && pt.getBiaochu().length()>0){
					// Float mianji =
					// pt.getThisLength()*pt.getThisWidth()*pt.getCorrCount();
					// if (mianji > 0) {
					// PanelSize fuhefenmo = (PanelSize) totalDao
					// .getObjectByCondition(
					// " from PanelSize where caizhi= ? and type = '复合粉末'",
					// pt.getBiaochu());
					// if (fuhefenmo != null) {
					// String hql_fenmowgj1 =
					// " from  YuanclAndWaigj where name like '%"
					// + fuhefenmo.getFenmo1()
					// + "%' and wgType = '粉末'";
					// if(fuhefenmo.getFenmo1().indexOf("华彩")==-1){
					// hql_fenmowgj1 += " and name not like '%华彩%'";
					// }
					// YuanclAndWaigj fenmowgj1 = (YuanclAndWaigj) totalDao
					// .getObjectByCondition(hql_fenmowgj1);
					// if (fenmowgj1 != null) {
					// ProcardTemplate wgpt = new ProcardTemplate();
					// wgpt.setMarkId(fenmowgj1.getMarkId());// 件号
					// wgpt.setWgType(fenmowgj1.getWgType());// 物料类别
					// wgpt.setCaizhi(fenmowgj1.getCaizhi());// 材质
					// wgpt.setProName(fenmowgj1.getName());// 名称\
					// wgpt.setBanBenNumber(fenmowgj1.getBanbenhao());
					// wgpt.setKgliao(fenmowgj1.getKgliao());// 供料属性
					// wgpt
					// .setBelongLayer(pt
					// .getBelongLayer() + 1);// 层数
					// wgpt.setUnit(fenmowgj1.getUnit());// 单位
					// wgpt.setProcardStyle("外购");
					// wgpt
					// .setProductStyle(pt
					// .getProductStyle());
					// wgpt.setQuanzi1(1f);
					// Float quanzhi2 = 0f;
					// quanzhi2 =
					// (mianji*fenmowgj1.getAreakg()*fuhefenmo.getMiancount1())
					// / 1000000;
					// wgpt.setCorrCount(quanzhi2);
					// ptList.add(wgpt);
					// }
					// String hql_fenmowgj2 =
					// " from  YuanclAndWaigj where name like '%"
					// + fuhefenmo.getFenmo2()
					// + "%' and wgType = '粉末'";
					// if(fuhefenmo.getFenmo2().indexOf("华彩")==-1){
					// hql_fenmowgj2 += " and name not like '%华彩%'";
					// }
					// YuanclAndWaigj fenmowgj2 = (YuanclAndWaigj) totalDao
					// .getObjectByCondition(hql_fenmowgj2);
					// if (fenmowgj2 != null) {
					// ProcardTemplate wgpt = new ProcardTemplate();
					// wgpt.setMarkId(fenmowgj2.getMarkId());// 件号
					// wgpt.setWgType(fenmowgj2.getWgType());// 物料类别
					// wgpt.setCaizhi(fenmowgj2.getCaizhi());// 材质
					// wgpt.setProName(fenmowgj2.getName());// 名称\
					// wgpt.setBanBenNumber(fenmowgj2.getBanbenhao());
					// wgpt.setKgliao(fenmowgj2.getKgliao());// 供料属性
					// wgpt
					// .setBelongLayer(pt
					// .getBelongLayer() + 1);// 层数
					// wgpt.setUnit(fenmowgj2.getUnit());// 单位
					// wgpt.setProcardStyle("外购");
					// wgpt
					// .setProductStyle(pt
					// .getProductStyle());
					// wgpt.setQuanzi1(1f);
					// Float quanzhi2 = 0f;
					// quanzhi2 =
					// (mianji*fenmowgj2.getAreakg()*fuhefenmo.getMiancount1())
					// / 1000000;
					// wgpt.setCorrCount(quanzhi2);
					// ptList.add(wgpt);
					// }
					// } else {
					// String hql_fenmowgj =
					// " from  YuanclAndWaigj where name like '%"
					// + pt.getBiaochu()
					// + "%' and wgType = '粉末'";
					// if(pt.getBiaochu().indexOf("华彩")==-1){
					// hql_fenmowgj += " and name not like '%华彩%'";
					// }
					// YuanclAndWaigj fenmowgj = (YuanclAndWaigj) totalDao
					// .getObjectByCondition(hql_fenmowgj);
					// if (fenmowgj != null) {
					// ProcardTemplate wgpt = new ProcardTemplate();
					// wgpt.setMarkId(fenmowgj.getMarkId());// 件号
					// wgpt.setWgType(fenmowgj.getWgType());// 物料类别
					// wgpt.setCaizhi(fenmowgj.getCaizhi());// 材质
					// wgpt.setProName(fenmowgj.getName());// 名称\
					// wgpt.setBanBenNumber(fenmowgj.getBanbenhao());
					// wgpt.setKgliao(fenmowgj.getKgliao());// 供料属性
					// wgpt
					// .setBelongLayer(pt
					// .getBelongLayer() + 1);// 层数
					// wgpt.setUnit(fenmowgj.getUnit());// 单位
					// wgpt.setProcardStyle("外购");
					// wgpt
					// .setProductStyle(pt
					// .getProductStyle());
					// wgpt.setQuanzi1(1f);
					// Float quanzhi2 = 0f;
					// quanzhi2 = (mianji*fenmowgj.getAreakg()) / 1000000;
					// wgpt.setCorrCount(quanzhi2);
					// ptList.add(wgpt);
					// }
					// }
					// }
					// }
					//
					// }
					//						
					// }

				}
				Integer sbCount = ptList.size();
				List<ProcardTemplate> sametotalptList = new ArrayList<ProcardTemplate>();
				if (ptList.size() > 0) {
					ProcardTemplate totalPt = null;
					if (id != null) {// 表示导入子BOM
						totalPt = (ProcardTemplate) totalDao.getObjectById(
								ProcardTemplate.class, id);
						if (totalPt == null) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						if (totalPt.getProductStyle().equals("外购件!")) {
							throw new RuntimeException("上层零件为外购件无法导入子件!</br>");
						}
						if (totalPt.getBanbenStatus() != null
								&& totalPt.getBanbenStatus().equals("历史")) {
							throw new RuntimeException("上层零件为历史件无法导入子件!</br>");
						}
						if (totalPt.getDataStatus() != null
								&& totalPt.getDataStatus().equals("删除")) {
							throw new RuntimeException("没有找到对应的上层零件!</br>");
						}
						if (totalPt.getBzStatus() != null
								&& (totalPt.getBzStatus().equals("已批准") || totalPt
										.getBzStatus().equals("设变发起中"))) {
							throw new RuntimeException("上层零件编制状态为："
									+ totalPt.getBzStatus() + "不允许导入子件!</br>");
						}
						ywMarkId = totalPt.getYwMarkId();
						// 增加日志

					} else {
						totalPt = ptList.get(0);
						// totalPt.setYwMarkId(ywMarkId);
						// totalPt.setAddDateTime(createTime);
						// totalPt.setCreateDate(createTime);
						// totalPt.setCreateUserName(createUser);
						// totalPt.setLastUser(lastUser);
						totalPt.setProcardStyle("总成");
						totalPt.setCorrCount(1f);
						totalPt.setMaxCount(1000f);
						totalPt.setDaoruDate(new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(new Date()));
						totalPt.setRootMarkId(totalPt.getMarkId());
						ProcardTemplate same = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=? and productStyle = '试制' and procardStyle='总成' and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
										totalPt.getMarkId());
						if (same != null) {
							throw new RuntimeException("该总成件号已存在,导入失败!");
						}
						totalPt.setBelongLayer(1);
						totalDao.save(totalPt);
						totalPt.setRootId(totalPt.getId());
						// 获取同件号自制件
						String banbenSql = null;
						if (totalPt.getBanBenNumber() == null
								|| totalPt.getBanBenNumber().length() == 0) {
							banbenSql = " and (banBenNumber is null or banBenNumber='')";
						} else {
							banbenSql = " and banBenNumber = '"
									+ totalPt.getBanBenNumber() + "'";
						}
						ProcardTemplate zzSame = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=?  and procardStyle='自制' and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')"
												+ banbenSql, totalPt
												.getMarkId());
						if (zzSame != null) {
							copyjcMsg(zzSame, totalPt, totalPt.getYwMarkId(),
									0, user);
							totalPt.setProcardStyle("总成");
							List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
									.query(
											"from ProcardTemplate where fatherId=?",
											zzSame.getId());
							if (historySonList != null
									&& historySonList.size() > 0) {
								Set<ProcardTemplate> nowSonSet = totalPt
										.getProcardTSet();
								List<String> hadMarkIds = new ArrayList<String>();
								if (nowSonSet != null && nowSonSet.size() > 0) {
									for (ProcardTemplate nowSon : nowSonSet) {
										hadMarkIds.add(nowSon.getMarkId());
									}
								}
								for (ProcardTemplate historySon : historySonList) {
									try {
										if (!hadMarkIds.contains(historySon
												.getMarkId())) {
											saveCopyProcard(totalPt,
													historySon, "试制");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							return "导入成功,此BOM的总成在其他BOM中已存在,直接借用!";
						}
					}

					String msg = "";

					if (ptList.size() >= 1) {

						// 获取关联ProcardTemplate,关联外购件库数据
						StringBuffer markIdsb = new StringBuffer();
						for (ProcardTemplate nowpt : ptList) {
							if (id != null) {
								nowpt.setBelongLayer(totalPt.getBelongLayer()
										+ nowpt.getBelongLayer());
							}
							if (markIdsb.length() == 0) {
								markIdsb.append("'" + nowpt.getMarkId() + "'");
							} else {
								markIdsb.append(",'" + nowpt.getMarkId() + "'");
							}
						}
						if (markIdsb.length() > 0) {
							aboutptList = totalDao
									.query(" from ProcardTemplate where  id in("
											+ "select min(id) from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and   markId in("
											+ markIdsb.toString()
											+ ") group by markId,banBenNumber,banci,banbenStatus,dataStatus ) order by markId");
							aboutwgjList = totalDao
									.query("from YuanclAndWaigj where  markId in("
											+ markIdsb.toString()
											+ ") and (banbenStatus is null or banbenStatus !='历史' ) order by markId");
						}
						List<Banbenxuhao> banbenxuhaoList = totalDao
								.query("from Banbenxuhao");
						banBenxuhaoMap = new HashMap<String, Integer>();
						for (Banbenxuhao banebnxuhao : banbenxuhaoList) {
							banBenxuhaoMap.put(banebnxuhao.getBanbenNumber(),
									banebnxuhao.getBanbenxuhao());
						}
						try {
							if (id != null) {
								msg += addProcardTemplateTreeK3(totalPt,
										ptList, totalPt.getRootId(), -1,
										totalPt.getRootMarkId(), ywMarkId,
										base, 2, user);
							} else {
								msg += addProcardTemplateTreeK3(totalPt,
										ptList, totalPt.getRootId(), 0, totalPt
												.getMarkId(), ywMarkId, base,
										2, user);
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
					}

					if (msg.length() > 0 || jymsg.length() > 0) {
						String msg0 = jsbcmsg;
						jsbcmsg = "";
						throw new RuntimeException(jymsg + msg + msg0);
					} else {
						String nowtime = Util.getDateTime();
						for (ProcardTemplate hasHistory : ptList) {
							hasHistory.setAddcode(user.getCode());
							hasHistory.setAdduser(user.getName());
							hasHistory.setAddtime(nowTime);
							if (id != null && hasHistory.getId() != null
									&& hasHistory.getFatherId() != null
									&& hasHistory.getFatherId().equals(id)) {
								ProcardTemplateChangeLog.addchangeLog(totalDao,
										totalPt, hasHistory, "子件", "增加", user,
										nowtime);
							}
							if (hasHistory.getHistoryId() != null) {
								if (!hasHistory.getProcardStyle().equals("外购")) {
									List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
											.query(
													"from ProcardTemplate where fatherId=?",
													hasHistory.getHistoryId());
									if (historySonList != null
											&& historySonList.size() > 0) {
										Set<ProcardTemplate> nowSonSet = hasHistory
												.getProcardTSet();
										List<String> hadMarkIds = new ArrayList<String>();
										if (nowSonSet != null
												&& nowSonSet.size() > 0) {
											for (ProcardTemplate nowSon : nowSonSet) {
												hadMarkIds.add(nowSon
														.getMarkId());
											}
										}
										for (ProcardTemplate historySon : historySonList) {
											try {
												if (!hadMarkIds
														.contains(historySon
																.getMarkId())) {
													saveCopyProcard(hasHistory,
															historySon, "试制");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							} else {
								if (hasHistory.getId() != null) {
									if (hasHistory.getBanci() == null) {
										hasHistory.setBanci(0);
									}
									if (hasHistory.getBanbenStatus() == null) {
										hasHistory.setBanbenStatus("默认");
									}
									if (hasHistory.getBzStatus() == null
											|| hasHistory.getBzStatus().equals(
													"初始")
											|| hasHistory.getBzStatus().equals(
													"待编制")) {
										hasHistory.setBzStatus("待编制");
										hasHistory.setBianzhiId(user.getId());
										hasHistory.setBianzhiName(user
												.getName());
										totalDao.update(hasHistory);
									}
								}
							}
						}
						if (id != null) {
							Set<ProcardTemplate> sons = totalPt
									.getProcardTSet();
							if (sons != null) {
								for (ProcardTemplate son : sons) {
									if (son.getId() >= thisminId) {
										ProcardTemplateChangeLog.addchangeLog(
												totalDao, totalPt, son, user,
												Util.getDateTime());
									}
								}
							}
						}
						// 同步其他同件号
						if (id != null) {
							List<ProcardTemplate> sameFatherList = totalDao
									.query(
											" from ProcardTemplate where markId=? and id !=? and productStyle='试制'"
													+ " and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
											totalPt.getMarkId(), totalPt
													.getId());
							if (sameFatherList != null
									&& sameFatherList.size() > 0) {
								for (ProcardTemplate sameFather : sameFatherList) {
									Set<ProcardTemplate> ptsonSet = totalPt
											.getProcardTSet();
									Set<ProcardTemplate> sameptSonSet = sameFather
											.getProcardTSet();
									for (ProcardTemplate ptson : ptsonSet) {
										boolean had = false;
										if (sameptSonSet != null
												&& sameptSonSet.size() > 0) {
											for (ProcardTemplate same : sameptSonSet) {
												if (same.getMarkId().equals(
														ptson.getMarkId())) {
													had = true;
													break;
												}
											}
										}
										if (!had) {
											saveCopyProcard(sameFather, ptson,
													"试制");
										}
									}
								}
							}

						}

						if (jsbcmsg.length() > 0) {
							String msg0 = jsbcmsg;
							jsbcmsg = "";
							return "导入成功,识别了" + sbCount + "条数据!<br/>"
									+ "板材粉末未计算信息:" + msg0;
						}
						return "导入成功,识别了" + sbCount + "条数据!";
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		}
		return "导入失败,未读取到有效数据,请核实模板格式是否有误!";
	}

	@Override
	public String updateUnuploadTzname(Integer id, String path) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		List<ProcardTemplate> ptList = totalDao.query(
				"from ProcardTemplate where rootId=?", id);
		if (pt != null || ptList.size() == 0) {
			String month = Util.getDateTime("yyyy-MM");
			Users user = Util.getLoginUser();
			if (pt.getYwMarkId() != null || pt.getMarkId() != null) {
				// 件号查找
				File file = new File(path + "\\" + pt.getYwMarkId());
				if (!file.exists()) {
					file = new File(path + "\\" + pt.getMarkId());
				}
				if (!file.exists()) {
					return "对不起没有找到对应的文件";
				}
				File[] sonList1 = file.listFiles();
				if (sonList1 != null && sonList1.length > 0) {
					// 将文件放入特定位置
					StringBuffer drmes = new StringBuffer();
					for (File son1 : sonList1) {
						if (son1.isDirectory() && son1.getName() != null) {
							if (son1.getName().equals("3D文件")) {
								int sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										String fileName = son2.getName();
										String markId = fileName.substring(0,
												fileName.lastIndexOf("."));
										String type = fileName.substring(
												fileName.lastIndexOf("."),
												fileName.length());
										if ((markId.startsWith("DKBA") || markId
												.startsWith("dkba"))
												&& markId.length() >= 12
												&& !markId.equals("DKBA3359")
												&& (markId.indexOf(".") > 6 || markId
														.indexOf(".") < 0)) {
											markId = markId.substring(0, 5)
													+ "."
													+ markId.substring(5, 8)
													+ "."
													+ markId.substring(8,
															markId.length());
										}
										markId = markId.replaceAll("_", "-");
										String newFilename = son1
												.getAbsolutePath()
												+ "\\" + markId + type;
										son2.renameTo(new File(newFilename));
										sdnum++;
									}
								}
								drmes.append("修改成功3D文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("SOP")) {
								int sdnum = 0;// 上传成功SOP文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										String fileName = son2.getName();
										String markId = fileName.substring(0,
												fileName.lastIndexOf("."));
										String type = fileName.substring(
												fileName.lastIndexOf("."),
												fileName.length());
										if ((markId.startsWith("DKBA") || markId
												.startsWith("dkba"))
												&& markId.length() >= 12
												&& !markId.equals("DKBA3359")
												&& (markId.indexOf(".") > 6 || markId
														.indexOf(".") < 0)) {
											markId = markId.substring(0, 5)
													+ "."
													+ markId.substring(5, 8)
													+ "."
													+ markId.substring(8,
															markId.length());
										}
										markId = markId.replaceAll("_", "-");
										String newFilename = son1
												.getAbsolutePath()
												+ "\\" + markId + type;
										son2.renameTo(new File(newFilename));
										sdnum++;
									}
								}
								drmes.append("修改成功SOP文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("SAP")) {
								int sdnum = 0;// 上传成功SAP文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										String fileName = son2.getName();
										String markId = fileName.substring(0,
												fileName.lastIndexOf("."));
										String type = fileName.substring(
												fileName.lastIndexOf("."),
												fileName.length());
										if ((markId.startsWith("DKBA") || markId
												.startsWith("dkba"))
												&& markId.length() >= 12
												&& !markId.equals("DKBA3359")
												&& (markId.indexOf(".") > 6 || markId
														.indexOf(".") < 0)) {
											markId = markId.substring(0, 5)
													+ "."
													+ markId.substring(5, 8)
													+ "."
													+ markId.substring(8,
															markId.length());
										}
										markId = markId.replaceAll("_", "-");
										String newFilename = son1
												.getAbsolutePath()
												+ "\\" + markId + type;
										son2.renameTo(new File(newFilename));
										sdnum++;
									}
								}
								drmes.append("修改成功SAP文件:" + sdnum + "份<br/>");
							}
							if (son1.getName().equals("3D模型")) {
								int sdnum = 0;// 上传成功3D文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String fileName = son2.getName();
											String markId = fileName.substring(
													0, fileName
															.lastIndexOf("."));
											String type = fileName.substring(
													fileName.lastIndexOf("."),
													fileName.length());
											if ((markId.startsWith("DKBA") || markId
													.startsWith("dkba"))
													&& markId.length() >= 12
													&& !markId
															.equals("DKBA3359")
													&& (markId.indexOf(".") > 6 || markId
															.indexOf(".") < 0)) {
												markId = markId.substring(0, 5)
														+ "."
														+ markId
																.substring(5, 8)
														+ "."
														+ markId
																.substring(
																		8,
																		markId
																				.length());
											}
											markId = markId
													.replaceAll("_", "-");
											String newFilename = son1
													.getAbsolutePath()
													+ "\\" + markId + type;
											son2
													.renameTo(new File(
															newFilename));
											sdnum++;
										}
									}
								}
								drmes.append("修改成功3D模型:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
							if (son1.getName().equals("视频文件")
									|| son1.getName().equals("视频")) {
								int spnum = 0;// 上传成功视频文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String fileName = son2.getName();
											String markId = fileName.substring(
													0, fileName
															.lastIndexOf("."));
											String type = fileName.substring(
													fileName.lastIndexOf("."),
													fileName.length());
											if ((markId.startsWith("DKBA") || markId
													.startsWith("dkba"))
													&& markId.length() >= 12
													&& (markId.indexOf(".") > 6 || markId
															.indexOf(".") < 0)) {
												markId = markId.substring(0, 5)
														+ "."
														+ markId
																.substring(5, 8)
														+ "."
														+ markId
																.substring(
																		8,
																		markId
																				.length());
											}
											markId = markId
													.replaceAll("_", "-");
											String newFilename = son1
													.getAbsolutePath()
													+ "\\" + markId + type;
											son2
													.renameTo(new File(
															newFilename));
											spnum++;
										}
									}
								}
								drmes.append("修改成功视频文件:" + spnum + "份<br/>");
							}
							if (son1.getName().equals("工艺规范")) {
								int gynum = 0;// 上传成功工艺规范文件数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (son2.isDirectory()
												&& son2.getName() != null) {
											if (son2.getName().contains("工艺卡")
													|| son2.getName().contains(
															"原图")
													|| son2.getName().contains(
															"展开图")) {
												File[] sonList3 = son2
														.listFiles();
												if (sonList3 != null
														&& sonList3.length > 0) {
													for (File son3 : sonList3) {
														if (!son3.isDirectory()
																&& son3
																		.getName() != null
																&& !"Thumbs.db"
																		.equals(son3
																				.getName())) {
															String fileName = son3
																	.getName();
															String markId = fileName
																	.substring(
																			0,
																			fileName
																					.lastIndexOf("."));
															String type = fileName
																	.substring(
																			fileName
																					.lastIndexOf("."),
																			fileName
																					.length());
															if ((markId
																	.startsWith("DKBA") || markId
																	.startsWith("dkba"))
																	&& (markId
																			.indexOf(".") > 6 || markId
																			.indexOf(".") < 0)) {
																markId = markId
																		.substring(
																				0,
																				5)
																		+ "."
																		+ markId
																				.substring(
																						5,
																						8)
																		+ "."
																		+ markId
																				.substring(
																						8,
																						markId
																								.length());
															}
															markId = markId
																	.replaceAll(
																			"_",
																			"-");
															String newFilename = son2
																	.getAbsolutePath()
																	+ "\\"
																	+ markId
																	+ type;
															son3
																	.renameTo(new File(
																			newFilename));
															gynum++;
														}
													}
												}
											}
										}

									}
								}
								drmes.append("修改成功工艺规范文件:" + gynum + "份<br/>");
							}
							if (son1.getName().equals("成型图")) {
								int sdnum = 0;// 上传成功成型图数量
								File[] sonList2 = son1.listFiles();
								if (sonList2 != null && sonList2.length > 0) {
									for (File son2 : sonList2) {
										if (!son2.isDirectory()
												&& son2.getName() != null) {
											String fileName = son2.getName();
											String markId = fileName.substring(
													0, fileName
															.lastIndexOf("."));
											String type = fileName.substring(
													fileName.lastIndexOf("."),
													fileName.length());
											if ((markId.startsWith("DKBA") || markId
													.startsWith("dkba"))
													&& markId.length() >= 12
													&& (markId.indexOf(".") > 6 || markId
															.indexOf(".") < 0)) {
												markId = markId.substring(0, 5)
														+ "."
														+ markId
																.substring(5, 8)
														+ "."
														+ markId
																.substring(
																		8,
																		markId
																				.length());
											}
											markId = markId
													.replaceAll("_", "-");
											String newFilename = son1
													.getAbsolutePath()
													+ "\\" + markId + type;
											son2
													.renameTo(new File(
															newFilename));
											sdnum++;
										}
									}
								}
								drmes.append("导入成功成型图:" + sdnum + "份<br/>");
								// return "导入成功！";
							}
						}
					}
					return drmes.toString();
				}
			}
		}
		return "对不起没有找到目标BOM";
	}

	@Override
	public List findTzfg(String name1, String name2) {
		// TODO Auto-generated method stub
		List<String> list = (List<String>) totalDao
				.query("select more from Logging where objectName='ProcessTemplateFile' and userName='"
						+ name1
						+ "' and status='删除' and more like '%\"userName\":\""
						+ name2 + "\"%' ");
		if (list != null && list.size() > 0) {
			Gson gson = new Gson();
			List<ProcessTemplateFile> fileList = new ArrayList<ProcessTemplateFile>();
			for (String json : list) {
				ProcessTemplateFile file = gson.fromJson(json,
						ProcessTemplateFile.class);
				fileList.add(file);
			}
			return fileList;
		}
		return null;
	}

	@Override
	public List<ProcardTemplate> getGongxuPt(Integer rootId) {
		String hql = "from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus!='删除') and (procardstyle in ('总成','自制','组合') or (needProcess ='yes' and procardstyle='外购')) and id not in(select procardTemplate.id from ProcessTemplate where (dataStatus is null or dataStatus !='删除') and procardTemplate.id in (select id from ProcardTemplate where rootId=? and (procardstyle in ('总成','自制','组合') or (needProcess ='yes' and procardstyle='外购'))))";
		List<ProcardTemplate> list = totalDao.query(hql, rootId, rootId);
		return list;
	}

	/****
	 * 一键导入工序
	 * 
	 * @param rootId
	 * @return
	 */
	@Override
	public Object[] updatedaoRuProcessTemplate(ProcardTemplate pt,
			Statement sql, ResultSet rs) {
		String addTime = Util.getDateTime();
		String processNOs = "";
		String processNames = "";
		String MarkIds = "";
		String errmess = "";
		int sucesscount = 0;
		int errorcount = 0;
		List<ProcessTemplate> processTemplatelist = new ArrayList<ProcessTemplate>();
		List<ProcardTemplate> sameList = totalDao
				.query(
						"from ProcardTemplate where  markId =? and (procardStyle in('总成','自制') or (procardStyle ='外购' and needProcess='yes')) and productStyle=?  and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')",
						pt.getMarkId(), pt.getProductStyle());
		try {
			rs = sql
					.executeQuery("select gyorder,procedurekind,proceduretext,TimeQty,ToolNo,ProMachine from GykTechnics where partid in "
							+ "(select partid from GykProDes where partno ='"
							+ pt.getMarkId()
							+ "') and gyorder is not null order by gyorder ");
			if (rs != null) {
				String processNO = "";
				String processName = "";
				String processExplain = "";
				Float timeQty = null;// 点数
				String toolNo = "";// 工装编号
				String proMachine = "";// 设备
				int oneerror = 0;
				List<String> hadprocessNoList = new ArrayList<String>();
				while (rs.next()) {
					processNO = rs.getString(1);
					if (hadprocessNoList.contains(processNO)) {
						processNO = (Integer.parseInt(processNO) + 1) + "";
						hadprocessNoList.add(processNO);
					} else {
						hadprocessNoList.add(processNO);
					}
					processName = rs.getString(2);
					processName = processName.replaceAll(" ", "");
					processName = processName.replaceAll("（", "(");
					processName = processName.replaceAll("）", ")");
					if ("装配".equals(processName)) {
						processName = "组装";
					}
					// processExplain = rs.getString(3);
					// if (processExplain != null
					// && processExplain.length() > 0) {
					// for (int n = 10; n < 14; n++) {
					// processExplain = processExplain
					// .replaceAll(String
					// .valueOf((char) n), "");
					// }
					// }

					timeQty = rs.getFloat(4);
					toolNo = rs.getString(5);
					if (toolNo != null && toolNo.length() > 0) {
						toolNo = toolNo.replaceAll(" ", "");
						toolNo = toolNo.replaceAll("（", "(");
						toolNo = toolNo.replaceAll("）", ")");
					}
					proMachine = rs.getString(6);
					if (proMachine != null && proMachine.length() > 0) {
						proMachine = proMachine.replaceAll(" ", "");
						proMachine = proMachine.replaceAll("（", "(");
						proMachine = proMachine.replaceAll("）", ")");
					}
					// 查询工序库，判断工序是否存在
					String hq_ProcessGzstore = "from ProcessGzstore where processName=?";
					ProcessGzstore processGzstore = (ProcessGzstore) totalDao
							.getObjectByCondition(hq_ProcessGzstore,
									processName);
					if (processGzstore != null) {
						for (ProcardTemplate pt2 : sameList) {
							// Set<ProcessTemplate> processSet2 =
							// pt2
							// .getProcessTemplate();
							String hql_process = "from ProcessTemplate pc where pc.procardTemplate.id=? and (pc.dataStatus is null or pc.dataStatus !='删除')  order by pc.processNO";
							Integer processCount = totalDao.getCount(
									hql_process, pt2.getId());
							if (processCount == null || processCount == 0) {
								ProcessTemplate processTemplate = new ProcessTemplate();
								BeanUtils.copyProperties(processGzstore,
										processTemplate, new String[] { "id" });
								if (processGzstore.getProductStyle() != null
										&& processGzstore.getProductStyle()
												.equals("外委")) {
									processTemplate.setProcessStatus("no");
								}
								processTemplate.setAddTime(addTime);
								processTemplate.setAddUser(Util.getLoginUser()
										.getName());
								processTemplate.setProcessNO(Integer
										.parseInt(processNO));
								if (processTemplate.getOpcaozuojiepai() == null) {
									processTemplate.setOpcaozuojiepai(3f);
								}
								if (processTemplate.getOpshebeijiepai() == null) {
									processTemplate.setOpshebeijiepai(1f);
								}
								if (processTemplate.getGzzhunbeijiepai() == null) {
									processTemplate.setGzzhunbeijiepai(1f);
								}
								if (processTemplate.getGzzhunbeicishu() == null) {
									processTemplate.setGzzhunbeicishu(1f);
								}
								processTemplate.setAllJiepai(processTemplate
										.getOpcaozuojiepai()
										+ processTemplate.getOpshebeijiepai()
										+ processTemplate.getGzzhunbeijiepai()
										* processTemplate.getGzzhunbeicishu());
								// processTemplate
								// .setProcessExplain(processExplain);
								// 点数
								if (timeQty != null) {
									processTemplate.setProcesdianshu(timeQty
											.doubleValue());
								}
								// 工装编号
								if (toolNo != null) {
									String hql = "from Gzstore where matetag=?";
									Gzstore gzstore = (Gzstore) totalDao
											.getObjectByQuery(hql, toolNo);
									if (gzstore == null) {
										gzstore = new Gzstore();
										gzstore.setNumber(toolNo);
										totalDao.save(gzstore);
									}
									if (gzstore != null) {
										processTemplate.setGzstoreId(gzstore
												.getId());
										processTemplate.setNumber(gzstore
												.getNumber());
										processTemplate.setMatetag(gzstore
												.getMatetag());
									}
								}
								// 设备

								processTemplate.setProcardTemplate(pt2);
								processTemplatelist.add(processTemplate);
							}
						}
					} else {
						return new Object[] {
								"0",
								pt.getMarkId() + "的比对工序[" + processName
										+ "],在PEBS系统中的工序库中未查询到!<br/>" };
					}
				}
				if (processTemplatelist != null
						&& processTemplatelist.size() > 0 && oneerror == 0) {
					List<Integer> processNoList = new ArrayList<Integer>();
					String banciSql = "";
					if (pt.getBanci() == null || pt.getBanci() == 0) {
						banciSql = " and (banci is null or banci =0)";
					} else {
						banciSql = " and banci=" + pt.getBanci();
					}
					for (ProcessTemplate processTemplate : processTemplatelist) {
						totalDao.save(processTemplate);
						if (!processNoList.contains(processTemplate
								.getProcessNO())) {
							ProcardTemplateChangeLog
									.addchangeLog(totalDao, processTemplate
											.getProcardTemplate(),
											processTemplate, "工序", "增加", Util
													.getLoginUser(), Util
													.getDateTime());
							processNoList.add(processTemplate.getProcessNO());
						} else {
							if (pt.getProductStyle() == null
									|| !pt.getProductStyle().equals("试制")) {
								continue;
							}
						}
						Float hasgxCunt = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcessTemplate where procardTemplate.markId=? and (dataStatus is null and dataStatus !='删除') and (procardTemplate.banbenStatus is null or procardTemplate.banbenStatus !='历史') and (procardTemplate.dataStatus is null or procardTemplate.dataStatus!='删除')",
										pt.getMarkId());
						List<ProcessTemplateFile> files = null;
						if (hasgxCunt == null || hasgxCunt == 0) {// 无工序
							// 添加图纸
							if (pt.getProductStyle() != null
									&& pt.getProductStyle().equals("试制")) {
								files = totalDao
										.query(
												"from ProcessTemplateFile where markId=? and processNO is null and type in('3D文件','工艺规范') and glId="
														+ pt.getId() + banciSql,
												pt.getMarkId());
							} else {
								files = totalDao
										.query(
												"from ProcessTemplateFile where markId=? and processNO is null and type in('3D文件','工艺规范') and glId is null "
														+ banciSql, pt
														.getMarkId());
							}
						} else {
							// 添加图纸
							if (pt.getProductStyle() != null
									&& pt.getProductStyle().equals("试制")) {
								files = totalDao
										.query(
												"from ProcessTemplateFile where markId=? and processNO is null "
														+ banciSql
														+ " and glId=?  and oldfileName in"
														+ "(select oldfileName from ProcessTemplateFile where markId=? "
														+ banciSql
														+ " and processNO !=? and processName !=? group by oldfileName)",
												pt.getMarkId(), pt.getId(), pt
														.getMarkId(),
												processTemplate.getProcessNO(),
												processTemplate
														.getProcessName());
							} else {
								files = totalDao
										.query(
												"from ProcessTemplateFile where markId=? and processNO is null "
														+ banciSql
														+ " and glId is null and oldfileName in"
														+ "(select oldfileName from ProcessTemplateFile where markId=? "
														+ banciSql
														+ " and processNO !=? and processName !=? group by oldfileName)",
												pt.getMarkId(), pt.getMarkId(),
												processTemplate.getProcessNO(),
												processTemplate
														.getProcessName());
							}

						}
						if (files != null && files.size() > 0) {
							List<String> oldFilenames = new ArrayList<String>();
							for (ProcessTemplateFile file : files) {
								if (!oldFilenames.contains(file
										.getOldfileName())) {
									Float hasCount = null;
									if (pt.getProductStyle() != null
											&& pt.getProductStyle()
													.equals("试制")) {
										hasCount = 0f;
									} else {
										hasCount = (Float) totalDao
												.getObjectByCondition(
														"select count(*) from ProcessTemplateFile where markId=? and glId is null and processNO=? and processName =? "
																+ banciSql
																+ " and oldfileName=?",
														pt.getMarkId(),
														processTemplate
																.getProcessNO(),
														processTemplate
																.getProcessName(),
														file.getOldfileName());
									}

									if (hasCount == null || hasCount == 0) {
										ProcessTemplateFile newFile = new ProcessTemplateFile();
										BeanUtils.copyProperties(file, newFile,
												new String[] { "id",
														"processNO",
														"processName" });
										newFile.setProcessNO(processTemplate
												.getProcessNO());
										newFile.setProcessName(processTemplate
												.getProcessName());
										if (pt.getProductStyle() != null
												&& pt.getProductStyle().equals(
														"试制")) {
											newFile.setGlId(processTemplate
													.getId());
											newFile.setProductStyle("试制");
										}
										totalDao.save(newFile);
									}
									oldFilenames.add(file.getOldfileName());
								}
							}
						}

					}
					sucesscount++;
				} else {
					return new Object[] { "0", pt.getMarkId() + "未比对到工序<br/>" };
				}
			} else {
				return new Object[] { "0", pt.getMarkId() + "未比对到工序<br/>" };
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[] { "0", "连接数据源异常!<br/>" };
		}
		return new Object[] { "1", "导入成功" };

	}

	/****
	 * 统一更新工装编号
	 * 
	 * @param sql
	 * @param rs
	 * @return
	 */
	public String updategzprocess(Statement sql, ResultSet rs) {
		String addTime = Util.getDateTime();
		String processNOs = "";
		String processNames = "";
		String MarkIds = "";
		String errmess = "";
		int sucesscount = 0;
		int errorcount = 0;
		String mess = "";
		try {
			rs = sql
					.executeQuery("select partno from GykProDes where  partid in "
							+ "(select partid from GykTechnics where toolno is not null)");
			if (rs != null) {
				while (rs.next()) {
					String partno = rs.getString(1);
					String hql_processt = "from ProcessTemplate where "
							+ "procardTemplate.id in (select id from ProcardTemplate where markid=?) "
							+ "and gzstoreId is null "
							+ "and (dataStatus is null or dataStatus !='删除') ";
					List<ProcessTemplate> list_process = totalDao.query(
							hql_processt, partno);
					for (ProcessTemplate processTemplate : list_process) {
						try {
							Statement sql2 = null;
							String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 加载JDBC驱动
							String dbURL = "jdbc:sqlserver://192.168.2.37:1433;databaseName=yyf_ytDB"; // 连接服务器和数据库sample
							String userName = "tiaomao"; // 默认用户名
							String userPwd = "a123456"; // 密码
							Connection dbConn = null;
							try {
								Class.forName(driverName);
								dbConn = DriverManager.getConnection(dbURL,
										userName, userPwd);
								sql2 = dbConn.createStatement();
							} catch (Exception e1) {
								e1.printStackTrace();
								ActionContext.getContext().getApplication()
										.put("BOMDaoru", null);
								MKUtil
										.writeJSON("导入失败，无法连接数据源(192.168.2.37:1433;databaseName=yyf_ytDB)!");
								return null;
							}
							ResultSet rs2 = sql2
									.executeQuery("select gyorder,procedurekind,proceduretext,TimeQty,ToolNo,ProMachine from GykTechnics where partid in "
											+ "(select partid from GykProDes where partno ='"
											+ partno
											+ "') and procedurekind='"
											+ processTemplate.getProcessName()
											+ "' and gyorder is not null and toolno is not null order by gyorder ");
							if (rs2 != null) {
								String processNO = "";
								String processName = "";
								String processExplain = "";
								Float timeQty = null;// 点数
								String toolNo = "";// 工装编号
								String proMachine = "";// 设备
								int oneerror = 0;
								List<String> hadprocessNoList = new ArrayList<String>();
								while (rs2.next()) {
									// processExplain = rs.getString(3);
									// if (processExplain != null
									// && processExplain.length() > 0) {
									// for (int n = 10; n < 14; n++) {
									// processExplain = processExplain
									// .replaceAll(String
									// .valueOf((char) n), "");
									// }
									// }

									timeQty = rs2.getFloat(4);
									toolNo = rs2.getString(5);
									if (toolNo != null && toolNo.length() > 0) {
										toolNo = toolNo.replaceAll(" ", "");
										toolNo = toolNo.replaceAll("（", "(");
										toolNo = toolNo.replaceAll("）", ")");
									}
									proMachine = rs2.getString(6);
									if (proMachine != null
											&& proMachine.length() > 0) {
										proMachine = proMachine.replaceAll(" ",
												"");
										proMachine = proMachine.replaceAll("（",
												"(");
										proMachine = proMachine.replaceAll("）",
												")");
									}
									// 工装编号
									if (toolNo != null) {
										String hql = "from Gzstore where number=?";
										Gzstore gzstore = (Gzstore) totalDao
												.getObjectByQuery(hql, toolNo);
										if (gzstore == null) {
											gzstore = new Gzstore();
											gzstore.setNumber(toolNo);
											totalDao.save(gzstore);
											totalDao.clear();
										}
										if (gzstore != null) {
											processTemplate
													.setGzstoreId(gzstore
															.getId());
											processTemplate.setNumber(gzstore
													.getNumber());
											processTemplate.setMatetag(gzstore
													.getMatetag());
											totalDao.update(processTemplate);
											totalDao.clear();
										}
									}
								}
							}
							rs2.close();
							sql2.close();
							dbConn.close();
						} catch (Exception e) {
							e.printStackTrace();
							mess += "id:" + processTemplate.getId() + "异常:"
									+ e.getMessage() + "<br/>";
						}
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return mess;

	}

	@Override
	public Map<Integer, Object> getProcardTemDifference(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate now = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		if (now != null && now.getBanci() != null && now.getBanci() > 0) {
			ProcardTemplate old = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where markId=? and procardStyle=? and (banci<? or banci is null) and (dataStatus is null or dataStatus!='删除')order by banci desc ",
							now.getMarkId(), now.getProcardStyle(), now
									.getProductStyle(), now.getBanci());
			if (old != null) {
				map.put(1, true);
				map.put(2, now);
				map.put(3, old);
				Map<String, String> tgMap = new HashMap<String, String>();
				// 通过版本关系对应出来的下层零件信息
				List<ProcardTemplate> oldSonList = totalDao
						.query(
								"from ProcardTemplate where id in"
										+ "(select min(p1.id) from ProcardTemplate p1,ProcardTBanbenRelation p2 where p2.fmarkId=? and p2.fbanci=? and p1.banci=p2.sbanci and p1.markId=p2.smarkId and (p1.dataStatus is null or p1.dataStatus!='删除') and (p2.dataStatus is null or p2.dataStatus!='删除') group by p1.markId,p1.banci)",
								old.getMarkId(), old.getBanci() == null ? 0
										: old.getBanci());
				// 获取当前的下层零件信息
				List<ProcardTemplate> nowSonList = totalDao.query(
						"from ProcardTemplate where procardTemplate.id=?", id);
				List<String> oldSonMarkIdList = new ArrayList<String>();
				List<String> nowSonMarkIdList = new ArrayList<String>();
				if (oldSonList != null && oldSonList.size() > 0) {
					for (ProcardTemplate oldpt : nowSonList) {
						if (!oldSonMarkIdList.contains(oldpt.getMarkId())) {
							oldSonMarkIdList.add(oldpt.getMarkId());
						}
						if (nowSonList != null && nowSonList.size() > 0) {
							for (ProcardTemplate nowpt : nowSonList) {
								if (!nowSonMarkIdList.contains(nowpt
										.getMarkId())) {
									nowSonMarkIdList.add(nowpt.getMarkId());
								}
								if (oldpt.getMarkId().equals(nowpt.getMarkId())) {
									// 相同的零件对比一下用量
									if (oldpt.getProcardStyle().equals("外购")) {
										if (isEquals(oldpt.getQuanzi1(), nowpt
												.getQuanzi1())
												&& isEquals(oldpt.getQuanzi2(),
														nowpt.getQuanzi2())) {
											tgMap.put(nowpt.getMarkId(),
													"white");
										} else {
											tgMap.put(nowpt.getMarkId(),
													"yellow");
										}
									} else {
										if (isEquals(oldpt.getCorrCount(),
												nowpt.getCorrCount())) {
											tgMap.put(nowpt.getMarkId(),
													"white");
										} else {
											tgMap.put(nowpt.getMarkId(),
													"yellow");
										}
									}
									break;
								}
							}

						}
					}
				} else {
					if (nowSonList != null && nowSonList.size() > 0) {
						for (ProcardTemplate nowpt : nowSonList) {
							if (!nowSonMarkIdList.contains(nowpt.getMarkId())) {
								nowSonMarkIdList.add(nowpt.getMarkId());
							}
							tgMap.put(nowpt.getMarkId(), "green");
						}
					}
				}

			}
		} else {
			map.put(1, false);
			map.put(2, now);
		}
		return map;
	}

	public boolean isEquals(String str1, String str2) {
		if ((str1 == null || str1.length() == 0) && str2 != null
				&& str2.length() > 0) {
			return false;
		}
		if ((str2 == null || str2.length() == 0) && str1 != null
				&& str1.length() > 0) {
			return false;
		}
		if (str1 != null && str1.length() >= 0 && str2 != null
				&& str2.length() > 0 && !str1.equals(str2)) {
			return false;
		}
		return true;
	}

	public boolean isEquals(Float f1, Float f2) {
		if (f1 == null && f2 != null) {
			return false;
		}
		if (f2 == null && f1 != null) {
			return false;
		}
		if (f1 != null && f2 != null && !f1.equals(f2)) {
			return false;
		}
		return true;
	}

	public boolean isEquals(Integer i1, Integer i2) {
		if (i1 == null && i2 != null) {
			return false;
		}
		if (i2 == null && i1 != null) {
			return false;
		}
		if (i1 != null && i2 != null && !i1.equals(i2)) {
			return false;
		}
		return true;
	}

	@Override
	public String deleteSons(int[] checkboxs, Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowtime = Util.getDateTime();
		if (checkboxs != null && checkboxs.length > 0 && id != null) {
			for (int i = 0; i < checkboxs.length; i++) {
				ProcardTemplate procardTemplate = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where id=? and procardTemplate.id=?",
								checkboxs[i], id);
				if (procardTemplate != null
						&& procardTemplate.getProcardTemplate() != null
						&& procardTemplate.getProcardTemplate().getBzStatus() != null
						&& (procardTemplate.getProcardTemplate().getBzStatus()
								.equals("已批准") || procardTemplate
								.getProcardTemplate().getBzStatus().equals(
										"设变发起中"))) {
					return "上层零件编制状态为:"
							+ procardTemplate.getProcardTemplate()
									.getBzStatus() + "不允许删除下阶层!";
				}
				if (procardTemplate != null) {
					String markId = procardTemplate.getMarkId();
					boolean b = true;
					// 删除上层同件号的同件号零件
					List<ProcardTemplate> sonList = totalDao
							.query(
									"from ProcardTemplate where markId=? and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')  and productStyle=? and  procardTemplate.id in(select id from ProcardTemplate where markId=(select markId from ProcardTemplate where id=?))",
									markId, procardTemplate.getProductStyle(),
									id);
					List<String> markIds = new ArrayList<String>();
					if (sonList != null && sonList.size() > 0) {
						for (int j = (sonList.size() - 1); j >= 0; j--) {
							ProcardTemplate procardt = sonList.get(j);
							if (!markIds.contains(procardt)) {
								String xiaohao = null;
								ProcardTemplateChangeLog.addchangeLog(totalDao,
										procardTemplate, procardt, "子件", "删除",
										user, nowtime);
								markIds.add(procardt.getMarkId());
							}
							// Set<ProcessTemplate> processSet = procardt
							// .getProcessTemplate();
							// if (processSet != null && processSet.size() > 0)
							// {
							// for (ProcessTemplate process : processSet) {
							// process.setDataStatus("删除");
							// process.setProcardTemplate(null);
							// process.setOldPtId(procardt.getId());
							// process.setOldbanci(procardt.getBanci());
							// process.setDeleteTime(Util.getDateTime());
							// totalDao.update(process);
							// }
							// }
							procardt.setDataStatus("删除");
							procardt.setProcardTemplate(null);
							b = b & totalDao.update(procardt);
							b = b & tbDownDataStatus(procardt);
						}
					}
				}
			}
		}
		return "删除成功!";
	}

	public boolean tbDownDataStatus(ProcardTemplate pt) {
		Set<ProcardTemplate> sonSet = pt.getProcardTSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (ProcardTemplate son : sonSet) {
				// Set<ProcessTemplate> processSet = son.getProcessTemplate();
				// if (processSet != null && processSet.size() > 0) {
				// for (ProcessTemplate process : processSet) {
				// process.setDataStatus("删除");
				// totalDao.update(process);
				// }
				// }
				son.setDataStatus("删除");
				son.setProcardTemplate(null);
				tbDownDataStatus(son);
				totalDao.update(son);
			}
		}
		pt.setProcardTSet(null);
		totalDao.update(pt);
		return true;
	}

	@Override
	public String deleteSameProcess() {
		// TODO Auto-generated method stub
		List<ProcardTemplate> procardList = totalDao
				.query("from ProcardTemplate p where  p.id in"
						+ "(select procard.id from ProcessTemplate process,ProcardTemplate procard where (process.dataStatus is null  or process.dataStatus!='删除' ) and process.procardTemplate.id=procard.id group by processNo,procard.id having count(*)>1)");
		if (procardList.size() > 0) {
			for (ProcardTemplate pt : procardList) {
				Set<ProcessTemplate> processSet = pt.getProcessTemplate();
				if (processSet != null) {
					List<Integer> hasProcessNo = new ArrayList<Integer>();
					List<ProcessTemplate> deleteList = new ArrayList<ProcessTemplate>();
					for (ProcessTemplate process : processSet) {
						if (process.getDataStatus() == null
								|| !process.getDataStatus().equals("删除")) {
							if (hasProcessNo.contains(process.getProcessNO())) {// 包含要删
								deleteList.add(process);
							} else {
								hasProcessNo.add(process.getProcessNO());
							}
						}
					}
					if (deleteList != null && deleteList.size() > 0) {
						for (ProcessTemplate deletep : deleteList) {
							processSet.remove(deletep);
							deletep.setProcardTemplate(null);
							deletep.setDataStatus("删除");
							deletep.setOldPtId(pt.getId());
							deletep.setOldbanci(pt.getBanci());
							deletep.setDeleteTime(Util.getDateTime());
							totalDao.update(deletep);
						}
					}
				}
			}
		}
		return procardList.size() + "";
	}

	@Override
	public ProcessTemplateFile findGyTzById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessTemplateFile) totalDao.getObjectById(
				ProcessTemplateFile.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void changeycl() {
		// TODO Auto-generated method stub
		List<ProcardTemplate> procardLst = totalDao
				.query("from ProcardTemplate where trademark is not null and trademark!='' and markId in('DKBA8.031.8978','DKBA8.031.8550')");
		if (procardLst != null && procardLst.size() > 0) {
			int len = procardLst.size();
			for (int i = 0; i < len; i++) {
				ProcardTemplate father = procardLst.get(i);
				ProcardTemplate yclProcard = new ProcardTemplate();
				if (father.getTrademark() != null
						&& father.getTrademark().length() > 0) {
					yclProcard.setMarkId(father.getTrademark());
					yclProcard.setProName(father.getYuanName());
					yclProcard.setSpecification(father.getSpecification());
					yclProcard.setCaizhi(father.getCaizhi());
					yclProcard.setWgType(father.getWgType());
					yclProcard.setKgliao("TK");
					yclProcard.setTuhao(father.getYtuhao());
					yclProcard.setQuanzi1(father.getQuanzi1());
					yclProcard.setQuanzi2(father.getQuanzi2());
					yclProcard.setFatherId(father.getId());
					yclProcard.setUnit(father.getYuanUnit());
					yclProcard.setProcardTemplate(father);
					yclProcard.setProcardStyle("外购");
					yclProcard.setRootId(father.getRootId());
					yclProcard.setLingliaostatus(father.getLingliaostatus());
					yclProcard.setBanbenStatus(father.getBanbenStatus());
					yclProcard.setBzStatus("初始");
					yclProcard.setThisLength(father.getThisLength());
					yclProcard.setThisWidth(father.getThisWidth());
					yclProcard.setThisHight(father.getThisHight());
					yclProcard.setBili(father.getBili());
					yclProcard.setWgType(father.getWgType());
					yclProcard.setProductStyle(father.getProductStyle());
					yclProcard.setBelongLayer(father.getBelongLayer() + 1);
					totalDao.save(yclProcard);
					father.setTrademark(null);
					father.setYuanName(null);
					father.setYtuhao(null);
					father.setSpecification(null);
					father.setQuanzi1(null);
					father.setQuanzi2(null);
					father.setYuanUnit(null);
					father.setThisLength(null);
					father.setThisWidth(null);
					father.setThisHight(null);
					father.setBili(null);
					father.setWgType(null);
					father.setKgliao(null);
					totalDao.update(father);
				}
				if (i % 400 == 0) {
					totalDao.clear();
				}
			}
		}

	}

	@Override
	public String daorutest(File bomTree) {
		String fileName = Util.getDateTime("yyyyMMddhhmmss") + ".xls";
		// 上传到服务器
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file")
				+ "/" + fileName;
		File file = new File(fileRealPath);
		jxl.Workbook wk = null;
		int insize = 0;
		try {
			FileCopyUtils.copy(bomTree, file);
			// 开始读取excle表格
			InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
			if (is != null) {
				wk = Workbook.getWorkbook(is);// 创建workbook
			}
			Sheet st = wk.getSheet(0);// 获得第一张sheet表;
			int exclecolums = st.getRows();// 获得excle总行数
			if (exclecolums > 1) {
				String msg1 = "";
				for (int i = 1; i < exclecolums; i++) {
					Cell[] cells = st.getRow(i);//
					String markId = cells[0].getContents();// 件号
					// String sunhaostr = cells[1].getContents();// 损耗
					// Float sunhao = 0f;
					// if (sunhaostr != null) {
					// try {
					// sunhao = Float.parseFloat(sunhaostr);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					//
					// }
					// String biaochu = cells[1].getContents();// 表处
					String caizhi = cells[1].getContents();// 材质
					String c = cells[2].getContents();// 长
					String d = cells[3].getContents();// 宽
					String e = cells[4].getContents();// 厚
					Float thisLength = 0f;
					Float thisWidth = 0f;
					Float thisHight = 0f;
					try {
						if (c != null && c.length() > 0) {
							thisLength = Float.parseFloat(c);
						}
						if (d != null && d.length() > 0) {
							thisWidth = Float.parseFloat(d);
						}
						if (e != null && e.length() > 0) {
							thisHight = Float.parseFloat(e);
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					List<ProcardTemplate> ptList = totalDao
							.query(
									" from ProcardTemplate where markId = ? and (banbenStatus is null or banbenStatus = '默认' )"
											+ " and (dataStatus is null or dataStatus = '' )  and (thisHight is null or thisHight=0)",
									markId);
					// String hql =
					// " FROM ProcardTemplate where fatherId IN ( SELECT id FROM ProcardTemplate WHERE markId = ? ) AND procardStyle = '外购'"
					// +
					// "AND wgType IN (SELECT name FROM Category where fatherId =( SELECT id  FROM  Category  where name = '板材'))"
					// +
					// " and (banbenStatus is null or banbenStatus = '默认' ) and (sunhao is null or sunhao = 0)";
					// List<ProcardTemplate> ptList = totalDao.query(hql,
					// markId);
					for (ProcardTemplate p : ptList) {
						// p.setSunhao(sunhao);
						// p.setSunhaoType(0);
						// p.setBiaochu(biaochu);
						p.setCaizhi(caizhi);
						p.setThisHight(thisHight);
						p.setThisLength(thisLength);
						p.setThisWidth(thisWidth);
						totalDao.update(p);
					}
					if (i % 200 == 0) {
						totalDao.clear();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "true";

	}

	@Override
	public ProcardTemplate getProcardTemplateById(Integer id, Integer rootId) {
		// TODO Auto-generated method stub
		// 获取整个BOM
		if (id.equals(rootId)) {// 总成
			ProcardTemplate p = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
			p.setBeforeTrid(1);
			return p;
		}
		List<Object[]> ptList = totalDao
				.query(
						"select id,procardTemplate.id,xuhao from ProcardTemplate where rootId=? and (dataStatus is null or dataStatus!='删除') order by procardTemplate.id,xuhao,id",
						rootId);
		if (ptList != null && ptList.size() > 0) {
			bomXuhao = 1;
			ProcardTemplate pt = getBomIndexPt(ptList, rootId, id);
			return pt;
		}
		return null;
	}

	private ProcardTemplate getBomIndexPt(List<Object[]> ptList,
			Integer fathrId, Integer id) {
		// TODO Auto-generated method stub
		List<Object[]> sonList = new ArrayList<Object[]>();
		for (Object[] objs : ptList) {
			if (objs[1] != null) {
				Integer fid = Integer.parseInt(objs[1].toString());
				if (fathrId.equals(fid)) {
					sonList.add(objs);
				}
			}
		}
		ptList.removeAll(sonList);
		if (sonList != null && sonList.size() > 0) {
			for (Object[] objs : sonList) {
				bomXuhao++;
				if (objs[0] != null) {
					Integer pid = Integer.parseInt(objs[0].toString());
					if (pid.equals(id)) {
						ProcardTemplate p = (ProcardTemplate) totalDao
								.getObjectById(ProcardTemplate.class, id);
						p.setBeforeTrid(bomXuhao);
						return p;
					} else {
						ProcardTemplate pt = getBomIndexPt(ptList, pid, id);
						if (pt != null) {
							return pt;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object[] upptlvnew(List<ProcardTemplateBanBen> ptbbList,
			Integer rootId, ProcardTemplateBanBenApply ptbbA,
			ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		Users users = Util.getLoginUser();
		if (users == null) {
			return new Object[] { "请选登录!" };
		}
		ProcardTemplate totalPt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, rootId);
		if (totalPt == null) {
			return new Object[] { "对不起,没有找到对应的总成!" };
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, ptbbA.getId());
		if (ptbbApply == null) {
			return new Object[] { "对不起,没有找到设变申请!" };
		}
		if (!ptbbApply.getProcessStatus().equals("选择设变零件")) {
			return new Object[] { "对不起,当前状态为：" + ptbbApply.getProcessStatus()
					+ ",无法选择设变零件!" };
		}
		ptbbApply.setNeedDeptJudege(ptbbA.getNeedDeptJudege());
		ptbbApply.setRemark(ptbbA.getRemark());
		ProcardTemplateBanBenJudges oldJudges = (ProcardTemplateBanBenJudges) totalDao
				.getObjectById(ProcardTemplateBanBenJudges.class, ptbbJudges
						.getId());
		if (oldJudges == null) {
			return new Object[] { "对不起,没有找到对应的项目工程师!" };
		}
		String nowTime = Util.getDateTime();
		// oldJudges.setSelectUserId(ptbbJudges.getSelectUserId());
		// oldJudges.setSelectUsers(ptbbJudges.getSelectUsers());
		oldJudges.setPlTime(nowTime);
		totalDao.update(oldJudges);
		if (ptbbList != null && ptbbList.size() > 0) {
			ptbbApply.setZpTime(Util.getDateTime());
			ptbbApply.setProcessStatus("变更设计");// 进程状态(设变发起,项目主管初评,项目工程师评审,指派各部门,各部门评审,结论,资料更新,关联并通知生产,生产后续,关闭)
			Set<ProcardTemplateBanBen> ptbbSet = ptbbApply
					.getProcardTemplateBanBen();
			if (ptbbSet == null) {
				ptbbSet = new HashSet<ProcardTemplateBanBen>();
			} else {
				// 选出要删除的对象
				Set<ProcardTemplateBanBen> deleteSet = new HashSet<ProcardTemplateBanBen>();
				for (ProcardTemplateBanBen fordelte : ptbbSet) {
					boolean had = false;
					for (ProcardTemplateBanBen newptbb : ptbbList) {
						if (newptbb != null && newptbb.getId() != null
								&& newptbb.getId().equals(fordelte.getId())) {
							fordelte.setNewBanBenNumber(newptbb
									.getNewBanBenNumber());
							fordelte.setUptype(newptbb.getUptype());// 版本升级,设变
							fordelte.setChangeProcess(newptbb
									.getChangeProcess());// 是否修改工序
							fordelte.setChangeTz(newptbb.getChangeTz());// 是否修改图纸
							fordelte.setRemark(newptbb.getRemark());// 说明
							had = true;
							break;
						}
					}
					if (!had) {
						deleteSet.add(fordelte);
					}
				}
				if (deleteSet != null && deleteSet.size() > 0) {
					ptbbSet.removeAll(deleteSet);
					for (ProcardTemplateBanBen delte : deleteSet) {
						delte.setProcardTemplateBanBenApply(null);
						List<ProcardTemplate> backptList = totalDao
								.query(
										"from ProcardTemplate where markId = ? and (banbenStatus is null or banbenStatus!='历史')",
										delte.getMarkId());
						if (backptList != null && backptList.size() > 0) {
							for (ProcardTemplate back : backptList) {
								back.setBzStatus("已批准");
								totalDao.update(back);
							}
						}
						totalDao.delete(delte);
					}
				}
			}
			int i = 1;
			for (ProcardTemplateBanBen ptbb : ptbbList) {
				if (ptbb != null && ptbb.getPtId() != null) {
					ProcardTemplate newProcardtemplate = null;// 如果是件号替换，查询出新的件号是否已存在
					if (ptbb.getRemark() == null
							|| ptbb.getRemark().length() == 0) {
						throw new RuntimeException("第" + (i + 1) + "条没有填写设变明细!");
					}
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, ptbb
									.getPtId());
					if (pt == null) {
						throw new RuntimeException("你选择的第" + i
								+ "条数据没有找到对应的零件!");
					}
					if (ptbb.getId() == null) {
						// ptbb.setptId;//Bom模板id
						ptbb.setFptId(pt.getFatherId());
						ptbb.setMarkId(pt.getMarkId());// 件号
						ptbb.setRootMarkId(totalPt.getMarkId());
						ptbb.setYwMarkId(totalPt.getYwMarkId());// 业务件号（总成对外销售使用）
						ptbb.setProName(pt.getProName());// 名称
						ptbb.setUnit(pt.getUnit());// 单位
						ptbb.setProcardStyle(pt.getProcardStyle());// 卡片类型(总成，组合，外购，自制)
						ptbb.setProductStyle(pt.getProductStyle());// 产品类型(试制，批产)
						ptbb.setBanBenNumber(pt.getBanBenNumber());// 原版本号
						// ptbb.setnewBanBenNumber;//升级版本号
						Integer banci = pt.getBanci();
						if (banci == null) {
							banci = 0;
						}
						ptbb.setBanci(banci);// 版次
						ptbb.setNewbanci(banci + 1);// 新版次
						ptbb.setAddTime(nowTime);
						// ptbb.setuptype;
						// ptbb.setchangeProcess;//是否修改工序
						// ptbb.setchangeTz;//是否修改图纸
						// ptbb.setxuhao;//序号
						ptbb.setProcardTemplateBanBenApply(ptbbApply);
						ptbb.setKgliao(pt.getKgliao());
						ptbbSet.add(ptbb);
					} else {
						for (ProcardTemplateBanBen had : ptbbSet) {
							if (had.getId().equals(ptbb.getId())) {
								ptbb = had;
								break;
							}
						}
					}
					if (ptbb.getProductStyle().equals("批产")) {
						if (ptbb.getSingleSb().equals("是")) {
							throw new RuntimeException("批产不允许单独设变");
						}
					} else {
						if (ptbb.getSingleSb().equals("是")
								&& ptbb.getUptype().equals("设变")) {
							throw new RuntimeException("单独设变必须是版本升级或者件号替换!");
						}
					}
					if (ptbb.getUptype().equals("设变")
							&& Util.isEquals(ptbb.getBanBenNumber(), ptbb
									.getNewBanBenNumber())) {
						ptbb.setUptype("版本升级");
					}

					if (ptbb.getUptype().equals("件号替换")) {
						throw new RuntimeException("目前已关闭件号替换功能!");
						// if (ptbb.getNewmarkId() == null
						// || ptbb.getNewmarkId().length() == 0) {
						// throw new RuntimeException("零件" + ptbb.getMarkId()
						// + "的设变类型为件号替换请填写件号!");
						// }
						// String newbanbenSql = null;
						// if (ptbb.getNewBanBenNumber() == null
						// || ptbb.getNewBanBenNumber().equals("")) {
						// newbanbenSql =
						// " and (banBenNumber is null or banBenNumber='')";
						// } else {
						// newbanbenSql = " and banBenNumber='"
						// + ptbb.getNewBanBenNumber();
						// }
						// newProcardtemplate = (ProcardTemplate) totalDao
						// .getObjectByCondition(
						// "from ProcardTemplate where markId=? and productStyle='批产' "
						// +
						// "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')",
						// ptbb.getNewmarkId());
						// if (ptbb.getProductStyle().equals("批产")) {
						// if (newProcardtemplate != null
						// && !Util.isEquals(
						// ptbb.getNewBanBenNumber(),
						// newProcardtemplate
						// .getBanBenNumber())) {
						// throw new RuntimeException("零件"
						// + ptbb.getMarkId()
						// + "的设变类型为件号替换，新版本号为:"
						// + ptbb.getNewBanBenNumber()
						// + ",系统现有版本为："
						// + newProcardtemplate.getBanBenNumber());
						// }
						// } else {
						// if (newProcardtemplate != null) {
						// if (!Util.isEquals(ptbb.getNewBanBenNumber(),
						// newProcardtemplate.getBanBenNumber())) {
						// String pcbaneben = newProcardtemplate
						// .getBanBenNumber();
						// newProcardtemplate = (ProcardTemplate) totalDao
						// .getObjectByCondition(
						// "from ProcardTemplate where markId=? and productStyle='试制' "
						// +
						// "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')"
						// + newbanbenSql,
						// ptbb.getNewmarkId());
						// if (newProcardtemplate == null) {
						// throw new RuntimeException("零件"
						// + ptbb.getMarkId()
						// + "的设变类型为件号替换，新版本号为:"
						// + ptbb.getNewBanBenNumber()
						// + ",系统现有批产版本为：" + pcbaneben
						// + "并且系统中也无此版本试制零件!");
						// }
						// }
						// } else {
						// newProcardtemplate = (ProcardTemplate) totalDao
						// .getObjectByCondition(
						// "from ProcardTemplate where markId=? and productStyle='试制' "
						// +
						// "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')"
						// + newbanbenSql, ptbb
						// .getNewmarkId());
						// if (newProcardtemplate == null) {
						// newProcardtemplate = (ProcardTemplate) totalDao
						// .getObjectByCondition(
						// "from ProcardTemplate where markId=? and productStyle='试制' "
						// +
						// "and (banbenStatus is null or banbenStatus='默认') and (dataStatus is null or dataStatus!='删除')",
						// ptbb.getNewmarkId());
						// throw new RuntimeException("零件"
						// + ptbb.getMarkId()
						// + "的设变类型为件号替换，新版本号为:"
						// + ptbb.getNewBanBenNumber()
						// + ",系统现无批产此零件"
						// + "并且系统中试制零件版本为:"
						// + newProcardtemplate
						// .getBanBenNumber() + "!");
						// }
						// }
						// }
					}
					// 在制数量
					Float zaizhiCount = 0f;
					// 在途数量
					Float zaituCount = 0f;

					String kgsql = "";
					if (ptbb.getKgliao() != null
							&& ptbb.getKgliao().length() > 0) {
						kgsql += " and kgliao ='" + ptbb.getKgliao() + "'";
					}
					String goodsClassSql = " and goodsClass in ('外购件库') "
							+ kgsql;
					String banben_hql = "";
					String banben_hql2 = "";
					if (ptbb.getBanBenNumber() != null
							&& ptbb.getBanBenNumber().length() > 0) {
						banben_hql = " and banBenNumber='"
								+ ptbb.getBanBenNumber() + "'";
						banben_hql2 = " and banben='" + ptbb.getBanBenNumber()
								+ "'";
					}
					String specification_sql = "";
					// 库存量(件号+版本+供料属性+库别)
					String hqlGoods = "";
					hqlGoods = "select sum(goodsCurQuantity) from Goods where goodsMarkId=? "
							+ goodsClassSql
							+ " and goodsCurQuantity>0 "
							+ banben_hql
							+ " and (fcStatus is null or fcStatus='可用')";
					Float kcCount = (Float) totalDao.getObjectByCondition(
							hqlGoods, ptbb.getMarkId());
					if (kcCount == null || kcCount < 0) {
						kcCount = 0f;
					}
					/****************** 占用量=生产占用量+导入占用量 ******************************/
					// 系统占用量(含损耗)(已计算过采购量(1、有库存 2、采购中)，未领料)
					String zyCountSql = "select sum(hascount) from Procard where markId=? "
							+ banben_hql
							+ " and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' and (lingliaostatus='是' or lingliaostatus is null or lingliaostatus='' ) "
							+ " and (sbStatus<>'删除' or sbStatus is null ) ";
					if ("外购".equals(ptbb.getProcardStyle())) {
						zyCountSql += " and kgliao='" + ptbb.getKgliao() + "'";
					}
					Double zyCountD = (Double) totalDao
							.getObjectByConditionforDouble(zyCountSql, ptbb
									.getMarkId());
					if (zyCountD == null || zyCountD < 0) {
						zyCountD = 0d;
					}
					zaizhiCount = zyCountD.floatValue();

					if ("外购".equals(ptbb.getProcardStyle())) {
						// 系统在途量(已生成物料需求信息，未到货)
						String hql_zc0 = "select sum(number-ifnull(rukuNum,0)) from ManualOrderPlan where markId = ?  "
								+ banben_hql2
								+ " and kgliao=? and (number>rukuNum or rukuNum is null) and (status<>'取消' or status is null)"
								+ specification_sql;
						Double ztCount = (Double) totalDao
								.getObjectByCondition(hql_zc0,
										ptbb.getMarkId(), ptbb.getKgliao());
						if (ztCount == null) {
							ztCount = 0D;
						}
						zaituCount = ztCount.floatValue();
					} else {
						// 系统委外量(委外数量)--自制件的在途数量
						String wwCountSql = "select sum(wwblCount) from  Procard where markId=? "
								+ banben_hql
								+ "and jihuoStatua='激活' and (status='已发卡' or (oldStatus='已发卡' and status='设变锁定')) and procardStyle='外购' "
								+ "and cgNumber is not null and wwblCount is not null";
						Float wwCount = (Float) totalDao.getObjectByCondition(
								wwCountSql, ptbb.getMarkId());
						if (wwCount == null || wwCount < 0) {
							wwCount = 0f;
						}
						zaituCount = wwCount.floatValue();
					}
					ptbb.setZaizhiCount(zaizhiCount);
					ptbb.setZaituCount(zaituCount);
					// 库存数量
					if (ptbb.getProcardStyle().equals("自制")) {
						ptbb.setKucunCount(0f);
					} else {
						ptbb.setKucunCount(kcCount);
					}
					if (ptbb.getId() == null) {
						totalDao.save(ptbb);
					} else {
						totalDao.update(ptbb);
					}
					List<Procard> procardSameList = null;
					// if (pt.getProductStyle().equals("试制")) {
					// // 同总成生产任务
					// procardSameList = totalDao
					// .query(
					// "from Procard where markId =? and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where procardTemplateId=? and (tjNumber is null or tjNumber<filnalCount) and status in('初始','已发卡','设变锁定','已发料','领工序','待入库'))",
					// ptbb.getMarkId(), rootId);
					// } else {
					// // 同总成生产任务
					// }
					procardSameList = totalDao
							.query(
									"from Procard where markId =?  and status not in('取消','删除')  and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where markId=? and (tjNumber is null or tjNumber<filnalCount) and status in('初始','已发卡','已发料','设变锁定','完成','领工序','待入库'))",
									ptbb.getMarkId(), totalPt.getMarkId());
					if (procardSameList != null && procardSameList.size() > 0) {
						for (Procard same : procardSameList) {
							if (same.getStatus().equals("完成")
									&& same.getJihuoStatua() == null) {// 判定为补料报废记录零件
								continue;
							}
							Object[] totalabout = (Object[]) totalDao
									.getObjectByCondition(
											"select markId,ywMarkId,selfCard,status,oldStatus,wlstatus from Procard where id=?",
											same.getRootId());
							String totaljd = null;
							String wlstatus = "待定";
							if (totalabout[5] != null) {
								wlstatus = totalabout[5].toString();
							}
							String status = "";
							if (totalabout[3] != null) {
								status = totalabout[3].toString();
							}
							if (status.equals("设变锁定")) {
								status = totalabout[4].toString();
							}
							if (status.equals("设变锁定")) {
								totaljd = "异常请找管理员";
							}
							if (status.equals("初始")) {
								if (wlstatus.equals("待定")) {
									totaljd = "初始";
								} else {
									totaljd = "已运算MRP";
								}
							} else {
								totaljd = "已排产";
							}

							ProcardAboutBanBenApply pabb = new ProcardAboutBanBenApply();
							pabb.setTotaljd(totaljd);
							pabb.setProcardId(same.getId());// 零件id
							pabb.setFprocardId(same.getFatherId());
							pabb.setOrderNumber(same.getOrderNumber());// 订单号
							pabb.setRootMarkId(totalabout[0].toString());// 总成件号
							if (totalabout[1] != null) {
								pabb.setYwMarkId(totalabout[1].toString());// 业务件号
							}
							pabb.setRootSelfCard(totalabout[2].toString());// 总成批次
							pabb.setMarkId(same.getMarkId());// 件号
							pabb.setSelfCard(same.getSelfCard());// 批次
							pabb.setProName(same.getProName());// 零件名称
							pabb.setBanebenNumber(same.getBanBenNumber());// 版本号
							if (same.getBanci() != null) {
								pabb.setBanci(same.getBanci());// 版次
							} else {
								pabb.setBanci(0);// 版次
							}
							pabb.setScCount(same.getFilnalCount());

							// pabb.setclType;//处理方案
							pabb.setStatus("待处理");// 状态
							if (totalabout[3].toString().equals("设变锁定")) {
								pabb.setRootStatus(totalabout[4].toString());// 总成状态
							} else {
								pabb.setRootStatus(totalabout[3].toString());// 总成状态
							}
							pabb.setProcardStatus(same.getStatus());// 零件状态
							pabb.setBbapplyId(ptbbApply.getId());// ProcardTemplateBanBenApply
							pabb.setBbId(ptbb.getId());// ProcardTemplateBanBen
							pabb.setProcardStyle(same.getProcardStyle());

							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											same.getId());
							Set<ProcessAboutBanBenApply> processabbjSet = new HashSet<ProcessAboutBanBenApply>();
							if (processList != null && processList.size() > 0) {
								for (int j = 0; j < processList.size(); j++) {
									float zzCount = 0f;
									ProcessInfor process = processList.get(j);
									if (j == 0) {
										if (process.getApplyCount() < same
												.getFilnalCount()) {// 有还没有领的工序
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(null);// 工序号
											processabb.setProcessName("未生产");// 工序名称
											processabb.setScCount(same
													.getFilnalCount()
													- process.getApplyCount());// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
									if (process.getApplyCount() == 0) {
										continue;
									}
									// 此工序已领单未提交数量
									zzCount += process.getApplyCount()
											- process.getSubmmitCount();
									if ((j + 1) <= (processList.size() - 1)) {
										ProcessInfor process2 = processList
												.get((j + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount += process.getSubmmitCount()
												- process2.getApplyCount();
										if (zzCount < 0) {
											zzCount = 0;
										}
									} else {
										zzCount += process.getSubmmitCount();
									}
									if (zzCount > 0) {// 查询此工序当前的转库半成品数量
										if (zzCount > 0) {// 可转库的数量
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(process
													.getProcessNO());// 工序号
											processabb.setProcessName(process
													.getProcessName());// 工序名称
											processabb.setScCount(zzCount);// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
								}
							} else {
								ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
								processabb.setProcessNo(null);// 工序号
								processabb.setProcessName("无");// 工序名称
								processabb.setScCount(same.getFilnalCount());// 生产数量
								processabb.setPabb(pabb);// 生产关联零件
								processabbjSet.add(processabb);
							}
							pabb.setProcessabbSet(processabbjSet);
							if (!same.getStatus().equals("设变锁定")) {
								// 记录原生产状态
								same.setOldStatus(same.getStatus());
								// 暂停生产
								same.setStatus("设变锁定");
							}
							totalDao.save(pabb);
							totalDao.update(same);
						}
					}
					// 同件号非同总成零件(对到明细上面)
					List<Procard> procardList = totalDao
							.query(
									"from Procard where (sbStatus is null or sbStatus !='删除') and status not in('取消','删除')   and productStyle=? and rootId not in(select id from Procard where markId =?) and markId =?"
											+ "and rootId in(select id from Procard where procardStyle='总成'  and status in('初始','已发卡','已发料','设变锁定','领工序','完成','待入库'))",
									totalPt.getProductStyle(), totalPt
											.getMarkId(), pt.getMarkId());
					if (procardList != null && procardList.size() > 0) {
						for (Procard other : procardList) {
							if (other.getStatus().equals("完成")
									&& other.getJihuoStatua() == null) {// 判定为补料报废记录零件
								continue;
							}
							ProcardAboutBanBenApply pabb = new ProcardAboutBanBenApply();
							Object[] totalabout = (Object[]) totalDao
									.getObjectByCondition(
											"select markId,ywMarkId,selfCard,status,oldStatus,wlstatus from Procard where id=?",
											other.getRootId());
							String totaljd = null;
							String wlstatus = "待定";
							if (totalabout[5] != null) {
								wlstatus = totalabout[5].toString();
							}
							String status = "";
							if (totalabout[3] != null) {
								status = totalabout[3].toString();
							}
							if (status.equals("设变锁定")) {
								status = totalabout[4].toString();
							}
							if (status.equals("设变锁定")) {
								totaljd = "异常请找管理员";
							}
							if (status.equals("初始")) {
								if (wlstatus.equals("待定")) {
									totaljd = "初始";
								} else {
									totaljd = "已运算MRP";
								}
							} else {
								totaljd = "已排产";
							}
							pabb.setTotaljd(totaljd);
							pabb.setProcardId(other.getId());// 零件id
							pabb.setFprocardId(other.getFatherId());
							pabb.setOrderNumber(other.getOrderNumber());// 订单号
							pabb.setRootMarkId(totalabout[0].toString());// 总成件号
							if (totalabout[1] != null) {
								pabb.setYwMarkId(totalabout[1].toString());// 业务件号
							}
							pabb.setRootSelfCard(totalabout[2].toString());// 总成批次
							pabb.setMarkId(other.getMarkId());// 件号
							pabb.setSelfCard(other.getSelfCard());// 批次
							pabb.setProName(other.getProName());// 零件名称
							pabb.setBanebenNumber(other.getBanBenNumber());// 版本号
							if (other.getBanci() != null) {
								pabb.setBanci(other.getBanci());// 版次
							} else {
								pabb.setBanci(0);// 版次
							}
							pabb.setScCount(other.getFilnalCount());
							pabb.setStatus("待处理");
							if (totalabout[3].toString().equals("设变锁定")) {
								pabb.setRootStatus(totalabout[4].toString());// 总成状态
							} else {
								pabb.setRootStatus(totalabout[3].toString());// 总成状态
							}
							pabb.setProcardStatus(other.getStatus());// 零件状态
							pabb.setBbapplyId(ptbbApply.getId());
							;// ProcardTemplateBanBenApply
							pabb.setBbId(ptbb.getId());// ProcardTemplateBanBen
							pabb.setProcardStyle(other.getProcardStyle());
							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											other.getId());
							Set<ProcessAboutBanBenApply> processabbjSet = new HashSet<ProcessAboutBanBenApply>();
							if (processList != null && processList.size() > 0) {
								for (int j = 0; j < processList.size(); j++) {
									float zzCount = 0f;
									ProcessInfor process = processList.get(j);
									if (j == 0) {
										if (process.getSubmmitCount() == 0) {
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											if (process.getApplyCount() == 0) {
												processabb.setProcessNo(null);// 工序号
												processabb
														.setProcessName("未生产");// 工序名称
											} else {
												processabb.setProcessNo(process
														.getProcessNO());// 工序号
												processabb
														.setProcessName(process
																.getProcessName());// 工序名称
											}
											processabb.setScCount(other
													.getFilnalCount());// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
											break;
										}
									}
									// 此工序已领单未提交数量
									zzCount += process.getApplyCount()
											- process.getSubmmitCount();
									if ((j + 1) <= (processList.size() - 1)) {
										ProcessInfor process2 = processList
												.get((j + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount += process.getSubmmitCount()
												- process2.getApplyCount();
										if (zzCount < 0) {
											zzCount = 0;
										}
									} else {
										zzCount += process.getSubmmitCount();
									}
									if (zzCount > 0) {// 查询此工序当前的转库半成品数量
										if (zzCount > 0) {// 可转库的数量
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(process
													.getProcessNO());// 工序号
											processabb.setProcessName(process
													.getProcessName());// 工序名称
											processabb.setScCount(zzCount);// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
								}
							} else {
								ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
								processabb.setProcessNo(null);// 工序号
								processabb.setProcessName("无");// 工序名称
								processabb.setScCount(other.getFilnalCount());// 生产数量
								processabb.setPabb(pabb);// 生产关联零件
								processabbjSet.add(processabb);
							}
							pabb.setProcessabbSet(processabbjSet);
							if (!other.getStatus().equals("设变锁定")) {
								// 记录原生产状态
								other.setOldStatus(other.getStatus());
								// 暂停生产
								other.setStatus("设变锁定");
							}
							totalDao.save(pabb);
							totalDao.update(other);
						}
					}
					String sql = null;
					if (ptbb.getSingleSb() != null
							&& ptbb.getSingleSb().equals("是")) {
						sql = " and rootId=" + rootId;
					} else {
						if (ptbb.getBanBenNumber() == null
								|| ptbb.getBanBenNumber().length() == 0) {
							sql = " and (banBenNumber is null or banBenNumber='') ";
						} else {
							sql = " and banBenNumber='"
									+ ptbb.getBanBenNumber() + "'";
						}
					}
					List<ProcardTemplate> ptList = totalDao
							.query(
									"from ProcardTemplate where markId = ? "
											+ sql
											+ " and productStyle=? and (banbenStatus is null or banbenStatus!='历史')",
									pt.getMarkId(), ptbb.getProductStyle());
					for (ProcardTemplate ptSame : ptList) {
						ptSame.setBzStatus("设变发起中");
						totalDao.update(ptSame);
					}
					i++;
				}
			}

		}
		ptbbApply.setLastop("选择设变零件");
		ptbbApply.setLastUserId(users.getId());
		ptbbApply.setLastUserName(users.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		totalDao.update(ptbbApply);
		return new Object[] { "true", ptbbApply.getId() };
	}

	@Override
	public Map<Integer, Object> findSbApplyList(
			ProcardTemplateBanBenApply bbAply, int pageNo, int pageSize,
			String pageStatus, String markId, String startDate, String endDate,
			String tag) {
		// TODO Auto-generated method stub
		if (bbAply == null) {
			bbAply = new ProcardTemplateBanBenApply();
		}
		String pStatus = null;
		if (bbAply.getProcessStatus() != null
				&& bbAply.getProcessStatus().equals("未关闭")) {
			pStatus = "未关闭";
			bbAply.setProcessStatus(null);
		}
		String hql = totalDao.criteriaQueries(bbAply, null, null);
		if (pStatus != null) {
			hql += " and processStatus !='未关闭'";
			bbAply.setProcessStatus("未关闭");
		}
		if (markId != null && markId.length() > 0 && !markId.contains("delete")
				&& !markId.contains("update") && !markId.contains("select")) {
			hql += " and id in(select procardTemplateBanBenApply.id from ProcardTemplateBanBen where markId like'%"
					+ markId + "%')";
		}
		if (startDate != null && !"".equals(startDate)) {
			hql += " and applyTime >= '" + startDate + "'";
		}
		if (endDate != null && !"".equals(endDate)) {
			hql += " and applyTime <= '" + endDate + "'";
		}

		if (tag != null && tag.equals("self")) {
			Users login = Util.getLoginUser();
			if (login == null) {
				Map<Integer, Object> map = new HashMap<Integer, Object>();
				map.put(1, null);
				map.put(2, 0);
				return map;
			}
			hql += " and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where userId="
					+ login.getId() + ")";
		}
		hql += " and (status is null or status !='删除') order by id desc";
		int count = totalDao.getCount(hql);
		List<ProcardTemplateBanBenApply> ptbbaList = totalDao.findAllByPage(
				hql, pageNo, pageSize);
		if (ptbbaList != null && ptbbaList.size() > 0) {
			for (ProcardTemplateBanBenApply ptbba : ptbbaList) {
				List<String> dqclUserList = null;
				if ("设变发起".equals(ptbba.getProcessStatus())) {
					ptbba.setDqclUser(ptbba.getApplicantName());
				} else if ("分发项目组".equals(ptbba.getProcessStatus())) {
					ptbba.setDqclUser(ptbba.getApplicantName());
				} else if ("项目主管初评".equals(ptbba.getProcessStatus())) {
					String name = (String) totalDao
							.getObjectByCondition(
									"select userName from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='初评' ",
									ptbba.getId());
					ptbba.setDqclUser(name);
				} else if ("上传佐证".equals(ptbba.getProcessStatus())) {
					dqclUserList = totalDao
							.query(
									"select userName from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='佐证' ",
									ptbba.getId());
				} else if ("各部门评审".equals(ptbba.getProcessStatus())) {
					dqclUserList = totalDao
							.query(
									"select userName from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审' ",
									ptbba.getId());
				} else if ("成本审核".equals(ptbba.getProcessStatus())) {
					dqclUserList = totalDao
							.query(
									"select userName from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='成本' ",
									ptbba.getId());
				} else if ("关闭".equals(ptbba.getProcessStatus())) {

				} else {
					String name = (String) totalDao
							.getObjectByCondition(
									"select userName from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='工程师' ",
									ptbba.getId());
					if (name != null) {
						ptbba.setDqclUser(name);
					} else {
						ptbba.setDqclUser("");
					}
				}
				if (dqclUserList != null && dqclUserList.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (String dqclUser : dqclUserList) {
						if (sb.length() == 0) {
							sb.append(dqclUser);
						} else {
							sb.append(";" + dqclUser);
						}
					}
					ptbba.setDqclUser(sb.toString());
				}
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, ptbbaList);
		map.put(2, count);
		return map;
	}

	@Override
	public List<ProcardTemplateBanBenApply> finddclSbApplyList(
			ProcardTemplateBanBenApply bbAply, int pageNo, int pageSize,
			String pageStatus, String markId, String startDate, String endDate,
			String tag) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		if (bbAply == null) {
			bbAply = new ProcardTemplateBanBenApply();
		}
		String pStatus = bbAply.getProcessStatus();
		bbAply.setProcessStatus(null);
		String hql = totalDao.criteriaQueries(bbAply, null, null);
		hql += " and processStatus not in('删除','取消','关闭')";
		bbAply.setProcessStatus(pStatus);
		if (markId != null && markId.length() > 0 && !markId.contains("delete")
				&& !markId.contains("update") && !markId.contains("select")) {
			hql += " and id in(select procardTemplateBanBenApply.id from ProcardTemplateBanBen where markId like'%"
					+ markId + "%')";
		}
		if (startDate != null && !"".equals(startDate)) {
			hql += " and applyTime >= '" + startDate + "'";
		}
		if (endDate != null && !"".equals(endDate)) {
			hql += " and applyTime <= '" + endDate + "'";
		}
		Float bgry = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from AssessPersonnel where usersGroup.groupName='设变关闭组' and usersGroup.status='sb' and userId=? ",
						user.getId());
		String gbSql = "";
		if (bgry != null && bgry > 0) {
			gbSql = " or processStatus in('上传佐证')";
		}
		if (tag != null && tag.equals("self")) {
			// hql +=
			// " and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where userId="
			// + user.getId() + ")";
			hql += " and ("
					+ " ( processStatus in('设变发起','分发项目组') and applicantId="
					+ user.getId()
					+ " )"
					+ gbSql
					+ " or ( processStatus in('项目主管初评') and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where judgeType='初评' and userId="
					+ user.getId()
					+ "))"
					+ " or ( processStatus in('选择设变零件','变更设计','工程师评审','指派各部门','资料更新','关联并通知生产') "
					+ "and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where judgeType='工程师' and userId="
					+ user.getId()
					+ "))"
					+ " or ( processStatus in('成本审核') and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where judgeType='成本' and userId="
					+ user.getId()
					+ "))"
					+ " or ( processStatus in('各部门评审') and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where judgeType='内审' and userId="
					+ user.getId()
					+ "))"
					+ " or ( processStatus in('上传佐证') and id in(select ptbbApply.id from ProcardTemplateBanBenJudges where judgeType in('佐证','工程师') and userId="
					+ user.getId() + "))" + ")";
		}
		hql += " and (status is null or status !='删除') order by id desc";
		// List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
		List<ProcardTemplateBanBenApply> objs = totalDao.query(hql);
		return objs;
	}

	@Override
	public Object[] showSbApplyJd(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplateBanBenApply bbAply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (bbAply == null) {
			return null;
		}
		if (bbAply.getProcessStatus().equals("各部门评审")) {
			// 判断时候可以强制提交
			String zpTime = bbAply.getZpTime();
			if (zpTime != null) {
				try {
					Date date = Util
							.StringToDate(zpTime, "yyyy-MM-dd HH:mm:ss");
					String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='设变评审最大时长'";
					String sc = (String) totalDao.getObjectByCondition(hql1);
					if (sc != null) {
						Integer sci = Integer.parseInt(sc);
						Long t1 = date.getTime();
						Long t2 = new Date().getTime();
						if ((t1 + sci * 3600000) < t2) {
							bbAply.setQznext("yes");
						}
						bbAply.setZdpsTime(sc);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		Set<ProcardTemplateBanBen> bbSet = bbAply.getProcardTemplateBanBen();
		List<ProcardTemplateBanBen> bbList = new ArrayList<ProcardTemplateBanBen>();
		String tbProcard = "是";
		if (bbSet != null && bbSet.size() > 0) {
			for (ProcardTemplateBanBen ptbb : bbSet) {
				ProcardTemplate procardT = (ProcardTemplate) totalDao
						.getObjectByCondition(
								" from ProcardTemplate where markId=? and productStyle=?  and banci=? and (dataStatus is null or dataStatus!='删除')"
										+ " and (banbenStatus is null or banbenStatus !='历史')",
								ptbb.getMarkId(), bbAply.getProductStyle(),
								ptbb.getNewbanci());
				if (procardT == null) {
					ptbb.setBzStatus("无数据");
				} else {
					if (procardT.getBzStatus() == null) {
						ptbb.setBzStatus("");
						tbProcard = "否";
					} else {
						ptbb.setBzStatus(procardT.getBzStatus());
						if (!ptbb.getBzStatus().equals("已批准")) {
							tbProcard = "否";
						}
					}
				}
				bbList.add(ptbb);
			}
			bbAply.setBbList(bbList);
		}
		if (bbAply.getTbProcard() == null || !bbAply.equals("完成")) {
			bbAply.setTbProcard(tbProcard);
		}
		// 初评和内审人员
		Set<ProcardTemplateBanBenJudges> bbJudgesSet = bbAply.getPtbbjset();
		List<ProcardTemplateBanBenJudges> bbJudgesList = null;
		if (bbJudgesSet != null) {
			bbJudgesList = new ArrayList<ProcardTemplateBanBenJudges>(
					bbJudgesSet);
			Users login = Util.getLoginUser();
			if (login != null && bbJudgesList != null
					&& bbJudgesList.size() > 0) {
				for (ProcardTemplateBanBenJudges bbj : bbJudgesList) {
					if (bbj.getJudgeType().equals("工程师")) {
						if (login.getId().equals(bbj.getUserId())) {
							bbAply.setChangeremark("yes");
						}
						break;
					}
				}
			}
			bbAply.setBbJudegeList(bbJudgesList);

		}
		// 获取佐证文件
		List<ProcardTemplateBanBenJudgesFile> zzFileList = totalDao
				.query(
						"from ProcardTemplateBanBenJudgesFile where ptbbapplyId=? "
								+ "and (dataStatus is null or dataStatus !='删除') order by addUserName",
						bbAply.getId());
		bbAply.setPtbbjFileList(zzFileList);
		// 同总成
		List<ProcardAboutBanBenApply> pabbList1 = totalDao
				.query(
						"from ProcardAboutBanBenApply where bbapplyId=? and rootMarkId=? order by procardStyle, rootSelfCard,markId",
						bbAply.getId(), bbAply.getMarkId());
		if (pabbList1 != null && pabbList1.size() > 0) {// 设置评论
			for (ProcardAboutBanBenApply pabb : pabbList1) {
				Set<ProcessAboutBanBenApply> processabbAplySet = pabb
						.getProcessabbSet();
				if (processabbAplySet != null && processabbAplySet.size() > 0) {
					List<ProcessAboutBanBenApply> processabbList = new ArrayList<ProcessAboutBanBenApply>();
					for (ProcessAboutBanBenApply processabbApply : processabbAplySet) {
						List<ProcardBanBenJudge> pbbjList = totalDao
								.query(
										"from ProcardBanBenJudge where processbbJudgeId=?",
										processabbApply.getId());
						processabbApply.setPbbjList(pbbjList);
						processabbList.add(processabbApply);
					}
					pabb.setProcessabbList(processabbList);
				}
			}
		}
		// 非同总成
		List<ProcardAboutBanBenApply> pabbList2 = totalDao
				.query(
						"from ProcardAboutBanBenApply where bbapplyId=? and rootMarkId!=? order by procardStyle,rootMarkId,rootSelfCard,markId",
						bbAply.getId(), bbAply.getMarkId());
		if (pabbList2 != null && pabbList2.size() > 0) {// 设置评论
			for (ProcardAboutBanBenApply pabb : pabbList2) {
				Set<ProcessAboutBanBenApply> processabbAplySet = pabb
						.getProcessabbSet();
				if (processabbAplySet != null && processabbAplySet.size() > 0) {
					List<ProcessAboutBanBenApply> processabbList = new ArrayList<ProcessAboutBanBenApply>();
					for (ProcessAboutBanBenApply processabbApply : processabbAplySet) {
						List<ProcardBanBenJudge> pbbjList = totalDao
								.query(
										"from ProcardBanBenJudge where processbbJudgeId=?",
										processabbApply.getId());
						processabbApply.setPbbjList(pbbjList);
						processabbList.add(processabbApply);
					}
					pabb.setProcessabbList(processabbList);
				}
			}
		}
		List<ProcardTemplateChangeLog> ptchangeLogList = totalDao
				.query(
						"from ProcardTemplateChangeLog where ptbbId in(select id from ProcardTemplateBanBen where procardTemplateBanBenApply.id=?) order by sbMarkId",
						bbAply.getId());
		Object[] objs = new Object[] { bbAply, pabbList1, pabbList2,
				ptchangeLogList };
		return objs;
	}

	@Override
	public Object[] showSbApplyJd2(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplateBanBenApply bbAply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (bbAply == null) {
			return null;
		}
		if (bbAply.getProcessStatus().equals("各部门评审")) {
			// 判断时候可以强制提交
			String zpTime = bbAply.getZpTime();
			if (zpTime != null) {
				try {
					Date date = Util
							.StringToDate(zpTime, "yyyy-MM-dd HH:mm:ss");
					String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='设变评审最大时长'";
					String sc = (String) totalDao.getObjectByCondition(hql1);
					if (sc != null) {
						Integer sci = Integer.parseInt(sc);
						Long t1 = date.getTime();
						Long t2 = new Date().getTime();
						if ((t1 + sci * 3600000) < t2) {
							bbAply.setQznext("yes");
						}
						bbAply.setZdpsTime(sc);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		Set<ProcardTemplateBanBen> bbSet = bbAply.getProcardTemplateBanBen();
		List<ProcardTemplateBanBen> bbList = new ArrayList<ProcardTemplateBanBen>();
		String tbProcard = "是";
		if (bbSet != null && bbSet.size() > 0) {
			List<ProcardTemplateAboutsb> sqlptasbList = totalDao.query(
					" from ProcardTemplateAboutsb where sbApplyId=?", bbAply
							.getId());
			if (sqlptasbList != null && sqlptasbList.size() > 0) {// 赋值处理意见
				for (ProcardTemplateAboutsb ptasb : sqlptasbList) {
					StringBuffer zaizhicltype = new StringBuffer();// 在制处理方案
					StringBuffer zaitucltype = new StringBuffer();// 在途处理方案
					StringBuffer zaikucltype = new StringBuffer();// 库存处理方案
					StringBuffer daizhicltype = new StringBuffer();// 呆滞处理方案
					List<ProcardTemplateAboutsbcltype> ptasbclTypeList = totalDao
							.query(
									"from ProcardTemplateAboutsbcltype where ptasbId=?",
									ptasb.getId());
					if (ptasbclTypeList != null && ptasbclTypeList.size() > 0) {
						for (ProcardTemplateAboutsbcltype ptasbclType : ptasbclTypeList) {
							if (ptasbclType.getZaizhicltype() != null
									&& ptasbclType.getZaizhicltype().length() > 0) {
								if (zaizhicltype.length() == 0) {
									zaizhicltype.append(ptasbclType
											.getUserName()
											+ "-"
											+ ptasbclType.getZaizhicltype());
								} else {
									zaizhicltype.append("; "
											+ ptasbclType.getUserName() + "-"
											+ ptasbclType.getZaizhicltype());
								}
							}
							if (ptasbclType.getZaitucltype() != null
									&& ptasbclType.getZaitucltype().length() > 0) {
								if (zaitucltype.length() == 0) {
									zaitucltype.append(ptasbclType
											.getUserName()
											+ "-"
											+ ptasbclType.getZaitucltype());
								} else {
									zaitucltype.append("; "
											+ ptasbclType.getUserName() + "-"
											+ ptasbclType.getZaitucltype());
								}
							}
							if (ptasbclType.getZaikucltype() != null
									&& ptasbclType.getZaikucltype().length() > 0) {
								if (zaikucltype.length() == 0) {
									zaikucltype.append(ptasbclType
											.getUserName()
											+ "-"
											+ ptasbclType.getZaikucltype());
								} else {
									zaikucltype.append("; "
											+ ptasbclType.getUserName() + "-"
											+ ptasbclType.getZaikucltype());
								}
							}
							if (ptasbclType.getDaizhicltype() != null
									&& ptasbclType.getDaizhicltype().length() > 0) {
								if (daizhicltype.length() == 0) {
									daizhicltype.append(ptasbclType
											.getUserName()
											+ "-"
											+ ptasbclType.getDaizhicltype());
								} else {
									daizhicltype.append("; "
											+ ptasbclType.getUserName() + "-"
											+ ptasbclType.getDaizhicltype());
								}
							}
						}
					}
					ptasb.setZaizhicltype(zaizhicltype.toString());// 在制处理方案
					ptasb.setZaitucltype(zaitucltype.toString());// 在途处理方案
					ptasb.setZaikucltype(zaikucltype.toString());// 库存处理方案
					ptasb.setDaizhicltype(daizhicltype.toString());// 呆滞处理方案
				}
			}
			bbAply.setPtasbList(sqlptasbList);
			for (ProcardTemplateBanBen ptbb : bbSet) {
				ProcardTemplate procardT = (ProcardTemplate) totalDao
						.getObjectByCondition(
								" from ProcardTemplate where markId=? and productStyle=?  and banci=? and (dataStatus is null or dataStatus!='删除')"
										+ " and (banbenStatus is null or banbenStatus !='历史')",
								ptbb.getMarkId(), bbAply.getProductStyle(),
								ptbb.getNewbanci());
				if (procardT == null) {
					ptbb.setBzStatus("无数据");
				} else {
					if (procardT.getBzStatus() == null) {
						ptbb.setBzStatus("");
						tbProcard = "否";
					} else {
						ptbb.setBzStatus(procardT.getBzStatus());
						if (!ptbb.getBzStatus().equals("已批准")) {
							tbProcard = "否";
						}
					}
				}
				// 获取设变修改涉及的零件
				List<ProcardTemplateAboutsbDetail> ptasbDetailList = totalDao
						.query(
								"from ProcardTemplateAboutsbDetail where sbptId=?  order by markId,ptasbId",
								ptbb.getId());
				List<ProcardTemplateAboutsb> ptasbList = new ArrayList<ProcardTemplateAboutsb>();
				if (ptasbDetailList != null && ptasbDetailList.size() > 0) {
					Integer ptasbId = null;
					ProcardTemplateAboutsb ptasb = null;
					for (ProcardTemplateAboutsbDetail ptasbDeati : ptasbDetailList) {
						if (ptasbId == null
								|| !ptasbDeati.getPtasbId().equals(ptasbId)) {
							if (ptasb != null) {
								ptasbList.add(ptasb);
							}
							ptasbId = ptasbDeati.getPtasbId();
							ptasb = new ProcardTemplateAboutsb();
							for (ProcardTemplateAboutsb sqlptasb : sqlptasbList) {
								if (sqlptasb.getId().equals(ptasbId)) {
									ptasb.setZaikuCount(sqlptasb
											.getZaikuCount());
									ptasb.setZaituCount(sqlptasb
											.getZaituCount());
									ptasb.setZaizhiCount(sqlptasb
											.getZaizhiCount());
									ptasb.setDaizhiCount(sqlptasb
											.getDaizhiCount());
									ptasb.setProcardStyle(sqlptasb
											.getProcardStyle());
									break;
								}
							}
							ptasb.setProductStyle(ptasbDeati.getProductStyle());// 生产类型
							ptasb.setMarkId(ptasbDeati.getMarkId());// 件号
							ptasb.setProName(ptasbDeati.getProName());// 名称
							ptasb.setBanben(ptasbDeati.getBanben());// 版本
							ptasb.setBanci(ptasbDeati.getBanci());
							ptasb.setWgType(ptasbDeati.getWgType());// 物料类别
							ptasb.setKgliao(ptasbDeati.getKgliao());// 客供属性
							ptasb.setSpecification(ptasbDeati
									.getSpecification());// 规格
							ptasb.setUnit(ptasbDeati.getUnit());// 单位(自制)
							ptasb.setTuhao(ptasbDeati.getTuhao());// 图号
							ptasb.setYongliao(ptasbDeati.getYongliao());// 单台用量增减（表示生产一个总成的用量增减
							// 正数为增负数为减）
						} else {
							ptasb.setYongliao(ptasb.getYongliao()
									+ ptasbDeati.getYongliao());// 单台用量增减（表示生产一个总成的用量增减
							// 正数为增负数为减）
						}
					}
					if (ptasb != null) {
						ptasbList.add(ptasb);
					}
				}
				ptbb.setPtasbList(ptasbList);
				bbList.add(ptbb);
			}
			bbAply.setBbList(bbList);
		}
		if (bbAply.getTbProcard() == null || !bbAply.equals("完成")) {
			bbAply.setTbProcard(tbProcard);
		}
		// 初评和内审人员
		Set<ProcardTemplateBanBenJudges> bbJudgesSet = bbAply.getPtbbjset();
		List<ProcardTemplateBanBenJudges> bbJudgesList = null;
		if (bbJudgesSet != null) {
			bbJudgesList = new ArrayList<ProcardTemplateBanBenJudges>(
					bbJudgesSet);
			Users login = Util.getLoginUser();
			if (login != null && bbJudgesList != null
					&& bbJudgesList.size() > 0) {
				for (ProcardTemplateBanBenJudges bbj : bbJudgesList) {
					if (bbj.getJudgeType().equals("工程师")) {
						if (login.getId().equals(bbj.getUserId())) {
							bbAply.setChangeremark("yes");
						}
						break;
					}
				}
			}
			bbAply.setBbJudegeList(bbJudgesList);

		}
		// 获取佐证文件
		List<ProcardTemplateBanBenJudgesFile> zzFileList = totalDao
				.query(
						"from ProcardTemplateBanBenJudgesFile where ptbbapplyId=? "
								+ "and (dataStatus is null or dataStatus !='删除') order by addUserName",
						bbAply.getId());
		bbAply.setPtbbjFileList(zzFileList);
		// 同总成
		List<ProcardAboutBanBenApply> pabbList1 = totalDao
				.query(
						"from ProcardAboutBanBenApply where bbapplyId=? and rootMarkId=? order by procardStyle, rootSelfCard,markId",
						bbAply.getId(), bbAply.getMarkId());
		Float fysCount = 0f;
		if (pabbList1 != null && pabbList1.size() > 0) {// 设置评论
			for (ProcardAboutBanBenApply pabb : pabbList1) {
				Set<ProcessAboutBanBenApply> processabbAplySet = pabb
						.getProcessabbSet();
				if (processabbAplySet != null && processabbAplySet.size() > 0) {
					List<ProcessAboutBanBenApply> processabbList = new ArrayList<ProcessAboutBanBenApply>();
					for (ProcessAboutBanBenApply processabbApply : processabbAplySet) {
						List<ProcardBanBenJudge> pbbjList = totalDao
								.query(
										"from ProcardBanBenJudge where processbbJudgeId=?",
										processabbApply.getId());
						processabbApply.setPbbjList(pbbjList);
						processabbList.add(processabbApply);
						if (processabbApply.getClType() == null
								|| !processabbApply.getClType().equals("顺延")) {
							fysCount++;
						}
					}
					pabb.setProcessabbList(processabbList);
				}
				List<WaigouPlanLock> wplockList = totalDao.query(" from WaigouPlanLock where sbApplyId =? and sbProcardId=?", pabb.getBbapplyId(),pabb.getProcardId());
				pabb.setWplockList(wplockList);
			}
		}
		// 非同总成
		List<ProcardAboutBanBenApply> pabbList2 = totalDao
				.query(
						"from ProcardAboutBanBenApply where bbapplyId=? and rootMarkId!=? order by procardStyle,rootMarkId,rootSelfCard,markId",
						bbAply.getId(), bbAply.getMarkId());
		if (pabbList2 != null && pabbList2.size() > 0) {// 设置评论
			for (ProcardAboutBanBenApply pabb : pabbList2) {
				Set<ProcessAboutBanBenApply> processabbAplySet = pabb
						.getProcessabbSet();
				if (processabbAplySet != null && processabbAplySet.size() > 0) {
					List<ProcessAboutBanBenApply> processabbList = new ArrayList<ProcessAboutBanBenApply>();
					for (ProcessAboutBanBenApply processabbApply : processabbAplySet) {
						List<ProcardBanBenJudge> pbbjList = totalDao
								.query(
										"from ProcardBanBenJudge where processbbJudgeId=?",
										processabbApply.getId());
						processabbApply.setPbbjList(pbbjList);
						processabbList.add(processabbApply);
						if (processabbApply.getClType() == null
								|| !processabbApply.getClType().equals("顺延")) {
							fysCount++;
						}
					}
					pabb.setProcessabbList(processabbList);
				}
				List<WaigouPlanLock> wplockList = totalDao.query(" from WaigouPlanLock where sbApplyId =? and sbProcardId=?", pabb.getBbapplyId(),pabb.getProcardId());
				pabb.setWplockList(wplockList);
			}
		}
		// List<ProcardTemplateChangeLog> ptchangeLogList = totalDao
		// .query(
		// "from ProcardTemplateChangeLog where ptbbId in(select id from ProcardTemplateBanBen where procardTemplateBanBenApply.id=?) order by sbMarkId",
		// bbAply.getId());
		// 获取新旧对比差异
		List<ProcardTemplatesbDifference> ptsbDifferenceList1 = new ArrayList<ProcardTemplatesbDifference>();
		List<ProcardTemplatesbDifference> ptsbDifferenceList = totalDao.query(
				"from ProcardTemplatesbDifference where sbApplyId=?", bbAply
						.getId());
		if (ptsbDifferenceList != null && ptsbDifferenceList.size() > 0) {
			for (ProcardTemplatesbDifference ptsbDifference : ptsbDifferenceList) {
				List<ProcardTemplatesbDifferenceDetail> ptsbDifferencedetailList = totalDao
						.query(
								"from ProcardTemplatesbDifferenceDetail where ptsbdId=? order by datatype,xuhao",
								ptsbDifference.getId());
				if (ptsbDifferencedetailList != null
						&& ptsbDifferencedetailList.size() > 0) {
					ptsbDifference.setPtsbddList(ptsbDifferencedetailList);
					ptsbDifferenceList1.add(ptsbDifference);
				}
			}
		}
		bbAply.setPtsbdifferenceList(ptsbDifferenceList1);
		// 获取设变需要评审的必选组别
		List<String> pszbList = new ArrayList<String>();// 人员组别
		// 获取设变需要评审的推荐组别
		List<String> pszbList2 = new ArrayList<String>();// 人员组别
		if (ptsbDifferenceList1.size() > 0) {
			// 1.成本(必须)
			pszbList.add("设变成本组");
			// 2.生管()
			Float sgCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcardTemplatesbDifferenceDetail where  ptsbdId in(select id from ProcardTemplatesbDifference where  sbApplyId=?) and ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))",
							bbAply.getId());
			if (sgCount != null && sgCount > 0) {
				if (fysCount > 0) {
					pszbList.add("设变生管组");
				} else {
					pszbList2.add("设变生管组");
				}
			}
			// 3.外委
			Float wwCount1 = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from WaigouPlan where status not in('入库','删除','取消') and type='外委' and markId in (select markId from ProcardTemplatesbDifference  where sbApplyId=? and id in (select ptsbdId from ProcardTemplatesbDifferenceDetail where ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))))",
							bbAply.getId());
			Float wwCount2 = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from WaigouWaiweiPlan where status in('待入库','待激活') and markId in (select markId from ProcardTemplatesbDifference  where sbApplyId=? and id in (select ptsbdId from ProcardTemplatesbDifferenceDetail where ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))))",
							bbAply.getId());
			Float wwCount3 = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcessInforWWApplyDetail where processStatus in('预选未审批','合同待确认','外委待下单') and (dataStatus is null or dataStatus not in('删除','取消')) and markId in (select markId from ProcardTemplatesbDifference  where sbApplyId=? and id in (select ptsbdId from ProcardTemplatesbDifferenceDetail where ((datatype='本身' and sxName='版本' ) or datatype in('子件','工序','图纸'))))",
							bbAply.getId());
			if (wwCount1 == null) {
				wwCount1 = 0f;
			}
			if (wwCount2 == null) {
				wwCount2 = 0f;
			}
			if (wwCount3 == null) {
				wwCount3 = 0f;
			}
			if ((wwCount1 + wwCount2 + wwCount3) > 0) {
				if (fysCount > 0) {
					pszbList.add("设变外委组");
				} else {
					pszbList2.add("设变外委组");
				}
			}
		}
		Float pmcCount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardTemplateAboutsb where procardStyle='外购' and kgliao!='CS' and sbApplyId=? and yongliao<>0",
						bbAply.getId());
		if (pmcCount != null && pmcCount > 0) {
			// 4.PMC()
			if (fysCount > 0) {
				pszbList.add("设变物控组");
			} else {
				pszbList2.add("设变物控组");
			}
			// 5.外购
			if (fysCount > 0) {
				pszbList.add("设变外购组");
			} else {
				pszbList2.add("设变外购组");
			}
		}
		// 6.品质()
		// 7.仓库()
		Float ckCount1 = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from Goods where goodsCurQuantity>0 and goodsMarkId in(select markId from ProcardTemplateAboutsb where procardStyle='外购' and yongliao<>0 and sbApplyId=? )",
						bbAply.getId());
		if (ckCount1 != null && ckCount1 > 0) {
			if (fysCount > 0) {
				pszbList.add("设变仓库组");
			} else {
				pszbList2.add("设变仓库组");
			}
		}
		Object[] objs = new Object[] { bbAply, pabbList1, pabbList2, pszbList,
				pszbList2 };
		return objs;
	}

	@Override
	public List findCpryList() {
		// TODO Auto-generated method stub
		List<AssessPersonnel> apList = totalDao
				.query(" from AssessPersonnel where usersGroup.groupName='设变初评人员'");
		if (apList != null && apList.size() > 0) {
			List<AssessPersonnel> returnList = new ArrayList<AssessPersonnel>();
			List<Integer> userIdList = new ArrayList<Integer>();
			for (AssessPersonnel ap : apList) {
				if (!userIdList.contains(ap.getUserId())) {
					returnList.add(ap);
					userIdList.add(ap.getUserId());
				}
			}
			return returnList;
		}
		return null;
	}

	@Override
	public List<ProcardTemplate> findSbProcardTemByRootId(Integer rootId) {
		// TODO Auto-generated method stub
		if ((Object) rootId != null && rootId > 0) {
			String hql = "from ProcardTemplate where rootId=?  and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除') order by xuhao";
			List<ProcardTemplate> ptList = totalDao.query(hql, rootId);
			if (ptList != null && ptList.size() > 0) {
				for (ProcardTemplate pt : ptList) {
					if (pt.getMarkId().equals("W21261658Z")) {
						System.out.println(pt.getBzStatus());
					}
					Integer cansb = 0;// 0表示可以选
					if (pt.getBzStatus() == null
							|| !pt.getBzStatus().equals("已批准")) {
						cansb = 1;
						pt.setCansb(cansb);
						continue;
					}
					if (pt.getSbStatus() != null
							&& !pt.getSbStatus().equals("")
							&& !pt.getSbStatus().equals("同意")
							&& !pt.getSbStatus().equals("打回")) {
						cansb = 1;
					} else {
						Float count = (Float) totalDao
								.getObjectByCondition(
										"select count(*) from ProcardTemplateBanBen where markId=? and productStyle=?"
												+ "and (procardTemplateBanBenApply.processStatus is null or procardTemplateBanBenApply.processStatus not in('删除','取消','关联并通知生产','生产后续','上传佐证','关闭'))",
										pt.getMarkId(), pt.getProductStyle());

						if (count != null && count > 0) {
							cansb = 1;
						}
					}
					pt.setCansb(cansb);
				}
			}
			return ptList;
		}
		return null;
	}

	@Override
	public String submitchupin(ProcardTemplateBanBenJudges ptbbJudges,
			String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		String nowTime = Util.getDateTime();
		if (user == null) {
			return "请先登录";
		}
		ProcardTemplateBanBenJudges old = (ProcardTemplateBanBenJudges) totalDao
				.getObjectById(ProcardTemplateBanBenJudges.class, ptbbJudges
						.getId());
		if (old == null) {
			return "没有找到对应的初评人员";
		}
		if (!old.getUserId().equals(user.getId())) {
			return "您没有提交此初评权限!";
		}
		ProcardTemplateBanBenApply ptbbApply = old.getPtbbApply();
		if (!ptbbApply.getProcessStatus().equals("项目主管初评")) {
			return "当前状态不是项目主管初评!";
		}
		ptbbApply.setRemark(remark);
		old.setRemark(ptbbJudges.getRemark());
		old.setPlTime(nowTime);
		Integer[] userId = null;
		Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply.getPtbbjset();
		if (old.getSelectUserId() == null
				|| old.getSelectUserId().length() == 0) {// 还未设定
			if (ptbbJudges.getSelectUserId() != null
					&& ptbbJudges.getSelectUserId().length() > 0) {// 指定
				Users xmgcs = (Users) totalDao.getObjectById(Users.class,
						Integer.parseInt(ptbbJudges.getSelectUserId()));
				if (xmgcs != null) {
					// if(ptbbjSet==null){
					// ptbbjSet=new HashSet<ProcardTemplateBanBenJudges>();
					// }
					old.setSelectUserId(ptbbJudges.getSelectUserId());
					old.setSelectUsers(xmgcs.getName());
					ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
					ptbbj.setDept(xmgcs.getDept());// 部门
					ptbbj.setUserId(xmgcs.getId());// 项目工程师id
					ptbbj.setUserName(xmgcs.getName());// 评审名称
					ptbbj.setUserCode(xmgcs.getCode());// 用户工号
					ptbbj.setAddTime(nowTime);// 添加时间
					ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
					ptbbj.setJudgeType("工程师");// 评审类别（初评,工程师,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
					ptbbj.setJudgedId(old.getId());
					ptbbjSet.add(ptbbj);
					totalDao.save(ptbbj);
					// 接收消息人员
					userId = new Integer[] { xmgcs.getId() };
					ptbbApply.setPtbbjset(ptbbjSet);
					totalDao.update(ptbbApply);
				}

			}
		} else {// 已指定
			if (ptbbJudges.getSelectUserId() == null
					|| ptbbJudges.getSelectUserId().length() == 0) {// 去除指定
				old.setSelectUserId(null);
				old.setSelectUsers(null);
				for (ProcardTemplateBanBenJudges pj : ptbbjSet) {
					if (pj.getJudgeType().equals("工程师")
							&& pj.getJudgedId().equals(old.getId())) {
						ptbbjSet.remove(pj);
						totalDao.delete(pj);
						break;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);
				totalDao.update(ptbbApply);
				// } else if (!ptbbJudges.getSelectUserId().equals(
				// old.getSelectUserId())) {// 重新指定
			} else {// 重新指定
				for (ProcardTemplateBanBenJudges pj : ptbbjSet) {// 先去除原有的指定
					if (pj.getJudgeType().equals("工程师")
							&& pj.getJudgedId().equals(old.getId())) {
						ptbbjSet.remove(pj);
						totalDao.delete(pj);
						break;
					}
				}
				Users xmgcs = (Users) totalDao.getObjectById(Users.class,
						Integer.parseInt(ptbbJudges.getSelectUserId()));
				if (xmgcs != null) {
					old.setSelectUserId(ptbbJudges.getSelectUserId());
					old.setSelectUsers(xmgcs.getName());
					ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
					ptbbj.setDept(xmgcs.getDept());// 部门
					ptbbj.setUserId(xmgcs.getId());// 项目工程师id
					ptbbj.setUserName(xmgcs.getName());// 评审名称
					ptbbj.setUserCode(xmgcs.getCode());// 用户工号
					ptbbj.setAddTime(nowTime);// 添加时间
					ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
					ptbbj.setJudgeType("工程师");// 评审类别（初评,工程师,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
					ptbbj.setJudgedId(old.getId());
					ptbbjSet.add(ptbbj);
					totalDao.save(ptbbj);
					Calendar ca = new Calendar();
					ca.setTitle("设变内审");
					ca.setThingContent(ptbbApply.getApplicantName() + "对总成为:"
							+ ptbbApply.getMarkId() + ",业务件号为："
							+ ptbbApply.getYwMarkId() + "的BOM发起设变,"
							+ old.getUserName() + "指派你作为项目工程师,请前往处理.设变单号:"
							+ ptbbApply.getSbNumber());
					ca.setStart(nowTime);
					ca.setAddDateTime(nowTime);
					ca.setUserId(xmgcs.getId());// 所属人id
					ca.setUserName(xmgcs.getName());// 所属人用户名称
					ca.setCode(xmgcs.getCode());// 所属人工号
					ca.setDept(xmgcs.getDept());// 所属人部门
					totalDao.save(ca);
					// 接收消息人员
					userId = new Integer[] { xmgcs.getId() };
					ptbbApply.setPtbbjset(ptbbjSet);
					totalDao.update(ptbbApply);
				}

			}
		}
		if (userId != null) {// 发送消息
			AlertMessagesServerImpl.addAlertMessages("设变内审", ptbbApply
					.getApplicantName()
					+ "对总成为:"
					+ ptbbApply.getMarkId()
					+ ",业务件号为："
					+ ptbbApply.getYwMarkId()
					+ "的BOM发起设变,"
					+ old.getUserName()
					+ "指派你作为项目工程师,请前往处理.设变单号:" + ptbbApply.getSbNumber(),
					userId,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ ptbbApply.getId(), true, "no");
		}
		ptbbApply.setLastop("主管初评");
		ptbbApply.setLastUserId(user.getId());
		ptbbApply.setLastUserName(user.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		totalDao.update(ptbbApply);
		totalDao.update(old);
		Float count = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='初评'  and (remark is null or remark='' or selectUserId is null or selectUserId='')",
						old.getPtbbApply().getId());
		if (count == null || count == 0) {
			ptbbApply.setProcessStatus("选择设变零件");
			totalDao.update(ptbbApply);
			return "提交成功,初评结束!";
		}
		return "提交成功!";
	}

	@Override
	public String noprocardns(ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		String nowTime = Util.getDateTime();
		if (user == null) {
			return "请先登录";
		}
		ProcardTemplateBanBenJudges old = (ProcardTemplateBanBenJudges) totalDao
				.getObjectById(ProcardTemplateBanBenJudges.class, ptbbJudges
						.getId());
		if (old == null) {
			return "没有找到对应的内部评审人员";
		}
		if (!old.getUserId().equals(user.getId())) {
			return "您没有提交此内部评审权限!";
		}
		ProcardTemplateBanBenApply ptbbApply = old.getPtbbApply();
		if (!ptbbApply.getProcessStatus().equals("各部门评审")) {
			return "当前状态不是各部门评审!";
		}
		old.setRemark(ptbbJudges.getRemark());
		old.setPlTime(nowTime);
		totalDao.update(old);
		ptbbApply.setLastop("各部门评审");
		ptbbApply.setLastUserId(user.getId());
		ptbbApply.setLastUserName(user.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		totalDao.update(ptbbApply);
		Float count = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审'  and (remark is null or remark='')",
						old.getPtbbApply().getId());
		if (count == null || count == 0) {
			Integer uId = (Integer) totalDao
					.getObjectByCondition(
							"select userId from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='工程师' ",
							old.getPtbbApply().getId());
			Integer[] userId = new Integer[] { uId };
			if (userId != null) {// 发送消息
				AlertMessagesServerImpl.addAlertMessages("设变内审", "总成为:"
						+ ptbbApply.getMarkId() + ",业务件号为："
						+ ptbbApply.getYwMarkId() + "的BOM内部评审结束" + "请前往处理.",
						userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
			ptbbApply.setProcessStatus("资料更新");
			totalDao.update(ptbbApply);
			return "提交成功,初评结束!";
		}
		return "提交成功!";
	}

	@Override
	public List<DeptNumberVo> getDeptTreeVos(String ids) {
		// TODO Auto-generated method stub
		List<Integer> deptIds = new ArrayList<Integer>();
		if (ids != null && ids.length() > 0) {
			String[] idStrs = ids.split(";");
			if (idStrs != null && idStrs.length > 0) {
				for (String idStr : idStrs) {
					try {
						deptIds.add(Integer.parseInt(idStr));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
		List<DeptNumber> all = totalDao.query("from DeptNumber");
		if (all.size() > 0) {
			List<DeptNumberVo> vos = new ArrayList<DeptNumberVo>();
			for (DeptNumber d : all) {
				DeptNumberVo dVo = new DeptNumberVo();
				dVo.SetDeptContext(d);
				if (d.getId() != null && deptIds.contains(d.getId())) {
					dVo.setIsHad(true);
				} else {
					dVo.setIsHad(false);
				}
				vos.add(dVo);
			}
			return vos;
		}

		return null;
	}

	@Override
	public List<ProcardTemplate> getProcardTemplateMsg(String type,
			String markId) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> ptList = totalDao
				.query(
						"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成'  and "
								+ type + "=? ", markId);
		if (ptList == null || ptList.size() == 0) {
			ptList = totalDao
					.query(
							"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成'  and "
									+ type + "=? ", "W" + markId + "Z");
		}
		if (ptList == null || ptList.size() == 0) {
			ptList = totalDao
					.query(
							"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成'  and "
									+ type + "=? ", markId + "-WZ");
		}
		if (ptList == null || ptList.size() == 0) {
			ptList = totalDao
					.query(
							"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成'  and "
									+ type + "=? ", "W" + markId + "K");
		}
		if (ptList != null && ptList.size() > 0) {
			List<ProcardTemplate> ptList2 = new ArrayList<ProcardTemplate>();
			for (ProcardTemplate pt : ptList) {
				ProcardTemplate pt2 = new ProcardTemplate();
				pt2.setMarkId(pt.getMarkId());
				pt2.setYwMarkId(pt.getYwMarkId());
				pt2.setProName(pt.getProName());
				pt2.setBanBenNumber(pt.getBanBenNumber());
				pt2.setBanci(pt.getBanci());
				pt2.setBzStatus(pt.getBzStatus());
				pt2.setProductStyle(pt.getProductStyle());
				ptList2.add(pt2);
			}
			return ptList2;

		}
		return null;
	}

	@Override
	public Object[] addSbApply(ProcardTemplateBanBenApply ptbbApply,
			Integer id, String gygfFileName, String newFileName) {
		// TODO Auto-generated method stub
		Users users = Util.getLoginUser();
		if (users == null) {
			return new Object[] { "请先登录" };
		}
		String nowTime = Util.getDateTime();
		List<ProcardTemplateBanBenApply> ptbbaList = ptbbApply.getPtbbaList();
		Integer[] userId = null;
		if (ptbbaList != null && ptbbaList.size() > 0) {
			// 等发起人结论以后在申请
			// ptbbApply.setepId;//申请id
			// ptbbApply.setstatus;//状态（未审批,审批中,同意,打回）
			int count = 0;
			for (ProcardTemplateBanBenApply ptbba : ptbbaList) {
				ProcardTemplate totalPt = null;
				ptbba.setLastop("添加");
				ptbba.setLastUserId(users.getId());
				ptbba.setLastUserName(users.getName());
				ptbba.setLastTime(nowTime);
				ptbba.setOutbsNumber(ptbbApply.getOutbsNumber());
				ptbba.setSbSource(ptbbApply.getSbSource());
				ptbba.setSbreason(ptbbApply.getSbreason());
				ptbba.setSbType(ptbbApply.getSbType());
				ptbba.setAboutPlace(ptbbApply.getAboutPlace());
				ptbba.setRemark(ptbbApply.getRemark());

				/** 设变对接项 **/
				ptbba.setWorkItemId(ptbbApply.getWorkItemId());
				ptbba.setEcType(ptbbApply.getEcType());

				if (ptbba.getProductStyle() == null
						|| ptbba.getProductStyle().length() == 0) {
					totalPt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') "
											+ "and (dataStatus is null or dataStatus!='删除') and procardStyle='总成' and markId=? ",
									ptbba.getMarkId());
					if (totalPt != null) {
						return new Object[] { "未选定BOM类型" };
					}
				} else {
					if (ptbba.getMarkId() != null
							&& ptbba.getMarkId().length() > 0) {
						totalPt = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成' and markId=? and productStyle=?",
										ptbba.getMarkId(), ptbba
												.getProductStyle());
					}
					if (totalPt == null && ptbba.getYwMarkId() != null
							&& ptbba.getYwMarkId().length() > 0) {
						totalPt = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成' and ywMarkId=? and productStyle=?",
										ptbba.getYwMarkId(), ptbba
												.getProductStyle());
					}
				}
				// if (ptbbApply.getRemark() == null
				// || ptbbApply.getRemark().length() == 0) {
				// return new Object[] { "请填写备注!" };
				// }
				if (totalPt != null) {
					ptbba.setMarkId(totalPt.getMarkId());// 总成件号
					ptbba.setYwMarkId(totalPt.getYwMarkId());
					ptbba.setProName(totalPt.getProName());// 总成名称
					ptbba.setBanbenNumber(totalPt.getBanBenNumber());// 总成版本号
					ptbba.setRootId(totalPt.getId());
					// private Float dzcount;//内部计划待转换生产BOM数量
					Double dzcount = (Double) totalDao
							.getObjectByCondition(
									"select sum(num-ifnull(turnCount,0)) from "
											+ " InternalOrderDetail where (pieceNumber=? or pieceNumber=?) "
											+ " and id not in(select ioDetailId from Product_Internal where productId in(select id from ProductManager where status in('删除','取消')))",
									totalPt.getMarkId(),
									totalPt.getYwMarkId() == null ? ""
											: totalPt.getYwMarkId());
					// private Float wjhcount;//生产BOM未运算MRP数量
					Float wjhcount = (Float) totalDao
							.getObjectByCondition(
									"select sum(filnalCount) from Procard where markId=? and wlstatus='待定' and status!='取消'"
											+ " and id=rootId and procardStyle='总成' "
											+ " and id not in(select rootId from Procard where jihuoStatua='激活'  and status='初始' and rootId is not null)",
									totalPt.getMarkId());
					// private Float wpccount;//生产BOM运算MRP未排产数量
					Float wpccount = (Float) totalDao
							.getObjectByCondition(
									"select sum(filnalCount) from Procard where markId=? and (hasPlan is null or hasPlan=0)  and status!='取消'"
											+ " and id=rootId and procardStyle='总成' "
											+ " and id in(select rootId from Procard where jihuoStatua='激活'  and status not in('初始','取消','入库') and rootId is not null)",
									totalPt.getMarkId());
					// private Float pccount;//生产BOM已排产数量
					Float pccount = (Float) totalDao
							.getObjectByCondition(
									"select sum(filnalCount) from Procard where markId=? and hasPlan>0  and status not in('取消','入库')"
											+ " and id=rootId and procardStyle='总成' ",
									totalPt.getMarkId());
					ptbba
							.setDzcount(dzcount == null ? 0 : dzcount
									.floatValue());
					ptbba.setWjhcount(wjhcount == null ? 0 : wjhcount);
					ptbba.setWpccount(wpccount == null ? 0 : wpccount);
					ptbba.setPccount(pccount == null ? 0 : pccount);
					// } else {
					// return new Object[] { "此BOM系统中不存在!" };
				}
				ptbba.setApplicantId(users.getId());// 申请人
				ptbba.setApplicantdept(users.getDept());// 申请人部门
				ptbba.setApplicantName(users.getName());// 申请人名称
				ptbba.setApplyTime(nowTime);// 申请时间
				ptbba.setOldFileName(gygfFileName);
				ptbba.setFileName(newFileName);
				// ptbbApply.setStatus("正常");
				// 设变编号
				String before = null;
				if (ptbba.getProductStyle() == null
						|| ptbba.getProductStyle().length() == 0) {
					before = "ECNNB_" + Util.getDateTime("yyyyMMdd");
				} else if (ptbba.getProductStyle().equals("批产")) {
					before = "ECNPC_" + Util.getDateTime("yyyyMMdd");
				} else {
					before = "ECNSZ_" + Util.getDateTime("yyyyMMdd");
				}
				String maxnumber = (String) totalDao
						.getObjectByCondition("select sbNumber from ProcardTemplateBanBenApply where sbNumber like '"
								+ before + "%' order by sbNumber desc");
				if (maxnumber != null && !"".equals(maxnumber)) {
					// String number1 = paymentVoucher2.getNumber();
					String now_number = maxnumber.split(before)[1];
					Integer number2 = Integer.parseInt(now_number) + 1;
					DecimalFormat df = new DecimalFormat("00000");
					String number3 = df.format(number2);
					ptbba.setSbNumber(before + number3);
				} else {
					ptbba.setSbNumber(before + "00001");
				}
				userId = null;
				if (id != null) {
					ptbba.setProcessStatus("项目主管初评");// 进程状态(设变发起,分发主管,项目主管初评,初步结论,各部门评审,发起人结论,,资料更新,关联并通知生产，关)
					Set<ProcardTemplateBanBenJudges> ptbbjSet = new HashSet<ProcardTemplateBanBenJudges>();
					AssessPersonnel ap = (AssessPersonnel) totalDao
							.getObjectById(AssessPersonnel.class, id);
					if (ap != null) {
						List<ProcardTemplateBanBenJudges> hadcpList = totalDao
								.query(
										"from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='初评'",
										ptbba.getId());
						boolean same = false;
						if (hadcpList != null && hadcpList.size() > 0) {
							for (ProcardTemplateBanBenJudges hadcp : hadcpList) {
								if (hadcp.getUserId().equals(ap.getUserId())) {
									same = true;
								} else {
									totalDao.delete(hadcp);
								}
							}
						}
						if (!same) {
							ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
							ptbbj.setDept(ap.getDept());// 部门
							ptbbj.setUserId(ap.getUserId());// 评审人员id
							ptbbj.setUserName(ap.getUserName());// 评审名称
							ptbbj.setUserCode(ap.getCode());// 用户
							// ptbbj.setremark;//评论
							// ptbbj.setsblv;//影响级别
							ptbbj.setAddTime(nowTime);// 添加时间
							// ptbbj.setplTime;//评论时间
							ptbbj.setPtbbApply(ptbba);// 版本升级主表
							// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
							ptbbj.setJudgeType("初评");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
							ptbbjSet.add(ptbbj);
							totalDao.save(ptbbj);
						}
						// 发送消息
						userId = new Integer[] { ap.getUserId() };
						ptbba.setPtbbjset(ptbbjSet);
					}
				} else {
					ptbba.setProcessStatus("分发项目组");
				}
				totalDao.save(ptbba);
				if (userId != null) {
					AlertMessagesServerImpl.addAlertMessages("设变初评", users
							.getName()
							+ "对总成为:"
							+ ptbba.getMarkId()
							+ ",业务件号为："
							+ ptbba.getYwMarkId() + "的BOM发起设变，请前去初评", userId,
							"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
									+ ptbba.getId(), true, "no");
				}
				count++;
			}
		}
		return new Object[] { "发起成功" };
	}

	@Override
	public String sbffxmz(ProcardTemplateBanBenApply bbAply, Integer id) {
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply old = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		if (old == null) {
			return "没有找到对应的设变单!";
		}
		if (id == null || id == 0) {
			return "请选择项目主管!";
		}
		if (!old.getProcessStatus().equals("分发项目组")) {
			return "设变单状态为:" + old.getProcessStatus() + "不允许指派项目主管!";
		}
		old.setRemark(bbAply.getRemark());
		old.setAboutPlace(bbAply.getAboutPlace());
		Integer[] userId = null;
		old.setProcessStatus("项目主管初评");// 进程状态(设变发起,分发主管,项目主管初评,初步结论,各部门评审,发起人结论,,资料更新,关联并通知生产，关)
		old.setLastop("指派项目组");
		old.setLastUserId(users.getId());
		old.setLastUserName(users.getName());
		old.setLastTime(Util.getDateTime());
		Set<ProcardTemplateBanBenJudges> ptbbjSet = old.getPtbbjset();
		AssessPersonnel ap = (AssessPersonnel) totalDao.getObjectById(
				AssessPersonnel.class, id);
		if (ap != null) {
			List<ProcardTemplateBanBenJudges> hadcpList = totalDao
					.query(
							"from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='初评'",
							bbAply.getId());
			boolean same = false;
			if (hadcpList != null && hadcpList.size() > 0) {
				for (ProcardTemplateBanBenJudges hadcp : hadcpList) {
					if (hadcp.getUserId().equals(ap.getUserId())) {
						same = true;
					} else {
						hadcp.setPtbbApply(null);
						if (ptbbjSet != null && ptbbjSet.size() > 0) {
							ptbbjSet.remove(hadcp);
						}
						totalDao.delete(hadcp);
					}
				}
			}
			if (!same) {
				ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
				ptbbj.setDept(ap.getDept());// 部门
				ptbbj.setUserId(ap.getUserId());// 评审人员id
				ptbbj.setUserName(ap.getUserName());// 评审名称
				ptbbj.setUserCode(ap.getCode());// 用户
				// ptbbj.setremark;//评论
				// ptbbj.setsblv;//影响级别
				ptbbj.setAddTime(Util.getDateTime());// 添加时间
				// ptbbj.setplTime;//评论时间
				ptbbj.setPtbbApply(old);// 版本升级主表
				// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
				ptbbj.setJudgeType("初评");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
				ptbbjSet.add(ptbbj);
				totalDao.save(ptbbj);
				// 发送消息
				userId = new Integer[] { ap.getUserId() };
				old.setPtbbjset(ptbbjSet);
			}
		}
		totalDao.update(old);
		if (userId != null) {
			AlertMessagesServerImpl.addAlertMessages("设变初评", users.getName()
					+ "对总成为:" + old.getMarkId() + ",业务件号为：" + old.getYwMarkId()
					+ "的BOM发起设变，请前去初评", userId,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ old.getId(), true, "no");
		}
		return "指派成功!";

	}

	@Override
	public Integer getPtIdBybbApplyId(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplateBanBenApply ptBbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptBbApply != null) {
			if (ptBbApply.getRootId() != null) {
				return ptBbApply.getRootId();
			}
			if (ptBbApply.getProductStyle() == null) {
				return 0;
			}
			Integer id2 = null;
			if (ptBbApply.getMarkId() != null
					&& ptBbApply.getMarkId().length() > 0) {
				id2 = (Integer) totalDao
						.getObjectByCondition(
								"select id from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成'  and markId=? and productStyle=?",
								ptBbApply.getMarkId(), ptBbApply
										.getProductStyle());
			} else if (ptBbApply.getYwMarkId() != null
					&& ptBbApply.getYwMarkId().length() > 0) {
				id2 = (Integer) totalDao
						.getObjectByCondition(
								"select id from ProcardTemplate where (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除') and procardStyle='总成'  and ywMarkId=? and productStyle=?",
								ptBbApply.getYwMarkId(), ptBbApply
										.getProductStyle());

			}
			if (id2 == null) {
				return 0;
			} else {
				return id2;
			}
		}
		return 0;
	}

	@Override
	public String submitnp(ProcardTemplateBanBenJudges ptbbJudges,
			List<ProcardBanBenJudge> pbbJudgeList,
			List<ProcardBanBenJudge> pbbJudgeList2, String tag) {
		// TODO Auto-generated method stub
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录!";
		}
		String nowTime = Util.getDateTime();
		if (ptbbJudges != null && ptbbJudges.getId() != null) {
			ProcardTemplateBanBenJudges old = (ProcardTemplateBanBenJudges) totalDao
					.getObjectById(ProcardTemplateBanBenJudges.class,
							ptbbJudges.getId());
			if (old != null) {
				old.setRemark(ptbbJudges.getRemark());
				old.setPlTime(nowTime);
				old.setBackTag(null);
				totalDao.update(old);
				ProcardTemplateBanBenApply ptbbApply = old.getPtbbApply();
				if (ptbbApply.getProcessStatus().equals("结论")) {
					// 获取工程师Id
					Integer gcsUid = (Integer) totalDao
							.getObjectByCondition(
									"select userId from ProcardTemplateBanBenJudges where judgeType='工程师' and ptbbApply.id=?",
									ptbbApply.getId());
					if (gcsUid == null || !users.getId().equals(gcsUid)) {
						return "您不是此设变工程师,操作失败!当前设变状态为:结论。";
					}
				}
				if (pbbJudgeList != null && pbbJudgeList.size() > 0) {
					for (ProcardBanBenJudge pbbJudge : pbbJudgeList) {
						if (pbbJudge != null && pbbJudge.getRemark() != null
								&& pbbJudge.getRemark().length() > 0) {
							if (pbbJudge.getRemark().equals("请选择")) {
								return "请选择有效的处理方案!";
							}
							ProcardBanBenJudge had = (ProcardBanBenJudge) totalDao
									.getObjectByCondition(
											"from ProcardBanBenJudge where processbbJudgeId=? and ptbbJudgeId=?",
											pbbJudge.getProcessbbJudgeId(),
											pbbJudge.getPtbbJudgeId());
							if (had != null) {
								had.setRemark(pbbJudge.getRemark());
								had.setBcremark(pbbJudge.getBcremark());
								totalDao.update(had);
							} else {
								pbbJudge.setUserId(users.getId());
								pbbJudge.setUserName(users.getName());
								pbbJudge.setAddTime(nowTime);
								totalDao.save(pbbJudge);
							}
							if (ptbbApply.getProcessStatus().equals("结论")) {// 赋值最终处理方案
								ProcessAboutBanBenApply processbb = (ProcessAboutBanBenApply) totalDao
										.getObjectById(
												ProcessAboutBanBenApply.class,
												pbbJudge.getProcessbbJudgeId());
								if (processbb != null) {
									processbb.setClType(pbbJudge.getRemark());
									totalDao.update(processbb);
								}
							}

						}
					}
				}
				if (pbbJudgeList2 != null && pbbJudgeList2.size() > 0) {
					for (ProcardBanBenJudge pbbJudge2 : pbbJudgeList2) {
						if (pbbJudge2 != null && pbbJudge2.getRemark() != null
								&& pbbJudge2.getRemark().length() > 0) {
							if (pbbJudge2.getRemark().equals("请选择")) {
								return "请选择有效的处理方案!";
							}
							ProcardBanBenJudge had = (ProcardBanBenJudge) totalDao
									.getObjectByCondition(
											"from ProcardBanBenJudge where processbbJudgeId=? and ptbbJudgeId=?",
											pbbJudge2.getProcessbbJudgeId(),
											pbbJudge2.getPtbbJudgeId());
							if (had != null) {
								had.setRemark(pbbJudge2.getRemark());
								totalDao.update(had);
							} else {
								pbbJudge2.setUserId(users.getId());
								pbbJudge2.setUserName(users.getName());
								pbbJudge2.setAddTime(nowTime);
								totalDao.save(pbbJudge2);
							}
							if (ptbbApply.getProcessStatus().equals("结论")) {// 赋值最终处理方案
								ProcessAboutBanBenApply processbb2 = (ProcessAboutBanBenApply) totalDao
										.getObjectById(
												ProcessAboutBanBenApply.class,
												pbbJudge2.getProcessbbJudgeId());
								if (processbb2 != null) {
									processbb2.setClType(pbbJudge2.getRemark());
									totalDao.update(processbb2);
								}
							}
						}
					}
				}
				ptbbApply.setLastop("评选处理方案");
				ptbbApply.setLastUserId(users.getId());
				ptbbApply.setLastUserName(users.getName());
				ptbbApply.setLastTime(Util.getDateTime());
				if (ptbbApply.getProcessStatus().equals("各部门评审")) {
					Float count = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='内审' and (backTag is null or backTag!='yes') and (remark is null or remark='') ",
									ptbbApply.getId());
					if (count == null || count == 0) {
						ptbbApply.setProcessStatus("结论");
						totalDao.update(ptbbApply);
					}
				} else if (ptbbApply.getProcessStatus().equals("结论")) {
					// 获取所有关联并通知生产件
					List<ProcardAboutBanBenApply> pabbList = totalDao.query(
							"from ProcardAboutBanBenApply where bbapplyId=?",
							ptbbApply.getId());
					if (pabbList != null && pabbList.size() > 0) {
						for (ProcardAboutBanBenApply pabb : pabbList) {
							Set<ProcessAboutBanBenApply> prcoessabbSet = pabb
									.getProcessabbSet();
							String clType = "顺延";
							if (prcoessabbSet != null
									&& prcoessabbSet.size() > 0) {
								for (ProcessAboutBanBenApply processbb : prcoessabbSet) {
									if (processbb.getClType() == null
											|| !processbb.getClType().equals(
													"顺延")) {
										clType = processbb.getClType();
									}
								}
							}
							if (clType != null && clType.equals("顺延")) {// 放开生产锁定
								pabb.setStatus("完成");
								totalDao.update(pabb);
								Procard procard = (Procard) totalDao
										.getObjectById(Procard.class, pabb
												.getProcardId());
								if (procard != null
										&& procard.getStatus().equals("设变锁定")) {
									procard.setStatus(procard.getOldStatus());
									totalDao.update(procard);
								}
							}
						}
					}
					ptbbApply.setProcessStatus("资料更新");
					totalDao.update(ptbbApply);
				}
				totalDao.update(ptbbApply);
				return "评审成功";
			}
		}

		return "评审异常,没有找到目标评审员!";
	}

	@Override
	public String submitnp(Integer id) {
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录!";
		}
		String nowTime = Util.getDateTime();
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply == null) {
			return "没有找到目标设变单!";
		}
		if (!ptbbApply.getProcessStatus().equals("各部门评审")) {
			return "当前设变进度状态为:" + ptbbApply.getProcessStatus() + ",操作有误!";
		}
		String zpTime = ptbbApply.getZpTime();
		ptbbApply.setQznext("no");
		ProcardTemplateBanBenJudges pj = (ProcardTemplateBanBenJudges) totalDao
				.getObjectByCondition(
						"from ProcardTemplateBanBenJudges where ptbbApply.id=? and judgeType='工程师' and userId=?",
						ptbbApply.getId(), users.getId());
		if (pj == null) {
			return "对不起，您不是这个设变单的工程师,不能执行此操作!";
		}
		if (zpTime != null) {
			try {
				Date date = Util.StringToDate(zpTime, "yyyy-MM-dd HH:mm:ss");
				String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='设变评审最大时长'";
				String sc = (String) totalDao.getObjectByCondition(hql1);
				if (sc != null) {
					Integer sci = Integer.parseInt(sc);
					Long t1 = date.getTime();
					Long t2 = new Date().getTime();
					if ((t1 + sci * 3600000) < t2) {
						ptbbApply.setQznext("yes");
					}
					ptbbApply.setZdpsTime(sc);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (ptbbApply.getQznext() == null
				|| !ptbbApply.getQznext().equals("yes")) {
			return "没有通过设变最大时长判定!";
		}
		ptbbApply.setLastop("评选处理方案");
		ptbbApply.setLastUserId(users.getId());
		ptbbApply.setLastUserName(users.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		totalDao.update(ptbbApply);
		ptbbApply.setProcessStatus("结论");
		return "" + totalDao.update(ptbbApply);

	}

	@Override
	public String suresb(Integer id, ProcardTemplateBanBenJudges ptbbJudges,
			List<ProcardTemplateBanBen> ptbbList) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		String time = Util.getDateTime();
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply != null) {
			if (!ptbbApply.getProcessStatus().equals("资料更新")) {
				return "当前进度状态为:" + ptbbApply.getProcessStatus() + "操作失败!";
			}
			if (ptbbJudges.getSelectUserId() != null
					&& ptbbJudges.getSelectUserId().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("编制通知")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("编制通知");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);
				AlertMessagesServerImpl.addAlertMessages("BOM设变编制通知", user
						.getName()
						+ "对总成为:"
						+ ptbbApply.getMarkId()
						+ ",业务件号为："
						+ ptbbApply.getYwMarkId() + "的BOM设变申请已通过，请前去编制完善BOM",
						userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
			if (ptbbJudges.getSelectUserId2() != null
					&& ptbbJudges.getSelectUserId2().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId2().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("编制完通知")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("编制完通知");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);

			}
			if (ptbbJudges.getSelectUserId3() != null
					&& ptbbJudges.getSelectUserId3().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId3().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("佐证")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("佐证");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);

			}
			Set<ProcardTemplateBanBen> ptbbSet = ptbbApply
					.getProcardTemplateBanBen();
			if (ptbbSet != null && ptbbSet.size() > 0) {
				Integer[] userId2 = new Integer[ptbbSet.size()];
				int bzi = 0;
				for (ProcardTemplateBanBen ptbb : ptbbSet) {
					for (ProcardTemplateBanBen ptbb2 : ptbbList) {
						if (ptbb2.getId() != null
								&& ptbb2.getId().equals(ptbb.getId())) {
							if (ptbb2.getBianzhiId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getBianzhiId());
								if (u != null) {
									ptbb.setBianzhiId(u.getId());
									ptbb.setBianzhiName(u.getName());
									userId2[bzi] = u.getId();
									bzi++;
								}
							}
							if (ptbb2.getJiaoduiId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getJiaoduiId());
								if (u != null) {
									ptbb.setJiaoduiId(u.getId());
									ptbb.setJiaoduiName(u.getName());
								}
							}
							if (ptbb2.getShenheId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getShenheId());
								if (u != null) {
									ptbb.setShenheId(u.getId());
									ptbb.setShenheName(u.getName());
								}
							}
							if (ptbb2.getPizhunId() != null) {
								Users u = (Users) totalDao.getObjectById(
										Users.class, ptbb2.getPizhunId());
								if (u != null) {
									ptbb.setPizhunId(u.getId());
									ptbb.setPizhunName(u.getName());
								}
							}
						}
					}
					procardTemBanBenLvup(ptbb, user, time, ptbbApply
							.getProductStyle(), ptbbApply.getRootId());
				}
				AlertMessagesServerImpl.addAlertMessages("BOM设变编制通知", user
						.getName()
						+ "对总成为:"
						+ ptbbApply.getMarkId()
						+ ",业务件号为："
						+ ptbbApply.getYwMarkId() + "的BOM设变申请已通过，请前去编制完善BOM",
						userId2,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
			ptbbApply.setProcessStatus("关联并通知生产");
			ptbbApply.setLastop("开启BOM编制");
			ptbbApply.setLastUserId(user.getId());
			ptbbApply.setLastUserName(user.getName());
			ptbbApply.setLastTime(Util.getDateTime());
			totalDao.update(ptbbApply);
		}
		return "BOM模板设变开启,请前往编制";
	}

	private String procardTemBanBenLvup(ProcardTemplateBanBen ptbb, Users user,
			String time, String productStyle, Integer rootId) {
		// TODO Auto-generated method stub
		if (ptbb != null) {
			ProcardTemplate newProcardtemplate = null;// 如果是件号替换，查询出新的件号是否已存在
			if (ptbb.getUptype().equals("件号替换")) {
				if (ptbb.getNewmarkId() == null
						|| ptbb.getNewmarkId().length() == 0) {
					throw new RuntimeException("零件" + ptbb.getMarkId()
							+ "的设变类型为件号替换请填写件号!");
				}
				String newbanbenSql = null;
				if (ptbb.getNewBanBenNumber() == null
						|| ptbb.getNewBanBenNumber().equals("")) {
					newbanbenSql = " and (banBenNumber is null or banBenNumber='')";
				} else {
					newbanbenSql = " and banBenNumber='"
							+ ptbb.getNewBanBenNumber();
				}

				newProcardtemplate = (ProcardTemplate) totalDao
						.getObjectByCondition(
								"from ProcardTemplate where markId=? and productStyle='批产' "
										+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')",
								ptbb.getNewmarkId());
				if (productStyle.equals("批产")) {
					if (newProcardtemplate != null
							&& !Util.isEquals(ptbb.getNewBanBenNumber(),
									newProcardtemplate.getBanBenNumber())) {
						throw new RuntimeException("零件" + ptbb.getMarkId()
								+ "的设变类型为件号替换，新版本号为:"
								+ ptbb.getNewBanBenNumber() + ",系统现有版本为："
								+ newProcardtemplate.getBanBenNumber());
					}
				} else {
					if (newProcardtemplate != null) {
						if (!Util.isEquals(ptbb.getNewBanBenNumber(),
								newProcardtemplate.getBanBenNumber())) {
							String pcbaneben = newProcardtemplate
									.getBanBenNumber();
							newProcardtemplate = (ProcardTemplate) totalDao
									.getObjectByCondition(
											"from ProcardTemplate where markId=? and productStyle='试制' "
													+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
													+ newbanbenSql, ptbb
													.getNewmarkId());
							if (newProcardtemplate == null) {
								throw new RuntimeException("零件"
										+ ptbb.getMarkId()
										+ "的设变类型为件号替换，新版本号为:"
										+ ptbb.getNewBanBenNumber()
										+ ",系统现有批产版本为：" + pcbaneben
										+ "并且系统中也无此版本试制零件!");
							}
						}
					} else {
						newProcardtemplate = (ProcardTemplate) totalDao
								.getObjectByCondition(
										"from ProcardTemplate where markId=? and productStyle='试制' "
												+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
												+ newbanbenSql, ptbb
												.getNewmarkId());
						if (newProcardtemplate == null) {
							newProcardtemplate = (ProcardTemplate) totalDao
									.getObjectByCondition(
											"from ProcardTemplate where markId=? and productStyle='试制' "
													+ "and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')",
											ptbb.getNewmarkId());
							throw new RuntimeException("零件" + ptbb.getMarkId()
									+ "的设变类型为件号替换，新版本号为:"
									+ ptbb.getNewBanBenNumber() + ",系统现无批产此零件"
									+ "并且系统中试制零件版本为:"
									+ newProcardtemplate.getBanBenNumber()
									+ "!");
						}
					}
				}
			}
			String banbenSql = null;
			if (ptbb.getBanBenNumber() == null
					|| ptbb.getBanBenNumber().equals("")) {
				banbenSql = " and (banBenNumber is null or banBenNumber='')";
			} else {
				banbenSql = " and banBenNumber='" + ptbb.getBanBenNumber()
						+ "'";
			}
			if (productStyle.equals("试制") && ptbb.getSingleSb() != null
					&& ptbb.getSingleSb().equals("是")) {
				if (ptbb.getUptype().equals("设变")) {
					throw new RuntimeException("只有在版本升级时才允许单独设变!");
				}
				banbenSql += " and rootId=" + rootId;
			}
			List<ProcardTemplate> ptList = totalDao
					.query(
							"from ProcardTemplate where  markId=? and productStyle=? and (banbenStatus is null or banbenStatus!='历史') and (dataStatus is null or dataStatus!='删除')"
									+ banbenSql, ptbb.getMarkId(), productStyle);
			// 图纸版本升级
			// List<ProcessTemplateFile> processFileList =
			// totalDao.query("from ProcessTemplateFile where banBenNumber=? and markId=?",
			// bbApply.getBanbenNumber(), bbApply.getMarkId());
			//			
			if (ptList != null && ptList.size() > 0) {
				int i = 0;
				for (ProcardTemplate pt : ptList) {
					pt.setSbId(ptbb.getProcardTemplateBanBenApply().getId());
					i++;
					if (i == 1) {
						if (productStyle.equals("批产")) {
							if (ptbb.getUptype().equals("件号替换")
									&& newProcardtemplate != null) {
								// 件号替换并且是系统中已存在的零件直接使用新替换件号的员图纸就可以了
							} else {
								// 批次图纸升级一次性处理
								String tzSql = "from ProcessTemplateFile where markId=? and productStyle='批产' and glId is null and ( status is null or status!='历史') ";
								if (pt.getBanci() == null
										|| pt.getBanci().equals(0)) {
									tzSql += " and (banci is null or banci=0)";
								} else {
									tzSql += " and banci is not null and banci="
											+ pt.getBanci();
								}
								List<ProcessTemplateFile> processFileList = totalDao
										.query(tzSql, pt.getMarkId());
								if (processFileList != null
										&& processFileList.size() > 0) {
									for (ProcessTemplateFile processFile : processFileList) {
										if (processFile.getBanci() == null) {
											processFile.setBanci(0);
										}
										ProcessTemplateFile newfile = new ProcessTemplateFile();
										BeanUtils.copyProperties(processFile,
												newfile, new String[] { "id" });
										newfile.setBzStatus("初始");
										newfile
												.setBanci(processFile
														.getBanci() + 1);// 新版次升级
										if (ptbb.getUptype().equals("件号替换")) {
											newfile.setMarkId(ptbb.getMarkId());
											newfile.setBanBenNumber(ptbb
													.getNewBanBenNumber());
										} else if (ptbb.getUptype().equals(
												"版本升级")) {
											newfile.setBanBenNumber(ptbb
													.getNewBanBenNumber());
										} else {
											newfile.setBanBenNumber(ptbb
													.getBanBenNumber());
										}
										newfile.setStatus("默认");
										totalDao.save(newfile);
										totalDao.update(processFile);
									}
								}
							}
						}
						// 外购件升级库中版本
						if (pt.getProcardStyle().equals("外购")) {
							List<YuanclAndWaigj> wgjList = null;
							String productStylSql = null;
							if (productStyle.equals("批产")) {
								productStylSql = " and  productStyle='批产'";
							} else {
								productStylSql = " and  1=1";
							}
							if (ptbb.getUptype().equals("件号替换")) {
								wgjList = totalDao
										.query(
												"from YuanclAndWaigj where markId=? and (banbenStatus is null or banbenStatus !='历史') "
														+ productStylSql, ptbb
														.getNewmarkId());
							} else {
								wgjList = totalDao
										.query(
												"from YuanclAndWaigj where markId=? and (banbenStatus is null or banbenStatus !='历史')  "
														+ productStylSql, pt
														.getMarkId());
							}
							String waiBanbenNumber = null;
							if (!ptbb.getUptype().equals("设变")) {
								waiBanbenNumber = ptbb.getNewBanBenNumber();
							} else {
								waiBanbenNumber = ptbb.getBanBenNumber();
							}
							if (wgjList != null && wgjList.size() > 0) {
								boolean has = false;
								for (YuanclAndWaigj wgj : wgjList) {
									if (Util.isEquals(waiBanbenNumber, wgj
											.getBanbenhao())) {
										wgj.setStatus("使用");
										totalDao.update(wgj);
										has = true;
										break;
									}

								}
								if (!has) {
									YuanclAndWaigj wgj = wgjList.get(0);
									YuanclAndWaigj wgjnew = new YuanclAndWaigj();
									BeanUtils.copyProperties(wgj, wgjnew,
											new String[] { "id",
													"zhuserOfferSet" });
									wgjnew.setBanbenhao(waiBanbenNumber);
									wgjnew.setPricestatus("新增");
									wgjnew.setBanbenStatus("默认");
									wgjnew.setStatus("使用");
									wgjnew.setAddTime(time);
									wgjnew.setAddUserCode(user.getCode());
									wgjnew.setAddUserName(user.getName());
									totalDao.save(wgjnew);
									if (pt.getProductStyle().equals("批产")
											|| ptbb.getSingleSb() == null
											|| !ptbb.getSingleSb().equals("是")) {
										wgj.setBanbenStatus("历史");
										totalDao.update(wgj);
									}
								}
							} else if (ptbb.getUptype().equals("件号替换")) {
								YuanclAndWaigj wgjnew = new YuanclAndWaigj();
								wgjnew.setBanbenhao(waiBanbenNumber);
								wgjnew.setStatus("使用");
								wgjnew.setPricestatus("新增");
								wgjnew.setBanbenStatus("默认");
								wgjnew.setAddTime(time);
								wgjnew.setAddUserCode(user.getCode());
								wgjnew.setAddUserName(user.getName());
								wgjnew.setMarkId(ptbb.getNewmarkId());// 件号
								wgjnew.setName(pt.getProName());// 名称
								wgjnew.setSpecification(pt.getSpecification());// 规格
								wgjnew.setUnit(pt.getUnit());// BOM用的单位
								wgjnew.setCkUnit(null);// 仓库用的单位
								wgjnew.setZcMarkId(pt.getYwMarkId());// 总成号
								wgjnew.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
								wgjnew.setCaizhi(pt.getCaizhi());// 材质类型
								wgjnew.setTuhao(pt.getTuhao());// 图号
								wgjnew.setWgType(pt.getWgType());
								wgjnew.setKgliao(pt.getKgliao());
								wgjnew.setProductStyle(pt.getProductStyle());
								wgjnew.setPricestatus("新增");
								wgjnew.setBanbenStatus("默认");
								wgjnew.setBili(pt.getBili());
								wgjnew.setAddTime(time);
								wgjnew.setAddUserCode(user.getCode());
								wgjnew.setAddUserName(user.getName());
								wgjnew.setImportance(pt.getImportance());
								wgjnew.setCgperiod(pt.getCgperiod());
								wgjnew.setAvgProductionTakt(pt
										.getAvgProductionTakt());// 生产节拍(s)
								wgjnew.setAvgDeliveryTime(pt
										.getAvgDeliveryTime());// 配送时长(d)
								totalDao.save(wgjnew);
							}
						}
					}

					String historySql = "from ProcardTemplate where markId=? and productStyle=? and banbenStatus='历史' and (dataStatus is null or dataStatus!='删除')";
					if (pt.getBanci() == null || pt.getBanci() == 0) {
						historySql = historySql
								+ " and (banci is null or banci=0)";
					} else {
						historySql = historySql + " and banci='"
								+ pt.getBanci() + "'";
					}
					ProcardTemplate history = (ProcardTemplate) totalDao
							.getObjectByCondition(historySql, pt.getMarkId(),
									pt.getProductStyle());
					String banciSql = null;
					if (pt.getBanci() == null || pt.getBanci() == 0) {
						banciSql = " and ( banci is null or banci=0)";
					} else {
						banciSql = " and banci=" + pt.getBanci();
					}
					if (history == null) {// 此版本还没有历史，添加历史
						history = new ProcardTemplate();
						BeanUtils.copyProperties(pt, history, new String[] {
								"id", "procardTemplate", "procardTSet",
								"processTemplate", "zhUsers" });
						history.setBanbenStatus("历史");

						// 同步工序
						Set<ProcessTemplate> processSet1 = pt
								.getProcessTemplate();
						Set<ProcessTemplate> processSet2 = new HashSet<ProcessTemplate>();
						if (processSet1 != null && processSet1.size() > 0) {// 添加在添加列表中存在的工序号的工序
							for (ProcessTemplate process1 : processSet1) {
								if (process1.getProcessNO() != null) {
									ProcessTemplate process2 = new ProcessTemplate();
									// -----------辅料开始----------
									if (process1.getIsNeedFuliao() != null
											&& process1.getIsNeedFuliao()
													.equals("yes")) {
										Set<ProcessFuLiaoTemplate> fltmpSet = process1
												.getProcessFuLiaoTemplate();
										if (fltmpSet.size() > 0) {
											Set<ProcessFuLiaoTemplate> pinfoFlSet = new HashSet<ProcessFuLiaoTemplate>();
											for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
												ProcessFuLiaoTemplate pinfoFl = new ProcessFuLiaoTemplate();
												BeanUtils
														.copyProperties(
																fltmp,
																pinfoFl,
																new String[] {
																		"id",
																		"processTemplate" });
												if (pinfoFl.getQuanzhi1() == null) {
													pinfoFl.setQuanzhi1(1f);
												}
												if (pinfoFl.getQuanzhi2() == null) {
													pinfoFl.setQuanzhi2(1f);
												}
												pinfoFl
														.setProcessTemplate(process2);
												pinfoFlSet.add(pinfoFl);
											}
											process2
													.setProcessFuLiaoTemplate(pinfoFlSet);
										}
									}
									// -----------辅料结束----------
									BeanUtils.copyProperties(process1,
											process2, new String[] { "id",
													"procardTemplate",
													"taSopGongwei",
													"processFuLiaoTemplate" });
									process2.setProcardTemplate(history);
									totalDao.save(process2);
									if (productStyle.equals("试制")) {// 复制出一份图纸给历史模板工序
										String tzSql = "from ProcessTemplateFile where glId=? and processNO is not null "
												+ banciSql;
										List<ProcessTemplateFile> processFileList = totalDao
												.query(tzSql, process2.getId());
										if (processFileList != null
												&& processFileList.size() > 0) {
											for (ProcessTemplateFile processFile : processFileList) {
												if (processFile.getBanci() == null) {
													processFile.setBanci(0);
												}
												ProcessTemplateFile newfile = new ProcessTemplateFile();
												BeanUtils.copyProperties(
														processFile, newfile,
														new String[] { "id" });
												newfile.setBzStatus("初始");
												newfile.setStatus("默认");
												newfile.setGlId(process2
														.getId());
												newfile.setProductStyle("试制");
												totalDao.save(newfile);
											}
										}
									}
									processSet2.add(process2);
								}
							}
						}
						history.setProcessTemplate(processSet2);
						totalDao.save(history);
						if (productStyle.equals("试制")) {// 复制出一份图纸给历史模板
							String tzSql = "from ProcessTemplateFile where glId=? and processNO is  null "
									+ banciSql;
							List<ProcessTemplateFile> processFileList = totalDao
									.query(tzSql, pt.getId());
							if (processFileList != null
									&& processFileList.size() > 0) {
								for (ProcessTemplateFile processFile : processFileList) {
									if (processFile.getBanci() == null) {
										processFile.setBanci(0);
									}
									ProcessTemplateFile newfile = new ProcessTemplateFile();
									BeanUtils.copyProperties(processFile,
											newfile, new String[] { "id" });
									newfile.setBzStatus("初始");
									newfile.setStatus("默认");
									newfile.setGlId(history.getId());
									newfile.setProductStyle("试制");
									totalDao.save(newfile);
								}
							}
						}
					}
					if (productStyle.equals("试制")
							&& (!ptbb.getUptype().equals("件号替换") || (ptbb
									.getUptype().equals("件号替换") && newProcardtemplate == null))) {
						// 复制一份图纸进行升级版次,如果是件号替换并且新件号为系统已有零件就不用生成了，直接在待会重新添加零件的时候替换就可以了
						String tzSql = "from ProcessTemplateFile where (glId=? and processNO is  null "
								+ banciSql
								+ ") or "
								+ "(processNO is not null "
								+ banciSql
								+ " and glId in(select id from ProcessTemplate where procardTemplate.id=? and (dataStatus is null or dataStatus !='删除')))";
						List<ProcessTemplateFile> processFileList = totalDao
								.query(tzSql, pt.getId(), pt.getId());
						if (processFileList != null
								&& processFileList.size() > 0) {
							for (ProcessTemplateFile processFile : processFileList) {
								if (processFile.getBanci() == null) {
									processFile.setBanci(0);
								}
								ProcessTemplateFile newfile = new ProcessTemplateFile();
								BeanUtils.copyProperties(processFile, newfile,
										new String[] { "id" });
								if (ptbb.getUptype().equals("件号替换")) {
									newfile.setMarkId(ptbb.getNewmarkId());
								}
								newfile.setBanci(processFile.getBanci() + 1);
								newfile.setAddTime(time);
								if (ptbb.getUptype().equals("版本升级")) {
									newfile.setBanBenNumber(ptbb
											.getNewBanBenNumber());
								} else {
									newfile.setBanBenNumber(ptbb
											.getBanBenNumber());
								}
								totalDao.save(newfile);
							}
						}
					}
					if (ptbb.getUptype().equals("件号替换")
							&& newProcardtemplate != null) {
						ProcardTemplate fatehr = pt.getProcardTemplate();
						if (fatehr != null) {
							pt.setHwdrMarkId(ptbb.getNewmarkId());
							ProcardTemplateChangeLog.addchangeLog(totalDao,
									fatehr, pt, "子件", "件号替换", user, time);
							Set<ProcessTemplate> processSet = pt
									.getProcessTemplate();
							if (processSet != null && processSet.size() > 0) {
								for (ProcessTemplate process : processSet) {
									process.setDataStatus("删除");
									process.setProcardTemplate(null);
									process.setOldPtId(pt.getId());
									process.setOldbanci(pt.getBanci());
									process.setDeleteTime(Util.getDateTime());
									totalDao.update(process);
								}
							}
							pt.setDataStatus("删除");
							pt.setProcardTemplate(null);
							totalDao.update(pt);
							tbDownDataStatus(pt);
							Float q1 = newProcardtemplate.getQuanzi1();
							Float q2 = newProcardtemplate.getQuanzi1();
							Float c = newProcardtemplate.getQuanzi1();
							newProcardtemplate.setQuanzi1(pt.getQuanzi1());
							newProcardtemplate.setQuanzi2(pt.getQuanzi2());
							newProcardtemplate.setCorrCount(pt.getCorrCount());
							saveCopyProcard(fatehr, newProcardtemplate,
									productStyle);
							newProcardtemplate.setQuanzi1(q1);
							newProcardtemplate.setQuanzi2(q2);
							newProcardtemplate.setCorrCount(c);
						}
					} else {
						if (ptbb.getUptype().equals("件号替换")) {
							pt.setMarkId(ptbb.getNewmarkId());
						}
						pt.setChangeProcess(ptbb.getChangeProcess());
						pt.setChangeTz(ptbb.getChangeTz());
						if (ptbb.getBianzhiId() != null) {
							pt.setBianzhiId(ptbb.getBianzhiId());
							pt.setBianzhiName(ptbb.getBianzhiName());
						} else {
							pt.setBianzhiId(user.getId());
							pt.setBianzhiName(user.getName());
						}
						pt.setJiaoduiId(ptbb.getJiaoduiId());
						pt.setJiaoduiName(ptbb.getJiaoduiName());
						pt.setShenheId(ptbb.getShenheId());
						pt.setShenheName(ptbb.getShenheName());
						pt.setPizhunId(ptbb.getPizhunId());
						pt.setPizhunName(ptbb.getPizhunName());
						pt.setHasChange("否");
						if (pt.getBanci() == null) {
							pt.setBanci(1);
						} else {
							pt.setBanci(pt.getBanci() + 1);
						}
						if (ptbb.getUptype().equals("版本升级")) {
							pt.setBanBenNumber(ptbb.getNewBanBenNumber());
						} else {
							pt.setBanBenNumber(ptbb.getBanBenNumber());
						}
						Set<ProcardTemplate> sonSet = pt.getProcardTSet();
						if (sonSet != null && sonSet.size() > 0) {
							for (ProcardTemplate son : sonSet) {
								son.setHasChange("否");
								totalDao.update(son);
							}
						}
					}
					pt.setBzStatus("待编制");
					totalDao.update(pt);

				}
			}
		}
		return null;
	}

	@Override
	public String tbProcard(ProcardTemplateBanBenApply bbAply,
			ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		String sbtbsql = "select valueCode from CodeTranslation where type = 'sys' and keyCode='设变完全同步'";
		String sbwqtb = (String) totalDao.getObjectByCondition(sbtbsql);
		// sbwqtb="是";
		Map<String, String> markidMap = new HashMap<String, String>();
		markidMap.put("1.01.30017", "1.01.30022");
		markidMap.put("1.01.30005", "1.01.30011");
		markidMap.put("1.01.30012", "1.01.30007");
		markidMap.put("1.01.10025", "1.01.10055");
		markidMap.put("1.01.10021", "1.01.10040");
		markidMap.put("1.01.10028", "1.01.10043");
		markidMap.put("1.01.10027", "1.01.10042");
		markidMap.put("1.01.10026", "1.01.10062");
		markidMap.put("1.01.10030", "1.01.12501");
		markidMap.put("1.01.10029", "1.01.12537");
		markidMap.put("1.01.30018", "1.01.30014");
		markidMap.put("1.01.30026", "1.01.30016");
		markidMap.put("1.01.30034", "1.01.30010");
		markidMap.put("2.09.00014", "30100005");
		markidMap.put("DKBA8.840.0069", "30040054");
		markidMap.put("QDKBA4.409.0670", "26020187");
		markidMap.put("1.04.00560", "28010212");
		markidMap.put("DKBA3360.1", "30060106");
		markidMap.put("1.01.30002", "1.01.30013");
		Users user = Util.getLoginUser();
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		List<String> sbmarkIds = totalDao
				.query(
						" select DISTINCT markId from ProcardTemplateAboutsb where sbApplyId=?",
						bbAply.getId());
		if (sbmarkIds == null) {
			sbmarkIds = new ArrayList<String>();
		}
		if (sbmarkIds.size() == 0) {
			List<String> sbmarkIds2 = totalDao
					.query(
							"select DISTINCT entityData from ProcardTemplateChangeLog where entityType='子件' and "
									+ "ptbbId in(select id from ProcardTemplateBanBen where procardTemplateBanBenApply.id=? )",
							ptbbApply.getId());
			List<String> sbmarkIds3 = totalDao
					.query(
							"select DISTINCT sbMarkId from ProcardTemplateChangeLog where entityType='子件' and "
									+ "ptbbId in(select id from ProcardTemplateBanBen where procardTemplateBanBenApply.id=? )",
							ptbbApply.getId());
			sbmarkIds.addAll(sbmarkIds2);
			sbmarkIds.addAll(sbmarkIds3);
		}
		// List<String> sbmarkIds = new ArrayList<String>();
		
//		 sbmarkIds.add("1.03.10050");
//		 
//		 sbmarkIds.add("1.03.10667");
		List<String> zhMarkIdlist = new ArrayList<String>();
		if (sbmarkIds != null && sbmarkIds.size() > 0) {
			for (String sbmark : sbmarkIds) {
				String zhMarkId = markidMap.get(sbmark);
				if (zhMarkId != null) {
					zhMarkIdlist.add(zhMarkId);
				}
			}
		}
		sbmarkIds.addAll(zhMarkIdlist);
		String nowTime = Util.getDateTime();
		if (ptbbApply == null) {
			return "没有找到对应的设变申请";
		}
		if (ptbbApply.getTbProcard() != null
				&& ptbbApply.getTbProcard().equals("完成")) {
			return "已同步过，请勿重复同步!";
		}
		if (!ptbbApply.getProcessStatus().equals("关联并通知生产")) {
			return "当前状态为" + ptbbApply.getProcessStatus() + ",不能关联并通知生产!";
		}
		Users login = Util.getLoginUser();
		if (login == null) {
			return "请先登录!";
		}
		ptbbApply.setNeedzz(bbAply.getNeedzz());
		Set<Integer> syIdList = new HashSet<Integer>();// 记录顺延的procardId
		Set<Integer> nsyIdList = new HashSet<Integer>();// 记录不顺延的procardId
		// 添加佐证人员
		if (bbAply.getNeedzz().equals("是")) {
			if (ptbbJudges.getSelectUserId3() != null
					&& ptbbJudges.getSelectUserId3().length() > 0) {
				Integer[] userId = null;
				String[] checkboxs = ptbbJudges.getSelectUserId3().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				// if (had.getId().equals(ptbbJudges.getId())) {
				// had.setSelectUserId(ptbbJudges.getSelectUserId());
				// had.setSelectUsers(ptbbJudges.getSelectUsers());
				// }
				// }
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("佐证")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(Util.getDateTime());// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("佐证");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						// 发送消息
						j++;
					}
				}
				AlertMessagesServerImpl.addAlertMessages("BOM设变佐证通知", user
						.getName()
						+ "对总成为:"
						+ ptbbApply.getMarkId()
						+ ",业务件号为："
						+ ptbbApply.getYwMarkId()
						+ "的BOM设变发起佐证上传指派,请您及时前往上传设变佐证 !设变单号:"
						+ ptbbApply.getSbNumber(), userId,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
				ptbbApply.setPtbbjset(ptbbjSet);
				// ptbbApply.setLastop("指派佐证人员");
				// ptbbApply.setLastUserId(user.getId());
				// ptbbApply.setLastUserName(user.getName());
				// ptbbApply.setLastTime(Util.getDateTime());
				// totalDao.update(ptbbApply);
			} else {
				return "请选择佐证人员!";
			}
		} else {
			if (bbAply.getZzremark() == null
					|| bbAply.getZzremark().length() == 0) {
				return "如果不需要上传佐证请填写理由!";
			}
			ptbbApply.setZzremark(bbAply.getZzremark());
		}
		ptbbApply.setLastop("同步生产");
		ptbbApply.setLastUserId(user.getId());
		ptbbApply.setLastUserName(user.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		Set<ProcardTemplateBanBen> ptbbSet = ptbbApply
				.getProcardTemplateBanBen();
		if (ptbbSet != null && ptbbSet.size() > 0) {
			for (ProcardTemplateBanBen ptbb : ptbbSet) {
				// 获取对应零件处理方案
				List<ProcardAboutBanBenApply> procardAboutBanBenApplyList = totalDao
						.query("from ProcardAboutBanBenApply where bbId=?",
								ptbb.getId());
				if (procardAboutBanBenApplyList != null
						&& procardAboutBanBenApplyList.size() > 0) {
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, ptbb
									.getPtId());
					if (pt.getDataStatus() != null
							&& pt.getDataStatus().equals("删除")) {// 判定是否删除
						if (!pt.getProcardStyle().equals("总成")) {// 去查询之前的上层是否有与之相同件号的零件,如果有作为新的模板
							ProcardTemplate fpt = (ProcardTemplate) totalDao
									.getObjectById(ProcardTemplate.class, pt
											.getFatherId());
							if (fpt != null
									&& (fpt.getDataStatus() == null || !fpt
											.getDataStatus().equals("删除"))) {
								ProcardTemplate pt2 = (ProcardTemplate) totalDao
										.getObjectByCondition(
												"from ProcardTemplate where markId=? and procardTemplate.id=? and (banbenStatus is null or banbenStatus !='历史')"
														+ "and (dataStatus is null or dataStatus !='删除')",
												pt.getMarkId(), fpt.getId());
								if (pt2 != null) {
									pt = pt2;
								}
							}
						}
					}
					for (ProcardAboutBanBenApply pAbb : procardAboutBanBenApplyList) {
						if (pAbb.getStatus() == null
								|| !pAbb.getStatus().equals("完成")) {
							// 获取对应生产件rootid
							Procard procard = (Procard) totalDao.getObjectById(
									Procard.class, pAbb.getProcardId());
							if (procard != null) {
								if (procard.getSbStatus() != null
										&& procard.getSbStatus().equals("删除")) {
									pAbb.setStatus("完成");// 已经被上层连带处理过了
									totalDao.update(pAbb);
									continue;
								}
								if (pt != null) {
									if (pt.getDataStatus() != null
											&& pt.getDataStatus().equals("删除")) {// 对应的模板已经被记录删除
										Set<ProcessAboutBanBenApply> processabbSet = pAbb
												.getProcessabbSet();
										if (processabbSet != null
												&& processabbSet.size() > 0) {
											String clType = "";
											for (ProcessAboutBanBenApply processabb : processabbSet) {
												if (processabb.getClType() == null
														|| processabb
																.getClType()
																.length() == 0) {
													throw new RuntimeException(
															"件号"
																	+ ptbbApply
																			.getMarkId()
																	+ "第"
																	+ pAbb
																			.getSelfCard()
																	+ "批次处理方案异常!");
												}
												if (clType.equals("")) {
													clType = processabb
															.getClType();
												} else {
													if ((clType.equals("顺延") && !processabb
															.getClType()
															.equals("顺延"))
															|| (!clType
																	.equals("顺延") && processabb
																	.getClType()
																	.equals(
																			"顺延"))) {
														clType = "处理方案异常";
														break;
													}
												}
											}
											if (clType.equals("处理方案异常")) {
												throw new RuntimeException("件号"
														+ ptbbApply.getMarkId()
														+ "第"
														+ pAbb.getSelfCard()
														+ "批次处理方案异常!");
											} else if (clType.equals("顺延")) {
												syIdList.add(pAbb
														.getProcardId());
												pAbb.setStatus("完成");
												totalDao.update(pAbb);
												continue;
											} else {// 非顺延,因为模板已经删除则对应的生产件也为删除
												nsyIdList.add(pAbb
														.getProcardId());
												if (ptbb.getUptype().equals(
														"件号替换")) {
													// 生成替代的物料
													Procard father = procard
															.getProcard();
													ProcardTemplate thPt = (ProcardTemplate) totalDao
															.getObjectByCondition(
																	"from ProcardTemplate where markId=? and procardTemplate.id=? and (banbenStatus is null or banbenStatus !='历史')"
																			+ "and (dataStatus is null or dataStatus !='历史')",
																	ptbb
																			.getNewmarkId(),
																	pt
																			.getFatherId());

													if (thPt != null) {
														Procard thp = addProcardInFather(
																bbAply.getId(),
																ptbbApply
																		.getSbNumber(),
																father,
																procard, thPt,
																"", 1);
														if (thPt
																.getProcardStyle()
																.equals("外购")
																&& (thPt
																		.getLingliaostatus() == null || !thPt
																		.getLingliaostatus()
																		.equals(
																				"否"))) {
															List<ProcardBl> pblList2 = totalDao
																	.query(
																			"from ProcardBl where procardId=? and ylCount>0",
																			father
																					.getId());
															if (pblList2 != null
																	&& pblList2
																			.size() > 0) {
																for (ProcardBl pbl : pblList2) {
																	pbl
																			.setYlCount(0f);
																	pbl
																			.setStatus("未领完");
																	totalDao
																			.update(pbl);
																}
															}
															father
																	.setHascount(father
																			.getKlNumber());
														}
														if (thp != null) {
															if (procard
																	.getWwblCount() != null
																	&& procard
																			.getWwblCount() > 0) {
																updateWwp(
																		procard,
																		thp);
															}
														}
													}
												}
												float scgxCount = 0f;// 上层工序已领数量
												if ((procard.getSbStatus() == null || !procard
														.getSbStatus().equals(
																"删除"))
														&& !procard.getStatus()
																.equals("已发料")
														&& !procard.getStatus()
																.equals("领工序")
														&& !procard.getStatus()
																.equals("完成")) {
													// 添加报废数据
													List<ProcessInfor> processList = totalDao
															.query(
																	"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除') order by processNO",
																	procard
																			.getId());
													for (int j = 0; j < processList
															.size(); j++) {
														float zzCount = 0f;
														ProcessInfor process = processList
																.get(j);
														if (j == 0) {
															scgxCount = process
																	.getApplyCount();
														}
														if (process
																.getApplyCount() == 0) {
															continue;
														}
														// 此工序已领单未提交数量
														zzCount += process
																.getApplyCount()
																- process
																		.getSubmmitCount();
														if ((j + 1) <= (processList
																.size() - 1)) {
															ProcessInfor process2 = processList
																	.get((j + 1));
															// 此道工序做完未流转到下道工序的数量
															zzCount += process
																	.getSubmmitCount()
																	- process2
																			.getApplyCount();
															if (zzCount < 0) {
																zzCount = 0;
															}
														} else {
															zzCount += process
																	.getSubmmitCount();
														}
														if (zzCount > 0) {// 查询此工序当前的半成品数量
															if (zzCount > 0) {
																// 生成报废待处理在制品
																addProcardSbWaster(
																		bbAply
																				.getId(),
																		ptbbApply
																				.getSbNumber(),
																		procard,
																		zzCount,
																		nowTime,
																		process
																				.getProcessNO(),
																		process
																				.getProcessName(),
																		clType);
																sbData++;
															}
														}
													}

												}
												procard.setSbId(bbAply.getId());
												procard.setSbNumber(ptbbApply
														.getSbNumber());
												procard.setSbStatus("删除");
												procard.setProcard(null);
												if (procard.getFatherId() != null) {
													procard
															.setOldFatherId(procard
																	.getFatherId());
													procard
															.setOldRootId(procard
																	.getRootId());
												}
												procard.setFatherId(null);
												procard.setRootId(null);
												procard.setHasChange("是");
												if (procard.getProcardStyle()
														.equals("外购")) {
													// 处理外购订单
													addProcardSbWg(
															bbAply.getId(),
															ptbbApply
																	.getSbNumber(),
															procard,
															-procard
																	.getFilnalCount(),
															nowTime);
													sbData++;
													// 查询是否有在外的委外
													if (procard.getHascount() != null
															&& procard
																	.getKlNumber() != null
															&& procard
																	.getHascount() < procard
																	.getKlNumber()) {
														// 有过领料
														if (procard
																.getWwblCount() != null
																&& procard
																		.getWwblCount() > 0) {
															// 向上层递归找到对应的外委零件
															// 外委设变明细(原由)
															ProcardSbWwDetail psbwwd = addProcardSbWwDetail(procard);// 设变类型（新加,删除,用量调整）
															String msg = breakWWBysb(
																	procard,
																	bbAply
																			.getId(),
																	ptbbApply
																			.getSbNumber(),
																	psbwwd);
														}
													}

												}
												// 遍历下层零件设置设变属性为删除
												Set<Procard> sonSet = procard
														.getProcardSet();
												if (sonSet != null
														&& sonSet.size() > 0) {
													for (Procard son : sonSet) {
														deleteDownProcard(
																bbAply.getId(),
																ptbbApply
																		.getSbNumber(),
																son, nowTime,
																scgxCount,
																clType);
													}
												}
												totalDao.update(procard);
											}
										}
									} else {
										Set<ProcessAboutBanBenApply> processabbSet = pAbb
												.getProcessabbSet();
										String clType = "";
										if (processabbSet != null
												&& processabbSet.size() > 0) {
											for (ProcessAboutBanBenApply processabb : processabbSet) {
												if (processabb.getClType() == null
														|| processabb
																.getClType()
																.length() == 0) {
													throw new RuntimeException(
															"件号"
																	+ ptbbApply
																			.getMarkId()
																	+ "第"
																	+ pAbb
																			.getSelfCard()
																	+ "批次处理方案异常!");
												}
												if (clType.equals("")) {
													clType = processabb
															.getClType();
												} else {
													if ((clType.equals("顺延") && !processabb
															.getClType()
															.equals("顺延"))
															|| (!clType
																	.equals("顺延") && processabb
																	.getClType()
																	.equals(
																			"顺延"))) {
														clType = "处理方案异常";
														break;
													}
												}
											}
											if (clType.equals("处理方案异常")) {
												throw new RuntimeException("件号"
														+ ptbbApply.getMarkId()
														+ "第"
														+ pAbb.getSelfCard()
														+ "批次处理方案异常!");
											}

											if (clType.equals("顺延")) {
												syIdList.add(pAbb
														.getProcardId());
											} else {
												nsyIdList.add(pAbb
														.getProcardId());
											}
											if (clType.equals("报废重产")
													&& procard.getProcard() != null) {
												Float fCount = procard
														.getFilnalCount();
												Procard father = procard
														.getProcard();
												// ProcessInfor processinfor =
												// (ProcessInfor) totalDao
												// .getObjectByCondition(
												// "from ProcessInfor where procard.id=?"
												// +
												// " and (dataStatus is null or dataStatus !='删除') order by processNO desc",
												// procard.getId());
												// List<ProcessInfor>processinforList
												// = totalDao
												// .query(
												// "from ProcessInfor where procard.id=?"
												// +
												// " and (dataStatus is null or dataStatus !='删除') order by processNO aesc",
												// procard.getId());
												for (ProcessAboutBanBenApply processabb : processabbSet) {
													// 报废之前的
													// 生成报废待处理在制品
													addProcardSbWaster(
															bbAply.getId(),
															ptbbApply
																	.getSbNumber(),
															procard,
															processabb
																	.getScCount(),
															nowTime,
															processabb
																	.getProcessNo(),
															processabb
																	.getProcessName(),
															clType);
												}
												// 删除零件
												procard.setSbId(bbAply.getId());
												procard.setSbNumber(ptbbApply
														.getSbNumber());
												procard.setSbStatus("删除");
												procard.setProcard(null);
												if (procard.getFatherId() != null) {
													procard
															.setOldFatherId(procard
																	.getFatherId());
													procard
															.setOldRootId(procard
																	.getRootId());
												}
												procard.setFatherId(null);
												procard.setRootId(null);
												procard.setHasChange("是");
												if (procard.getProcardStyle()
														.equals("外购")) {
													// 处理外购订单
													addProcardSbWg(
															bbAply.getId(),
															ptbbApply
																	.getSbNumber(),
															procard,
															-procard
																	.getFilnalCount(),
															nowTime);
													sbData++;
													// 查询是否有在外的委外
													if (procard.getHascount() != null
															&& procard
																	.getKlNumber() != null
															&& procard
																	.getHascount() < procard
																	.getKlNumber()) {
														// 有过领料
														if (procard
																.getWwblCount() != null
																&& procard
																		.getWwblCount() > 0) {
															ProcardSbWwDetail psbwwd = addProcardSbWwDetail(procard);
															String msg = breakWWBysb(
																	procard,
																	bbAply
																			.getId(),
																	ptbbApply
																			.getSbNumber(),
																	psbwwd);
														}
													}
												}
												// 遍历下层零件设置设变属性为删除
												Set<Procard> sonSet = procard
														.getProcardSet();
												if (sonSet != null
														&& sonSet.size() > 0) {
													for (Procard son : sonSet) {
														deleteDownProcard(
																bbAply.getId(),
																ptbbApply
																		.getSbNumber(),
																son,
																nowTime,
																procard
																		.getFilnalCount(),
																clType);
													}
												}
												// 生成新的
												addProcardInFather(bbAply
														.getId(), ptbbApply
														.getSbNumber(), father,
														procard, pt, "", 1);
												if (pt.getProcardStyle()
														.equals("外购")
														&& (pt
																.getLingliaostatus() == null || !pt
																.getLingliaostatus()
																.equals("否"))) {
													procard.setHascount(procard
															.getKlNumber());
													List<ProcardBl> pblList2 = totalDao
															.query(
																	"from ProcardBl where procardId=? and ylCount>0",
																	procard
																			.getId());
													if (pblList2 != null
															&& pblList2.size() > 0) {
														for (ProcardBl pbl : pblList2) {
															pbl.setYlCount(0f);
															pbl
																	.setStatus("未领完");
															totalDao
																	.update(pbl);
														}
													}
												}
												totalDao.update(procard);
												pAbb.setStatus("完成");
												totalDao.update(pAbb);
												continue;
											}

										}
										Integer rootId = procard.getRootId() == null ? procard
												.getOldRootId()
												: procard.getRootId();
										Integer fatherId = procard
												.getFatherId() == null ? procard
												.getOldFatherId()
												: procard.getFatherId();
										String totalWlStatus = (String) totalDao
												.getObjectByCondition(
														"select wlstatus from Procard where id=?",
														rootId);
										Float scFinalCount = null;
										if (procard.getFatherId() != null) {
											scFinalCount = (Float) totalDao
													.getObjectByCondition(
															"select filnalCount from Procard where id=?",
															fatherId);
										}
										boolean changeyl = false;
										if (procard.getProcardTemplateId() != null) {
											if (procard.getProcardTemplateId()
													.equals(pt.getId())) {
												changeyl = true;
											} else {
												ProcardTemplate mbPt = (ProcardTemplate) totalDao
														.getObjectById(
																ProcardTemplate.class,
																procard
																		.getProcardTemplateId());
												if (mbPt != null) {
													if (mbPt.getProcardStyle()
															.equals("外购")) {
														if (!isEquals(
																mbPt
																		.getQuanzi1(),
																procard
																		.getQuanzi1())
																|| !isEquals(
																		mbPt
																				.getQuanzi2(),
																		procard
																				.getQuanzi2())) {
															pt = mbPt;
															changeyl = true;
														}
													} else {
														if (!isEquals(
																mbPt
																		.getCorrCount(),
																procard
																		.getCorrCount())) {
															pt = mbPt;
															changeyl = true;
														}
													}
												}
											}
										} else {
											if (pt.getProcardTemplate() != null
													&& procard.getProcard() != null
													&& pt
															.getProcardTemplate()
															.getMarkId()
															.equals(
																	procard
																			.getProcard()
																			.getMarkId())) {// 上层同件号修改用量
												changeyl = true;
											}
										}
										updateProcard(bbAply.getId(), ptbbApply
												.getSbNumber(), pt, procard,
												scFinalCount, 0f,
												totalWlStatus, pAbb,
												procardAboutBanBenApplyList,
												nowTime, sbmarkIds, user,
												changeyl, sbwqtb, markidMap,
												syIdList, nsyIdList);

									}
								} else {
									// 已改为状态删除此状态为非正常数据不考虑
								}
								pAbb.setStatus("完成");
								totalDao.update(pAbb);
							}
						}
					}
				}
			}
		}
		// 获取要通知的人员并发送通知
		Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply.getPtbbjset();
		if (ptbbjSet != null && ptbbjSet.size() > 0) {
			Integer[] userId = new Integer[ptbbjSet.size()];
			Integer[] userId2 = new Integer[ptbbjSet.size()];
			int tzi = 0;
			int tzi2 = 0;
			for (ProcardTemplateBanBenJudges ptbbj : ptbbjSet) {
				if (ptbbj.getJudgeType().equals("编制完通知")) {
					userId[tzi] = ptbbj.getUserId();
					tzi++;
				}
				if (ptbbj.getJudgeType().equals("佐证")) {
					userId2[tzi] = ptbbj.getUserId();
					tzi2++;
				}
			}
			AlertMessagesServerImpl.addAlertMessages("BOM设变编制完成通知", "总成为:"
					+ ptbbApply.getMarkId() + ",业务件号为："
					+ ptbbApply.getYwMarkId() + "的BOM设变编制完成，请前去查看设变明细。设变单号："
					+ ptbbApply.getSbNumber(), userId,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ ptbbApply.getId(), true, "no");
			AlertMessagesServerImpl.addAlertMessages("BOM佐证通知", "总成为:"
					+ ptbbApply.getMarkId() + ",业务件号为："
					+ ptbbApply.getYwMarkId() + "的BOM设变,已同步生产请前往查看并上传佐证!。设变单号："
					+ ptbbApply.getSbNumber(), userId2,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ ptbbApply.getId(), true, "no");
		}
		// if (sbData > 0) {
		// ptbbApply.setProcessStatus("生产后续");
		// } else {
		// ptbbApply.setProcessStatus("关闭");
		// }
		ptbbApply.setProcessStatus("上传佐证");
		// 查询所有关联影响的BOM下有生产件未激活的的总成 设置为待定状态并获取通知数据
		List<Procard> todjProcardList = totalDao
				.query(
						"from Procard where id in(select rootId  from Procard where sbId=? and (procardStyle in('自制','总成') or (procardStyle='外购' and needProcess='yes')) and status='初始' and (sbStatus is null or sbStatus!='删除')) and wlstatus!='待定' ",
						ptbbApply.getId());
		if (todjProcardList != null && todjProcardList.size() > 0) {
			Map<Integer, StringBuffer> jhtzmap = new HashMap<Integer, StringBuffer>();// 激活通知Map
			// key
			// 激活人Id,vaule
			// rootMarkId
			// selfCard
			// ywMarkId
			for (Procard todjProcard : todjProcardList) {
				todjProcard.setWlstatus("待定");
				StringBuffer tzMsgsb = jhtzmap.get(todjProcard.getZhikarenId());
				if (tzMsgsb == null) {
					tzMsgsb = new StringBuffer();
					tzMsgsb.append(todjProcard.getMarkId() + "("
							+ todjProcard.getYwMarkId() + ")" + "第"
							+ todjProcard.getSelfCard() + "批次");
				} else {
					tzMsgsb.append("," + todjProcard.getMarkId() + "("
							+ todjProcard.getYwMarkId() + ")" + "第"
							+ todjProcard.getSelfCard() + "批次");
				}
			}
			Set<Integer> keySet = jhtzmap.keySet();
			if (keySet != null && keySet.size() > 0) {
				for (Integer key : keySet) {
					StringBuffer tzMsgsb = jhtzmap.get(key);
					AlertMessagesServerImpl.addAlertMessages("设变待激活通知", tzMsgsb
							.toString()
							+ "的生产任务因设变需重新激活，请前往判定委外并激活。",
							new Integer[] { key },
							"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
									+ ptbbApply.getId(), true, "no");
				}
			}
		}
		List<Integer> zzrootIdList = totalDao
		.query(
				"select distinct rootId from Procard where sbId=? and procardStyle  in('自制','总成') and status='初始' and (sbStatus is null or sbStatus!='删除')  and rootId in(select id from Procard where procardStyle ='总成' and wlstatus!='待定')  ",
				ptbbApply.getId());
		// 查询所有关联影响的BOM下有未激活的外购件，将外购件激活
		List<Integer> rootIdList = totalDao
				.query(
						"select distinct rootId from Procard where sbId=? and procardStyle ='外购' and (needProcess is null or needProcess!='yes') and status='初始' and (sbStatus is null or sbStatus!='删除')  and rootId in(select id from Procard where procardStyle ='总成' and wlstatus!='待定')  ",
						ptbbApply.getId());
		if (rootIdList != null && rootIdList.size() > 0) {
			for (Integer jhRootId : rootIdList) {
				boolean canbgbl = false;
				if(zzrootIdList != null && zzrootIdList.size() > 0) {
					for (Integer zzjhRootId : zzrootIdList) {
						if(jhRootId.equals(zzjhRootId)){
							canbgbl =true;
							Procard totalProcard = (Procard) totalDao.getObjectById(Procard.class,
									zzjhRootId);
							if (totalProcard == null) {
								totalProcard.setWlstatus("待定");
								totalDao.update(totalProcard);
							}
						}
					}
				}
				if(!canbgbl){
					procardServer.nowwyx(jhRootId, null);
				}
			}
		}
		// if (djhProcardList != null && djhProcardList.size() > 0) {
		// Integer rootId = 0;
		// Float wwsbcount = 0f;
		// for (Procard djhProcard : djhProcardList) {
		// if (!rootId.equals(djhProcard.getRootId())) {
		// rootId = djhProcard.getRootId();
		// wwsbcount = (Float) totalDao
		// .getObjectByCondition(
		// "select count(*) from ProcardSbWw where ptbbApplyId=? "
		// +
		// "and procardId in(select id from Procard where rootId =? and (sbStatus is null or sbStatus!='删除') )",
		// ptbbApply.getId(), djhProcard.getRootId());
		// if (wwsbcount == null || wwsbcount == 0) {
		// procardServer.nowwyx(rootId);
		// }
		// }
		// }
		// }
		if (syIdList != null && syIdList.size() > 0) {
			StringBuffer sysb = new StringBuffer();
			for (Integer syId : syIdList) {
				if (sysb.length() == 0) {
					sysb.append(syId);
				} else {
					sysb.append("," + syId);
				}
			}
			if (sysb.length() > 0) {
				List<WaigouPlanLock> wpLockList = totalDao
						.query(
								"from WaigouPlanLock where sbApplyId=? and zxStatus='执行中' and dataStatus<>'取消'"
										+ "and sbProcardId in("
										+ sysb.toString() + ")", ptbbApply
										.getId());
				if (wpLockList != null && wpLockList.size() > 0) {
					for (WaigouPlanLock wpLock : wpLockList) {
						wpLock.setZxStatus("不执行");
						totalDao.update(wpLock);
						Float unpassCount = (Float) totalDao
								.getObjectByCondition(
										"select count(id) from WaigouPlanLock where  entityName=? and dataStatus<>'取消'"
												+ " and entityId=? and zxStatus='执行中'",
										wpLock.getEntityName(), wpLock
												.getEntityId());
						if (unpassCount == null || unpassCount == 0) {
							if (wpLock.getEntityName().equals("WaigouPlan")) {
								WaigouPlan wp = (WaigouPlan) totalDao
										.getObjectById(WaigouPlan.class, wpLock
												.getEntityId());
								if (wp.getStatus().equals("设变锁定")) {
									wp.setStatus(wp.getOldStatus());
									totalDao.update(wp);
								}
							} else if (wpLock.getEntityName().equals(
									"WaigouWaiweiPlan")) {
								WaigouWaiweiPlan wwp = (WaigouWaiweiPlan) totalDao
										.getObjectById(WaigouWaiweiPlan.class,
												wpLock.getEntityId());
								if (wwp.getStatus().equals("设变锁定")) {
									wwp.setStatus(wwp.getOldStatus());
									totalDao.update(wwp);
								}
							} else if (wpLock.getEntityName().equals(
									"ProcessInforWWApplyDetail")) {
								ProcessInforWWApplyDetail processww = (ProcessInforWWApplyDetail) totalDao
										.getObjectById(
												ProcessInforWWApplyDetail.class,
												wpLock.getEntityId());
								if (processww.getProcessStatus().equals("设变锁定")) {
									processww.setOldprocessStatus(processww
											.getProcessStatus());
									totalDao.update(processww);
								}
							}
						}
					}
				}

			}
		}
		if (nsyIdList != null && nsyIdList.size() > 0) {
			StringBuffer nsysb = new StringBuffer();
			for (Integer nsyId : nsyIdList) {
				if (nsysb.length() == 0) {
					nsysb.append(nsyId);
				} else {
					nsysb.append("," + nsyId);
				}
			}
			if (nsysb.length() > 0) {
				List<WaigouPlanLock> wpLockList = totalDao.query(
						"from WaigouPlanLock where sbApplyId=?"
								+ "and sbProcardId in(" + nsysb.toString()
								+ ")", ptbbApply.getId());
				if (wpLockList != null && wpLockList.size() > 0) {
					for (WaigouPlanLock wpLock : wpLockList) {
						wpLock.setZxStatus("已执行");
						totalDao.update(wpLock);
						Float unpassCount = (Float) totalDao
								.getObjectByCondition(
										"select count(id) from WaigouPlanLock where  entityName=? and dataStatus<>'取消'"
												+ " and entityId=? and zxStatus='执行中'",
										wpLock.getEntityName(), wpLock
												.getEntityId());
						if (unpassCount == null || unpassCount == 0) {
							if (wpLock.getEntityName().equals("WaigouPlan")) {
								WaigouPlan wp = (WaigouPlan) totalDao
										.getObjectById(WaigouPlan.class, wpLock
												.getEntityId());
								if (wp.getStatus().equals("设变锁定")) {
									wp.setStatus(wp.getOldStatus());
									totalDao.update(wp);
								}
							} else if (wpLock.getEntityName().equals(
									"WaigouWaiweiPlan")) {
								WaigouWaiweiPlan wwp = (WaigouWaiweiPlan) totalDao
										.getObjectById(WaigouWaiweiPlan.class,
												wpLock.getEntityId());
								if (wwp.getStatus().equals("设变锁定")) {
									wwp.setStatus(wwp.getOldStatus());
									totalDao.update(wwp);
								}
							} else if (wpLock.getEntityName().equals(
									"ProcessInforWWApplyDetail")) {
								ProcessInforWWApplyDetail processww = (ProcessInforWWApplyDetail) totalDao
										.getObjectById(
												ProcessInforWWApplyDetail.class,
												wpLock.getEntityId());
								if (processww.getProcessStatus().equals("设变锁定")) {
									processww.setProcessStatus(processww
											.getOldprocessStatus());
									totalDao.update(processww);
								}
							}
						}
					}
				}

			}
		}
		// 如果不需要上传佐证就通知关闭人员前去关闭设变
		if (ptbbApply.getNeedzz() != null && ptbbApply.getNeedzz().equals("否")) {
			List<Integer> gbUserIdList = totalDao
					.query("select userId from AssessPersonnel "
							+ "where usersGroup.groupName='设变关闭组' and usersGroup.status='sb'");
			if (gbUserIdList != null && gbUserIdList.size() > 0) {
				Integer[] gbUserIds = new Integer[gbUserIdList.size()];
				int i = 0;
				for (Integer gbUserId : gbUserIdList) {
					gbUserIds[i] = gbUserId;
					i++;
				}

				AlertMessagesServerImpl.addAlertMessages("设变待关闭通知", "总成为:"
						+ ptbbApply.getMarkId() + ",业务件号为："
						+ ptbbApply.getYwMarkId()
						+ "的BOM设变,已同步生产并选择无需佐证,请前往查看并关闭!。设变单号："
						+ ptbbApply.getSbNumber(), gbUserIds,
						"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
								+ ptbbApply.getId(), true, "no");
			}
		}

		totalDao.update(ptbbApply);
		return "同步成功!";
	}

	private ProcardSbWwDetail addProcardSbWwDetail(Procard procard) {
		ProcardSbWwDetail psbwwd = new ProcardSbWwDetail();
		psbwwd.setProcardId(procard.getId());//
		psbwwd.setMarkId(procard.getMarkId());//
		psbwwd.setBanBenNumber(procard.getBanBenNumber());// 版本号
		psbwwd.setBanci(procard.getBanci());// 版次
		psbwwd.setProName(procard.getProName());//
		psbwwd.setTuhao(procard.getTuhao());//
		psbwwd.setKgliao(procard.getKgliao());//
		psbwwd.setSbtype("删除");
		return psbwwd;
	}

	private void updateWwp(Procard procard, Procard thp) {
		thp.setWwblCount(Util.FomartFloat(procard.getWwblCount()
				* thp.getFilnalCount() / procard.getFilnalCount(), 4));
		ProcessInforWWProcard wwp = (ProcessInforWWProcard) totalDao
				.getObjectByCondition(
						"from ProcessInforWWProcard where procardId=?", procard
								.getId());
		if (wwp != null) {
			wwp.setProcardId(thp.getId());// 零件id
			wwp.setMarkId(thp.getMarkId());// 件号
			wwp.setProcName(thp.getProName());// 名称
			wwp.setBanben(thp.getBanBenNumber());// 版本号
			wwp.setBanci(thp.getBanci());// 版次
			wwp.setApplyCount(Util.FomartFloat(wwp.getApplyCount()
					* thp.getFilnalCount() / procard.getFilnalCount(), 4));// 数量
			if (wwp.getHascount() != null) {
				wwp.setHascount(Util.FomartFloat(wwp.getHascount()
						* thp.getFilnalCount() / procard.getFilnalCount(), 4));// 数量
			}
			totalDao.update(wwp);
		}
		totalDao.update(thp);
	}

	/**
	 * 外委是否
	 * 
	 * @param procard
	 * @return
	 */
	private String breakWWBysb(Procard procard, Integer ptbbApplyId,
			String sbNumber, ProcardSbWwDetail psbwwd) {
		// TODO Auto-generated method stub
		Procard fProcard = procard.getProcard();
		Users login = Util.getLoginUser();
		if (fProcard != null) {
			// 查询父零件下工序是否有BOM设定外委并被关联设变了
			List<ProcardSbWw> hadList = totalDao.query(
					"from ProcardSbWw where ptbbApplyId=? and procardId=?",
					ptbbApplyId, fProcard.getId());
			if (hadList != null && hadList.size() > 0) {
				for (ProcardSbWw had : hadList) {
					ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
					newpsbwwd.copyValue(psbwwd);
					newpsbwwd.setProcardSbWw(had);
					Set<ProcardSbWwDetail> psbwwdSet = had
							.getProcardSbWwDetailSet();
					if (psbwwdSet == null) {
						psbwwdSet = new HashSet<ProcardSbWwDetail>();
					}
					psbwwdSet.add(newpsbwwd);
					had.setProcardSbWwDetailSet(psbwwdSet);
					totalDao.update(had);
				}
			} else {
				if (!fProcard.getStatus().equals("初始")
						&& !fProcard.getStatus().equals("已发卡")) {
					// 查询父零件下工序是否有BOM设定外委
					Float count = fProcard.getFilnalCount();
					Float bomwwCount = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') and productStyle='外委'",
									fProcard.getId());
					if (bomwwCount != null && bomwwCount > 0) {
						List<ProcessInfor> processList = totalDao
								.query(
										"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') order by processNO desc",
										fProcard.getId());
						if (processList != null && processList.size() > 0) {
							Integer minProcessNo = null;
							for (ProcessInfor process : processList) {
								if (minProcessNo != null
										&& process.getProcessNO() >= minProcessNo) {// 工序已经被连带外委了
									continue;
								}
								if (process.getProductStyle().equals("外委")) {// BOM外委
									// 查询已经走到外委订单的数据
									List<WaigouPlan> wgPlanList = totalDao
											.query(
													"from WaigouPlan where wwSource='BOM外委' and  id in(select wgOrderId from ProcardWGCenter where procardId=?) and (processNOs='"
															+ process
																	.getProcessNO()
															+ "' or processNOs like '%;"
															+ process
																	.getProcessNO()
															+ "')", fProcard
															.getId());
									if (wgPlanList != null
											&& wgPlanList.size() > 0) {
										int i = 0;
										for (WaigouPlan wp : wgPlanList) {
											if (i == 0) {
												// 获得的连续外委中最小的工序号
												try {
													minProcessNo = Integer
															.parseInt(wp
																	.getProcessNOs()
																	.split(";")[0]);
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
											wp.setOldStatus(wp.getStatus());
											wp.setStatus("设变待确定");
											totalDao.update(wp);
											// 设变相关外委记录
											ProcardSbWw psbww = new ProcardSbWw();
											psbww.setStatus("待处理");
											psbww.setPtbbApplyId(ptbbApplyId);// 设变主表Id
											psbww
													.setProcardId(fProcard
															.getId());
											psbww.setSbNumber(sbNumber);// 设变单号
											psbww.setMarkId(fProcard
													.getMarkId());// 件号
											psbww.setYwmarkId(fProcard
													.getYwMarkId());// 业务件号
											psbww.setBanben(fProcard
													.getBanBenNumber());// 版本
											psbww.setProName(fProcard
													.getProName());// 零件名称
											psbww.setBanci(fProcard.getBanci());// 版次
											psbww.setTuhao(fProcard.getTuhao());// 图号
											psbww.setUnit(fProcard.getUnit());// 单位
											psbww.setWwType(wp.getWwType());// 外委类型（工序外委/包工包料、外购件/原材料）
											psbww.setWwSource(wp.getWwSource());// 外委来源（BOM外委,手动外委）
											psbww.setNumber(wp.getNumber());// 数量
											psbww.setProcessNOs(wp
													.getProcessNOs());// 工序号
											psbww.setProcessNames(wp
													.getProcessNames());// 工序名称
											psbww.setWwOrderNumber(wp
													.getWaigouOrder()
													.getPlanNumber());
											List<Integer> wwwwplanIdList = totalDao
													.query(
															"select wwxlId from ProcardWGCenter where wgOrderId=?",
															wp.getId());
											if (wwwwplanIdList != null
													&& wwwwplanIdList.size() > 0) {
												StringBuffer sb = new StringBuffer();
												for (Integer wwxlId : wwwwplanIdList) {
													if (sb.length() == 0) {
														sb.append(wwxlId);
													} else {
														sb.append("," + wwxlId);
													}
												}
												psbww.setWaigouWaiweiPlanIds(sb
														.toString());// (BOM外委才有)BOM外委计划Id
											}
											// psbww.setWwDetailId(wp.getWwDetailId());//(手动外委才有)外委申请明细Id(ProcessInforWWapplyDeatil)
											// 进入外委单才有价格
											psbww.setHsPrice(wp.getHsPrice()
													.floatValue());// 含税单价
											psbww.setPrice(wp.getPrice()
													.floatValue());// 不含税单价
											psbww.setTaxprice(wp.getTaxprice()); // 税率
											psbww.setMoney(wp.getMoney()
													.floatValue());// 总额
											psbww.setPriceId(wp.getPriceId());// 价格id
											psbww.setUserId(login.getId());// 用户id
											psbww.setUserCode(login.getCode());// 用户工号(编号)
											psbww.setGysId(wp.getGysId());// 供应商id
											psbww.setGysName(wp.getGysName());// 供应商名称
											psbww.setWaigouPlanId(wp.getId());// 委单Id
											psbww
													.setAddTime(Util
															.getDateTime());// 添加时间
											ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
											newpsbwwd.copyValue(psbwwd);
											newpsbwwd.setProcardSbWw(psbww);
											Set<ProcardSbWwDetail> psbwwdSet = new HashSet<ProcardSbWwDetail>();
											psbwwdSet.add(newpsbwwd);
											totalDao.save(psbww);
											i++;
										}
									}

									// 查看是否审查外委序列
									List<WaigouWaiweiPlan> wwpList = totalDao
											.query(
													"from WaigouWaiweiPlan where status in('待激活','待入库') and  procardId=? and (processNo='"
															+ process
																	.getProcessNO()
															+ "' or processNo like '%;"
															+ process
																	.getProcessNO()
															+ "')", fProcard
															.getId());
									if (wwpList != null && wwpList.size() > 0) {
										// 设变相关外委记录
										ProcardSbWw psbww = new ProcardSbWw();
										psbww.setStatus("待处理");
										psbww.setProcardId(fProcard.getId());
										psbww.setPtbbApplyId(ptbbApplyId);// 设变主表Id
										psbww.setSbNumber(sbNumber);// 设变单号
										psbww.setMarkId(fProcard.getMarkId());// 件号
										psbww.setYwmarkId(fProcard
												.getYwMarkId());// 业务件号
										psbww.setBanben(fProcard
												.getBanBenNumber());// 版本
										psbww.setProName(fProcard.getProName());// 零件名称
										psbww.setBanci(fProcard.getBanci());// 版次
										psbww.setTuhao(fProcard.getTuhao());// 图号
										psbww.setUnit(fProcard.getUnit());// 单位
										psbww.setWwType("工序外委");// 外委类型（工序外委/包工包料、外购件/原材料）
										psbww.setWwSource("BOM外委");// 外委来源（BOM外委,手动外委）
										psbww.setNumber(0f);// 数量
										StringBuffer ids = new StringBuffer();
										int i = 0;
										for (WaigouWaiweiPlan wwp : wwpList) {
											if (i == 0) {
												// 获得的连续外委中最小的工序号
												try {
													minProcessNo = Integer
															.parseInt(wwp
																	.getProcessNo()
																	.split(";")[0]);
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
											if (ids.length() == 0) {
												ids.append(wwp.getId());
											} else {
												ids.append("," + wwp.getId());
											}
											count = count - wwp.getBeginCount();
											psbww.setNumber(psbww.getNumber()
													+ wwp.getBeginCount());
											psbww.setProcessNOs(wwp
													.getProcessNo());// 工序号
											psbww.setProcessNames(wwp
													.getProcessName());// 工序名称
											wwp.setOldStatus(wwp.getStatus());
											wwp.setStatus("设变待确定");
											i++;
										}
										ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
										newpsbwwd.copyValue(psbwwd);
										newpsbwwd.setProcardSbWw(psbww);
										Set<ProcardSbWwDetail> psbwwdSet = new HashSet<ProcardSbWwDetail>();
										psbwwdSet.add(newpsbwwd);
										psbww
												.setProcardSbWwDetailSet(psbwwdSet);
										totalDao.save(psbww);
									}

									// psbww.setwaigouWaiweiPlan;//(BOM外委才有)BOM外委计划Id
									// psbww.setWwDetailId(wp.getWwDetailId());//(手动外委才有)外委申请明细Id(ProcessInforWWapplyDeatil)
									//						
									// //进入外委单才有价格
									// psbww.setHsPrice(wp.getHsPrice());// 含税单价
									// psbww.setPrice(wp.getPrice());// 不含税单价
									// psbww.setTaxprice(wp.getTaxprice()); //
									// 税率
									// psbww.setMoney(wp.getMoney());// 总额
									// psbww.setPriceId(wp.getPriceId());// 价格id
									// psbww.setUserId(login.getId());// 用户id
									// psbww.setUserCode(login.getCode());//
									// 用户工号(编号)
									// psbww.setGysId(wp.getGysId());// 供应商id
									// psbww.setGysName(wp.getGysName());//
									// 供应商名称
									// psbww.setWaigouPlanId(wp.getId());//委单Id
									// psbww.setAddTime(Util.getDateTime());//
									// 添加时间
									// ProcardSbWwDetail newpsbwwd=new
									// ProcardSbWwDetail();
									// newpsbwwd.copyValue(psbwwd);
									// newpsbwwd.setProcardSbWw(psbww);
									// Set<ProcardSbWwDetail> psbwwdSet = new
									// HashSet<ProcardSbWwDetail>();
									// psbwwdSet.add(newpsbwwd);
									// totalDao.save(psbww);
								}
							}
						}
					}
				}
				// 查看手动外委
				if (procard.getWwblCount() != null
						&& procard.getWwblCount() > 0) {
					if (fProcard.getWwblCount() == null
							|| fProcard.getWwblCount() == 0) {
						// 下层有包工包料上层无包工包料标记则此零件为外委包工包料主体
						// 查看关联外委进度
						List<WaigouPlan> wpList = totalDao
								.query(
										" from WaigouPlan where (status is null or status!='设变待确定') and  wwDetailId in (select id from ProcessInforWWApplyDetail where procardId=? and wwType='包工包料' )",
										fProcard.getId());
						if (wpList != null && wpList.size() > 0) {
							for (WaigouPlan wp : wpList) {
								wp.setOldStatus(wp.getStatus());
								wp.setStatus("设变待确定");
								totalDao.update(wp);
								// 设变相关外委记录
								ProcardSbWw psbww = new ProcardSbWw();
								psbww.setStatus("待处理");
								psbww.setPtbbApplyId(ptbbApplyId);// 设变主表Id
								psbww.setProcardId(fProcard.getId());
								psbww.setSbNumber(sbNumber);// 设变单号
								psbww.setMarkId(fProcard.getMarkId());// 件号
								psbww.setYwmarkId(fProcard.getYwMarkId());// 业务件号
								psbww.setBanben(fProcard.getBanBenNumber());// 版本
								psbww.setProName(fProcard.getProName());// 零件名称
								psbww.setBanci(fProcard.getBanci());// 版次
								psbww.setTuhao(fProcard.getTuhao());// 图号
								psbww.setUnit(fProcard.getUnit());// 单位
								psbww.setWwType(wp.getWwType());// 外委类型（工序外委/包工包料、外购件/原材料）
								psbww.setWwSource(wp.getWwSource());// 外委来源（BOM外委,手动外委）
								psbww.setNumber(wp.getNumber());// 数量
								psbww.setProcessNOs(wp.getProcessNOs());// 工序号
								psbww.setProcessNames(wp.getProcessNames());// 工序名称
								psbww.setWwOrderNumber(wp.getWaigouOrder()
										.getPlanNumber());

								// psbww.setwaigouWaiweiPlan;//(BOM外委才有)BOM外委计划Id
								psbww.setWwDetailId(wp.getWwDetailId());// (手动外委才有)外委申请明细Id(ProcessInforWWapplyDeatil)

								// 进入外委单才有价格
								psbww.setHsPrice(wp.getHsPrice().floatValue());// 含税单价
								psbww.setPrice(wp.getPrice().floatValue());// 不含税单价
								psbww.setTaxprice(wp.getTaxprice()); // 税率
								psbww.setMoney(wp.getMoney().floatValue());// 总额
								psbww.setPriceId(wp.getPriceId());// 价格id
								psbww.setUserId(login.getId());// 用户id
								psbww.setUserCode(login.getCode());// 用户工号(编号)
								psbww.setGysId(wp.getGysId());// 供应商id
								psbww.setGysName(wp.getGysName());// 供应商名称
								psbww.setWaigouPlanId(wp.getId());// 委单Id
								psbww.setAddTime(Util.getDateTime());// 添加时间
								ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
								newpsbwwd.copyValue(psbwwd);
								newpsbwwd.setProcardSbWw(psbww);
								Set<ProcardSbWwDetail> psbwwdSet = new HashSet<ProcardSbWwDetail>();
								psbwwdSet.add(newpsbwwd);
								totalDao.save(psbww);
							}
						} else {
							// 还没有到外委订单这一步
							ProcessInforWWApplyDetail pwwd = (ProcessInforWWApplyDetail) totalDao
									.getObjectByCondition(
											"from ProcessInforWWApplyDetail where procardId=? and wwType='包工包料' ",
											fProcard.getId());
							pwwd.setDataStatus("设变待确定");
							totalDao.update(pwwd);
							// 设变相关外委记录
							ProcardSbWw psbww = new ProcardSbWw();
							psbww.setStatus("待处理");
							psbww.setPtbbApplyId(ptbbApplyId);// 设变主表Id
							psbww.setProcardId(fProcard.getId());
							psbww.setSbNumber(sbNumber);// 设变单号
							psbww.setMarkId(fProcard.getMarkId());// 件号
							psbww.setYwmarkId(fProcard.getYwMarkId());// 业务件号
							psbww.setBanben(fProcard.getBanBenNumber());// 版本
							psbww.setProName(fProcard.getProName());// 零件名称
							psbww.setBanci(fProcard.getBanci());// 版次
							psbww.setTuhao(fProcard.getTuhao());// 图号
							psbww.setUnit(fProcard.getUnit());// 单位
							psbww.setWwType(pwwd.getWwType());// 外委类型（工序外委/包工包料、外购件/原材料）
							psbww.setWwSource("手动外委");// 外委来源（BOM外委,手动外委）
							psbww.setNumber(pwwd.getApplyCount());// 数量
							psbww.setProcessNOs(pwwd.getProcessNOs());// 工序号
							psbww.setProcessNames(pwwd.getProcessNames());// 工序名称

							// psbww.setwaigouWaiweiPlan;//(BOM外委才有)BOM外委计划Id
							psbww.setWwDetailId(pwwd.getId());// (手动外委才有)外委申请明细Id(ProcessInforWWapplyDeatil)
							psbww.setAddTime(Util.getDateTime());// 添加时间
							ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
							newpsbwwd.copyValue(psbwwd);
							newpsbwwd.setProcardSbWw(psbww);
							Set<ProcardSbWwDetail> psbwwdSet = new HashSet<ProcardSbWwDetail>();
							psbwwdSet.add(newpsbwwd);
							totalDao.save(psbww);
						}
						return null;
					} else {
						breakWWBysb(fProcard, ptbbApplyId, sbNumber, psbwwd);
					}
				} else {
					if (procard.getStatus().equals("初始")
							|| procard.getStatus().equals("已发卡")) {
						return null;
					}
					// 查看手动外委
					List<WaigouPlan> wpList = totalDao
							.query(
									" from WaigouPlan where (status is null or status!='设变待确定') and  wwDetailId in (select id from ProcessInforWWApplyDetail where procardId=? and wwType='工序外委' )",
									fProcard.getId());
					if (wpList != null && wpList.size() > 0) {
						for (WaigouPlan wp : wpList) {
							wp.setOldStatus(wp.getStatus());
							wp.setStatus("设变待确定");
							totalDao.update(wp);
							// 设变相关外委记录
							ProcardSbWw psbww = new ProcardSbWw();
							psbww.setStatus("待处理");
							psbww.setPtbbApplyId(ptbbApplyId);// 设变主表Id
							psbww.setSbNumber(sbNumber);// 设变单号
							psbww.setProcardId(fProcard.getId());
							psbww.setMarkId(fProcard.getMarkId());// 件号
							psbww.setYwmarkId(fProcard.getYwMarkId());// 业务件号
							psbww.setBanben(fProcard.getBanBenNumber());// 版本
							psbww.setProName(fProcard.getProName());// 零件名称
							psbww.setBanci(fProcard.getBanci());// 版次
							psbww.setTuhao(fProcard.getTuhao());// 图号
							psbww.setUnit(fProcard.getUnit());// 单位
							psbww.setWwType(wp.getWwType());// 外委类型（工序外委/包工包料、外购件/原材料）
							psbww.setWwSource(wp.getWwSource());// 外委来源（BOM外委,手动外委）
							psbww.setNumber(wp.getNumber());// 数量
							psbww.setProcessNOs(wp.getProcessNOs());// 工序号
							psbww.setProcessNames(wp.getProcessNames());// 工序名称
							psbww.setWwOrderNumber(wp.getWaigouOrder()
									.getPlanNumber());
							// psbww.setwaigouWaiweiPlan;//(BOM外委才有)BOM外委计划Id
							psbww.setWwDetailId(wp.getWwDetailId());// (手动外委才有)外委申请明细Id(ProcessInforWWapplyDeatil)

							// 进入外委单才有价格
							psbww.setHsPrice(wp.getHsPrice().floatValue());// 含税单价
							psbww.setPrice(wp.getPrice().floatValue());// 不含税单价
							psbww.setTaxprice(wp.getTaxprice()); // 税率
							psbww.setMoney(wp.getMoney().floatValue());// 总额
							psbww.setPriceId(wp.getPriceId());// 价格id
							psbww.setUserId(login.getId());// 用户id
							psbww.setUserCode(login.getCode());// 用户工号(编号)
							psbww.setGysId(wp.getGysId());// 供应商id
							psbww.setGysName(wp.getGysName());// 供应商名称
							psbww.setWaigouPlanId(wp.getId());// 委单Id
							psbww.setAddTime(Util.getDateTime());// 添加时间
							ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
							newpsbwwd.copyValue(psbwwd);
							newpsbwwd.setProcardSbWw(psbww);
							Set<ProcardSbWwDetail> psbwwdSet = new HashSet<ProcardSbWwDetail>();
							psbwwdSet.add(newpsbwwd);
							totalDao.save(psbww);
						}
						return null;
					} else {
						// 还没有到外委订单这一步
						ProcessInforWWApplyDetail pwwd = (ProcessInforWWApplyDetail) totalDao
								.getObjectByCondition(
										"from ProcessInforWWApplyDetail where procardId=? and wwType='工序外委' and (dataStatus is null or dataStatus!='设变待确定') and processInforWWApply.status='同意'",
										fProcard.getId());
						if (pwwd != null) {
							pwwd.setDataStatus("设变待确定");
							totalDao.update(pwwd);
							// 设变相关外委记录
							ProcardSbWw psbww = new ProcardSbWw();
							psbww.setPtbbApplyId(ptbbApplyId);// 设变主表Id
							psbww.setSbNumber(sbNumber);// 设变单号
							psbww.setProcardId(fProcard.getId());
							psbww.setMarkId(fProcard.getMarkId());// 件号
							psbww.setYwmarkId(fProcard.getYwMarkId());// 业务件号
							psbww.setBanben(fProcard.getBanBenNumber());// 版本
							psbww.setProName(fProcard.getProName());// 零件名称
							psbww.setBanci(fProcard.getBanci());// 版次
							psbww.setTuhao(fProcard.getTuhao());// 图号
							psbww.setUnit(fProcard.getUnit());// 单位
							psbww.setWwType(pwwd.getWwType());// 外委类型（工序外委/包工包料、外购件/原材料）
							psbww.setWwSource("手动外委");// 外委来源（BOM外委,手动外委）
							psbww.setNumber(pwwd.getApplyCount());// 数量
							psbww.setProcessNOs(pwwd.getProcessNOs());// 工序号
							psbww.setProcessNames(pwwd.getProcessNames());// 工序名称

							// psbww.setwaigouWaiweiPlan;//(BOM外委才有)BOM外委计划Id
							psbww.setWwDetailId(pwwd.getId());// (手动外委才有)外委申请明细Id(ProcessInforWWapplyDeatil)
							psbww.setAddTime(Util.getDateTime());// 添加时间
							ProcardSbWwDetail newpsbwwd = new ProcardSbWwDetail();
							newpsbwwd.copyValue(psbwwd);
							newpsbwwd.setProcardSbWw(psbww);
							Set<ProcardSbWwDetail> psbwwdSet = new HashSet<ProcardSbWwDetail>();
							psbwwdSet.add(newpsbwwd);
							totalDao.save(psbww);
							return null;
						} else {
							breakWWBysb(fProcard, ptbbApplyId, sbNumber, psbwwd);
						}
					}

				}
			}

		}

		return null;
	}

	/**
	 * 设置下层sbStatus为删除
	 * 
	 * @param ptbbApplyId
	 * @param procard
	 * @param nowTime
	 * @param scgxCount上层未领工序数量
	 * @return
	 */
	private String deleteDownProcard(Integer ptbbApplyId, String sbNumber,
			Procard procard, String nowTime, Float scgxCount, String clType) {
		// TODO Auto-generated method stub
		Float thisscwlgxCount = 0f;
		if ((procard.getSbStatus() == null || !procard.getSbStatus().equals(
				"删除"))
				&& !procard.getStatus().equals("已发料")
				&& !procard.getStatus().equals("领工序")
				&& !procard.getStatus().equals("完成")) {
			// 添加报废数据
			List<ProcessInfor> processList = totalDao
					.query(
							"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus !='删除') order by processNO",
							procard.getId());
			for (int j = 0; j < processList.size(); j++) {
				float zzCount = 0f;
				ProcessInfor process = processList.get(j);
				if (j == 0) {
					thisscwlgxCount = procard.getFilnalCount()
							- process.getApplyCount();
				}
				if (process.getApplyCount() == 0) {
					continue;
				}
				// 此工序已领单未提交数量
				zzCount += process.getApplyCount() - process.getSubmmitCount();
				if ((j + 1) <= (processList.size() - 1)) {
					ProcessInfor process2 = processList.get((j + 1));
					// 此道工序做完未流转到下道工序的数量
					zzCount += process.getSubmmitCount()
							- process2.getApplyCount();
					if (zzCount < 0) {
						zzCount = 0;
					}
				} else {
					Float nowgxCount = 0f;
					if (procard.getProcardStyle().equals("外购")) {
						nowgxCount = scgxCount * procard.getQuanzi2()
								/ procard.getQuanzi1();
					} else {
						nowgxCount = scgxCount * procard.getCorrCount();
						nowgxCount = (float) Math.ceil(nowgxCount);
					}
					if (nowgxCount < process.getSubmmitCount()) {// 最后一道工序扣去上层已领
						zzCount += (process.getSubmmitCount() - nowgxCount);
					}
				}
				if (zzCount > 0) {// 查询此工序当前的半成品数量
					if (zzCount > 0) {
						// 生成报废待处理在制品
						addProcardSbWaster(ptbbApplyId, sbNumber, procard,
								zzCount, nowTime, process.getProcessNO(),
								process.getProcessName(), clType);
						sbData++;
					}
				}
			}

		}
		procard.setSbId(ptbbApplyId);
		procard.setSbNumber(sbNumber);
		procard.setSbStatus("删除");
		if (procard.getFatherId() != null) {
			procard.setOldFatherId(procard.getFatherId());
			procard.setOldRootId(procard.getRootId());
		}
		procard.setFatherId(null);
		procard.setRootId(null);
		procard.setHasChange("是");
		totalDao.update(procard);
		if (procard.getProcardStyle().equals("外购")) {
			// 处理外购订单
			addProcardSbWg(ptbbApplyId, sbNumber, procard, -procard
					.getFilnalCount(), nowTime);
			sbData++;
		}
		// 遍历下层零件设置设变属性为删除
		Set<Procard> sonSet = procard.getProcardSet();
		if (sonSet != null && sonSet.size() > 0) {
			for (Procard son : sonSet) {
				deleteDownProcard(ptbbApplyId, sbNumber, son, nowTime,
						thisscwlgxCount, clType);
			}
		}
		return null;
	}

	/**
	 * 更新生产件具体处理方法
	 * 
	 * @param pt
	 *            模板
	 * @param pd
	 *            生产件
	 * @param finalCount
	 *            上层数量
	 * @param totalWlStatus
	 *            总成是否已经激活
	 * @param procardAboutBanBenApplyList
	 *            //该BOM对应生产件处理方案
	 * @param pAbb
	 *            //生产件处理方案
	 * @param changeyl
	 *            //是否修改用量
	 * @param sbwqtb
	 *            //是否完全同步设变
	 * @return
	 */
	public boolean updateProcard(Integer ptbbApplyId, String sbNumber,
			ProcardTemplate pt, Procard pd, Float finalCount, Float bfCount,
			String totalWlStatus, ProcardAboutBanBenApply pAbb,
			List<ProcardAboutBanBenApply> procardAboutBanBenApplyList,
			String nowTime, List<String> sbmarkIds, Users user,
			boolean changeyl, String sbwqtb, Map<String, String> markidMap,
			Set<Integer> syProcardIds, Set<Integer> nsyProcardIds) {
		boolean sameBanci = false;
		int ptbanci = pt.getBanci() == null ? 0 : pt.getBanci();
		int pdbanci = pd.getBanci() == null ? 0 : pd.getBanci();
		if (ptbanci == pdbanci) {
			sameBanci = true;
		}
		if (pAbb != null && pAbb.getStatus() != null
				&& pAbb.getStatus().equals("完成")) {// 已经处理过了
			return true;
		}
		if (pd.getSbStatus() != null && pd.getSbStatus().equals("删除")) {// 已被上级设变连带处理过
			if (pAbb != null) {
				pAbb.setStatus("完成");
				totalDao.update(pAbb);
				return true;
			}
		}
		if (pd.getStatus().equals("设变锁定")) {// 恢复状态
			pd.setStatus(pd.getOldStatus());
		}
		boolean b = true;
		float changeCount = 0;// 现数量-原数量
		Set<ProcessAboutBanBenApply> processabbSet = null;
		String clType = "";
		if (pAbb != null) {
			processabbSet = pAbb.getProcessabbSet();
			if (processabbSet != null && processabbSet.size() > 0) {
				for (ProcessAboutBanBenApply processabb : processabbSet) {
					if (!processabb.getClType().equals("顺延")) {
						clType = processabb.getClType();
						break;
					} else {
						clType = "顺延";
					}
				}
				if (clType.equals("顺延")) {// 无需更改
					syProcardIds.add(pAbb.getId());
					pAbb.setStatus("完成");
					totalDao.update(pAbb);
					return true;
				} else {
					nsyProcardIds.add(pAbb.getId());
				}
			}
		}
		Float nowFinalCount = null;// 新批次数量
		Float oldFilnalCount = pd.getFilnalCount();
		Float nowbfCount = null;// 本次报废数量
		Float nowq1 = pt.getQuanzi1();
		Float nowq2 = pt.getQuanzi2();

		if (finalCount != null && changeyl) {
			if (pt.getProcardStyle().equals("外购")) {
				nowFinalCount = finalCount * pt.getQuanzi2() / pt.getQuanzi1();
				if (pt.getSunhao() != null && pt.getSunhao() > 0) {
					if (pt.getSunhaoType() != null && pt.getSunhaoType() == 1) {
						nowFinalCount = nowFinalCount + pt.getSunhao();
					} else {
						nowFinalCount = nowFinalCount * (pt.getSunhao() + 100)
								/ 100F;
					}
					nowq1 = 1f;
					try {
						nowq2 = Util.Floatdiv(nowFinalCount, finalCount, 4);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						throw new RuntimeException("外购件：" + pt.getMarkId()
								+ "用量计算有误，(上层零件为:"
								+ pt.getProcardTemplate().getMarkId() + ")");
					}
				}
				if (bfCount != null) {
					nowbfCount = bfCount * nowq2 / nowq1;
				}
			} else {
				nowFinalCount = finalCount * pt.getCorrCount();
				nowFinalCount = (float) Math.ceil(nowFinalCount);
				if (bfCount != null) {
					nowbfCount = bfCount * pt.getCorrCount();
				}
			}
		} else {// 没有上层数量，此为总成或者不需要同步用量
			nowFinalCount = pd.getFilnalCount();
		}
		nowFinalCount = Util.FomartFloat(nowFinalCount, 4);
		if (nowbfCount != null) {
			if (pd.getLingliaostatus() == null
					|| !pd.getLingliaostatus().equals("否")) {
				if (pd.getHascount() != null) {
					pd.setHascount(pd.getHascount() + nowbfCount);
					if (pd.getHascount() > pd.getKlNumber()) {
						pd.setHascount(pd.getKlNumber());
					}
				}
			}
			if (pd.getTjNumber() != null) {
				pd.setTjNumber(pd.getTjNumber() - nowbfCount);
				if (pd.getTjNumber() < 0) {
					pd.setTjNumber(0f);
				}
			}
			if (pd.getOldStatus() != null && pd.getOldStatus().length() > 0
					&& pd.getStatus().equals("设变锁定")) {
				pd.setStatus(pd.getOldStatus());
			}
		}
		changeCount = Util.Floatdelete(nowFinalCount, pd.getFilnalCount());
		boolean haschangge = false;
		if (pd.getProcardStyle().equals("外购")) {
			if (pt.getProcardStyle().equals("自制")
					|| pt.getProcardStyle().equals("总成")) {// 外购件变自制件
				haschangge = true;
				Procard father = pd.getProcard();
				pd.setSbStatus("删除");
				if (pd.getFatherId() != null) {
					pd.setOldFatherId(pd.getFatherId());
					pd.setOldRootId(pd.getRootId());
				}
				pd.setFatherId(null);
				pd.setRootId(null);
				// 原来的减少1
				addProcardSbWg(ptbbApplyId, sbNumber, pd, -pd.getFilnalCount(),
						nowTime);
				sbData++;
				totalDao.update(pd);
				// 新添自制件
				addProcardInFather(ptbbApplyId, sbNumber, father, pd, pt,
						totalWlStatus, 0);
				if (pAbb != null) {
					pAbb.setStatus("完成");
					totalDao.update(pAbb);
				}
				return b;
			} else {
				if (changeCount > -0.001 && changeCount < 0.001
						&& (changeCount * nowq1 / nowq2) < 0.05
						&& (changeCount * nowq1 / nowq2) > -0.05) {
					changeCount = 0;// 屏蔽小数点差异
				}
				if (!Util.isEquals(pt.getBanBenNumber(), pd.getBanBenNumber())) {
					haschangge = true;
					// 版本不一样
					// 原来的减少
					addProcardSbWg(ptbbApplyId, sbNumber, pd, -pd
							.getFilnalCount(), nowTime);
					sbData++;
					if (pt.getProcardStyle().equals("外购")) {
						Procard pd2 = new Procard();
						pd.setSbId(ptbbApplyId);
						pd.setSbNumber(sbNumber);
						pd.setHasChange("是");
						BeanUtils.copyProperties(pd, pd2, new String[] { "id",
								"procard", "procardSet", "processInforSet",
								"wgwwPlanSet", "procardPartsSet", "procardPro",
								"oneProcardBonus", "processPeopleSet" });
						Procard father = pd.getProcard();
						pd2.setQuanzi2(nowq2);
						pd2.setQuanzi1(nowq1);
						pd2.setProcard(father);
						pd2.setCgNumber(null);
						pd2.setOutcgNumber(null);
						pd2.setWlstatus("待采购");
						pd2.setProName(pt.getProName());
						pd2.setBanBenNumber(pt.getBanBenNumber());
						pd2.setBanci(pt.getBanci());
						pd2.setKgliao(pt.getKgliao());
						pd2.setProcardStyle(pt.getProcardStyle());
						pd2.setStatus("初始");
						pd2.setLingliaoDetail(null);
						pd2.setNeedProcess(pt.getNeedProcess());
						pd2.setFilnalCount(nowFinalCount);
						pd2.setLingliaostatus(pt.getLingliaostatus());
						if (pt.getNeedProcess() == null
								|| !pt.getNeedProcess().equals("yes")) {
							if (pt.getLingliaostatus() == null
									|| !pt.getLingliaostatus().equals("是")) {
								father.setHascount(father.getKlNumber());
							}
						} else {
							pd2.setKlNumber(pd2.getFilnalCount());
							pd2.setHascount(pd2.getFilnalCount());
							// 复制工序
							// 遍历该流水卡片对应工序并生成工序
							Set<ProcessTemplate> setProCess = pt
									.getProcessTemplate();
							for (ProcessTemplate processTem : setProCess) {
								ProcessInfor process = new ProcessInfor();
								BeanUtils
										.copyProperties(processTem, process,
												new String[] { "id",
														"procardTemplate" });
								// process.setProcessNO(processTem.getProcessNO());//
								// 工序号
								// process.setProcessName(processTem.getProcessName());//
								// 工序名称
								// process.setProcessStatus(processTem.getProcessStatus());//
								// 状态(并行/单独)
								// process.setParallelId(processTem.getParallelId());//
								// 并行开始id

								// 人工节拍和设备节拍处理
								if (process.getProductStyle() != null
										&& process.getProductStyle().equals(
												"外委")) {// 外委工序节拍设为1
									process.setOpcaozuojiepai(1f);
									process.setOpshebeijiepai(1f);
									process.setGzzhunbeijiepai(1f);
									process.setGzzhunbeicishu(1f);
									process.setAllJiepai(1f);
								} else {
									if (process.getOpcaozuojiepai() == null) {
										process.setOpcaozuojiepai(0F);
									}
									if (process.getOpshebeijiepai() == null) {
										process.setOpshebeijiepai(0F);
									}
								}
								process.setTotalCount(pd.getFilnalCount());// 可领取量
								process.setStatus("初始");
								process.setProcard(pd);
								// -----------------辅料------------------
								if (processTem.getIsNeedFuliao() != null
										&& processTem.getIsNeedFuliao().equals(
												"yes")) {
									process.setIsNeedFuliao("yes");
									Set<ProcessFuLiaoTemplate> fltmpSet = processTem
											.getProcessFuLiaoTemplate();
									if (fltmpSet.size() > 0) {
										Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
										for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
											ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
											BeanUtils.copyProperties(fltmp,
													pinfoFl,
													new String[] { "id" });
											if (pinfoFl.getQuanzhi1() == null) {
												pinfoFl.setQuanzhi1(1f);
											}
											if (pinfoFl.getQuanzhi2() == null) {
												pinfoFl.setQuanzhi2(1f);
											}
											pinfoFl.setTotalCount(pd
													.getFilnalCount()
													* pinfoFl.getQuanzhi2()
													/ pinfoFl.getQuanzhi1());
											pinfoFl.setProcessInfor(process);
											pinfoFl.setOutCount(0f);
											pinfoFlSet.add(pinfoFl);
										}
										process
												.setProcessinforFuLiao(pinfoFlSet);
									}
								}
								try {
									totalDao.save(process);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
						totalDao.save(pd2);
						pd.setSbStatus("删除");
						if (pd.getFatherId() != null) {
							pd.setOldFatherId(pd.getFatherId());
							pd.setOldRootId(pd.getRootId());
						}
						pd.setFatherId(null);
						pd.setRootId(null);
						pd.setProcard(null);
						totalDao.update(pd);
						// 新的增加新增加的外购件由激活时重算采购
						// addProcardSbWg(ptbbApplyId, sbNumber, pd2,
						// nowFinalCount, nowTime);
						// 外委设变明细(原由)
						ProcardSbWwDetail psbwwd = new ProcardSbWwDetail();
						psbwwd.setProcardId(pd.getId());//
						psbwwd.setMarkId(pd.getMarkId());//
						psbwwd.setBanBenNumber(pd.getBanBenNumber());// 版本号
						psbwwd.setBanci(pd.getBanci());// 版次
						psbwwd.setProName(pd.getProName());//
						psbwwd.setTuhao(pd.getTuhao());//
						psbwwd.setKgliao(pd.getKgliao());//
						psbwwd.setSbtype("新增");// 设变类型（新加,删除,用量调整）
						String msg = breakWWBysb(pd, ptbbApplyId, sbNumber,
								psbwwd);
						pd = pd2;
						sbData++;
					}
				} else {
					if (changeCount != 0
							&& (changeCount > 0.0001 || changeCount < -0.0001)) {
						haschangge = true;
						if (pd.getKlNumber() != null) {
							if (pd.getHascount() == null) {
								pd.setHascount(pd.getKlNumber());
							}
							if (pd.getKlNumber() < pd.getHascount()) {
								pd.setKlNumber(pd.getHascount());
							}
							Float ylCount = pd.getKlNumber() - pd.getHascount();
							if (pd.getFilnalCount() != null
									&& pd.getFilnalCount() > 0
									&& pd.getHasPlan() != null) {
								pd.setHasPlan(pd.getHasPlan()
										* nowFinalCount
										/ pd.getFilnalCount());
							}
							if (changeCount > 0) {// 用量增加
								if (pd.getWwblCount() != null
										&& pd.getWwblCount() / nowFinalCount > 0.99) {// 包工包料按比例填充
									pd.setKlNumber(pd.getKlNumber()
											* nowFinalCount
											/ pd.getFilnalCount());
								} else {
									pd.setKlNumber(pd.getKlNumber()
											+ changeCount);
									pd.setHascount(pd.getHascount()
											+ changeCount);
									boolean zj = false;
									// 获取备料表信息
									List<ProcardBl> pblList = totalDao.query(
											"from ProcardBl where procardId=?",
											pd.getId());
									if (pblList != null && pblList.size() > 0) {
										for (ProcardBl pbl : pblList) {
											if (pd.getFilnalCount() == null
													|| pd.getFilnalCount() == 0) {
												pbl.setPcCount(nowFinalCount);
											} else {
												pbl.setPcCount(pbl.getPcCount()
														* nowFinalCount
														/ pd.getFilnalCount());
											}
											if (pbl.getLingliaoStatus() == null
													|| !pbl.getLingliaoStatus()
															.equals("否")) {
												pbl.setStatus("未领完");
												zj = true;
											}
											totalDao.update(pbl);
										}
									}
									if (pd.getProcardStyle().equals("外购") && zj) {
										List<ProcardBl> pblList2 = totalDao
												.query(
														"from ProcardBl where procardId=?",
														pd.getFatherId());
										if (pblList2 != null
												&& pblList2.size() > 0) {
											for (ProcardBl pbl : pblList2) {
												pbl.setYlCount(0f);
												pbl.setStatus("未领完");
												totalDao.update(pbl);
											}
										}
									}

								}
								if (pd.getStatus().equals("完成")) {
									pd.setStatus("已发卡");
								}
								pd.getProcard().setHascount(
										pd.getProcard().getKlNumber());
								ProcardBl fatherbl = (ProcardBl) totalDao
										.getObjectById(ProcardBl.class, pd
												.getProcard().getId());
								if (fatherbl != null) {
									fatherbl.setStatus("未领");
									fatherbl.setYlCount(0f);
									totalDao.update(fatherbl);
								}
							} else {
								if (pd.getWwblCount() != null
										&& pd.getWwblCount() / nowFinalCount > 0.99) {// 包工包料按比例填充
									pd.setKlNumber(pd.getKlNumber()
											* nowFinalCount
											/ pd.getFilnalCount());
								} else {
									pd.setKlNumber(pd.getKlNumber()
											+ changeCount);
									Float hascount = pd.getHascount()
											+ changeCount;
									if (hascount < 0) {
										hascount = 0f;
									}
									pd.setHascount(hascount);
									if (pd.getHascount() < 0) {
										pd.setHascount(0f);
									}
									// 获取备料表信息
									List<ProcardBl> pblList = totalDao.query(
											"from ProcardBl where procardId=?",
											pd.getId());
									if (pblList != null && pblList.size() > 0) {
										for (ProcardBl pbl : pblList) {
											if (pd.getFilnalCount() == null
													|| pd.getFilnalCount() == 0) {
												pbl.setPcCount(nowFinalCount);
											} else {
												pbl.setPcCount(pbl.getPcCount()
														* nowFinalCount
														/ pd.getFilnalCount());
											}
											if (pbl.getYlCount() != null
													&& pbl.getYlCount() >= pbl
															.getPcCount()) {
												pbl.setStatus("已领完");
											}
											totalDao.update(pbl);
										}
									}
								}
								if (pd.getHascount() == 0) {
									pd.setStatus("完成");
								}
								if (ylCount > pd.getKlNumber()) {// 已领用数量>需求数量，生成退库操作
									GoodsStore gs = new GoodsStore();
									gs.setGoodsStoreWarehouse("外购件库");// 库别
									gs.setGoodsStoreMarkId(pd.getMarkId());// 件号
									gs.setBanBenNumber(pd.getBanBenNumber());// 版本号
									gs.setKgliao(pd.getKgliao());// 供料属性
									gs.setGoodsStoreLot("ECN"
											+ pd.getSelfCard());// 批次
									gs.setGoodsStoreGoodsName(pd.getProName());// 名称
									gs.setGoodsStoreFormat(pd
											.getSpecification());// 规格
									gs.setWgType(pd.getWgType());// 物料类别
									gs.setGoodsStoreUnit(pd.getUnit());// 单位
									gs.setGoodsStoreCount(Util.Floatdelete(
											ylCount, pd.getKlNumber()));// 数量
									// gs.setgoodsStoreZhishu;// 转换数量
									// gs.setgoodsStoreZHUnit; // 转换单位
									// gs.sethsPrice;// 单价(含税)
									// gs.setgoodsStorePrice;// 单价(不含税)
									// private Double taxprice; // 税率
									// gs.setmoney;// 总额
									// private Integer priceId;// 价格id

									// gs.setgoodsStoreCompanyName;// 客户
									// gs.setgoodsStoreSupplier;// 供应商
									// gs.setgysId;// 供应商Id
									// gs.setcustomerId;// 客户ID
									gs.setGoodsStoreDate(nowTime.substring(0,
											10));// 时间
									gs.setGoodsStoreTime(nowTime);// 系统默认入库时间
									gs.setGoodsStorePerson(user.getName());// 负责人
									gs.setGoodsStoreGoodsMore(sbNumber);// 备注
									// gs.setGoodsStoreAdminId(user.getId());//
									// 管理员ID
									// gs.setgoodsStoreAdminName;// 管理员姓名
									gs.setGoodsStoreCharger(user.getName());// 办理人
									gs.setGoodsStorePlanner(user.getName());// 入库人
									gs.setGoodsStoreArtsCard(pd.getMarkId());// 工艺卡片号
									gs.setGoodsStoreProMarkId(pd
											.getRootMarkId());// 总成件号
									gs.setTuhao(pd.getTuhao());// 图号(别称)
									gs.setNeiorderId(pd.getOrderNumber());// 内部订单号;
									gs.setWaiorderId(pd.getOutOrderNum());// 外部订单号;
									gs.setInputSource("设变退库");// 入库来源（手动入库(来源为手动入库时出库关联的订单id为order_Id)，生产入库(默认为生产入库)）
									gs.setStatus("待入库");// 状态（待入库/入库）
									gs.setStyle("设变退库");// 入库类型（领用、可借用。
									// /返修入库/退货入库/批产/试制/中间件）（冲销转库和半成品转库为特殊入库类型与功能相关慎用）
									gs.setApplyTime(nowTime);// 申请入库时间
									gs.setPrintStatus("NO");// 打印状态(YES/NO)
									gs.setSqUsersName(user.getName());// 申请入库人;
									gs.setSqUsrsId(user.getId());// 申请人姓名
									gs.setSqUsersCode(user.getCode());// 申请人工号
									gs.setSqUsersdept(user.getDept());// 申请人部门
									totalDao.save(gs);
								}
							}
						}

						pd.setQuanzi1(nowq1);
						pd.setQuanzi2(nowq2);
						pd.setFilnalCount(nowFinalCount);
					}
					// 版本一样
					if (pd.getKgliao().equals(pt.getKgliao())) {// 供料属性一样
						if (changeCount < -0.0001
								|| (changeCount > 0.0001 && !pd.getStatus()
										.equals("初始"))) {
							addProcardSbWg(ptbbApplyId, sbNumber, pd,
									changeCount, nowTime);
							sbData++;
						}
					} else {
						// 原来的减少
						addProcardSbWg(ptbbApplyId, sbNumber, pd, -pd
								.getFilnalCount(), nowTime);
						sbData++;
						pd.setKgliao(pt.getKgliao());
						pd.setCgNumber(null);
						pd.setOutcgNumber(null);
						pd.setKlNumber(null);
						pd.setHascount(null);
						pd.setTjNumber(null);
						pd.setStatus("初始");
						totalDao.update(pd);
						// if (pt.getProcardStyle().equals("外购")) {
						// Procard pd2 = new Procard();
						// pd2.setMarkId(pt.getMarkId());
						// pd2.setProName(pt.getProName());
						// pd2.setBanBenNumber(pt.getBanBenNumber());
						// pd2.setBanci(pt.getBanci());
						// pd2.setKgliao(pt.getKgliao());
						// pd2.setProcardStyle(pt.getProcardStyle());
						// pd2.setKgliao(pt.getKgliao());
						// // 新的增加
						// addProcardSbWg(ptbbApplyId, sbNumber, pd2,
						// nowFinalCount, nowTime);
						// sbData++;
						// }
					}
				}
			}

		} else {
			if (pd.getProcardStyle().equals("自制")) {
				if (pt.getProcardStyle().equals("外购")) {// 自制件改外购
					// 删除原自制件
					Procard father = pd.getProcard();
					pd.setSbStatus("删除");
					if (pd.getFatherId() != null) {
						pd.setOldFatherId(pd.getFatherId());
						pd.setOldRootId(pd.getRootId());
					}
					pd.setFatherId(null);
					pd.setRootId(null);
					Float scgxCount = 0f;
					List<ProcessInfor> processList = totalDao
							.query(
									"from ProcessInfor where procard.id=? order by processNO",
									pd.getId());
					for (int j = 0; j < processList.size(); j++) {
						float zzCount = 0f;
						ProcessInfor process = processList.get(j);
						if (j == 0) {
							scgxCount = process.getApplyCount();
						}
						if (process.getApplyCount() == 0) {
							continue;
						}
						// 此工序已领单未提交数量
						zzCount += process.getApplyCount()
								- process.getSubmmitCount();
						if ((j + 1) <= (processList.size() - 1)) {
							ProcessInfor process2 = processList.get((j + 1));
							// 此道工序做完未流转到下道工序的数量
							zzCount += process.getSubmmitCount()
									- process2.getApplyCount();
							if (zzCount < 0) {
								zzCount = 0;
							}
						} else {
							zzCount += process.getSubmmitCount();
						}
						if (zzCount > 0) {// 查询此工序当前的半成品数量
							if (zzCount > 0) {
								// 生成报废待处理在制品
								addProcardSbWaster(ptbbApplyId, sbNumber, pd,
										zzCount, nowTime, process
												.getProcessNO(), process
												.getProcessName(), clType);
								sbData++;
							}
						}
					}
					// 遍历下层零件设置设变属性为删除
					Set<Procard> sonSet2 = pd.getProcardSet();
					if (sonSet2 != null && sonSet2.size() > 0) {
						for (Procard son2 : sonSet2) {
							deleteDownProcard(ptbbApplyId, sbNumber, son2,
									nowTime, scgxCount, clType);
						}
					}
					totalDao.update(pd);
					// 新增外购件
					addProcardInFather(ptbbApplyId, sbNumber, father, pd, pt,
							totalWlStatus, 0);
					father.setHascount(father.getKlNumber());
					List<ProcardBl> fpblList = totalDao.query(
							"from ProcardBl where procardId=? and ylCount>0",
							father.getId());
					if (fpblList != null && fpblList.size() > 0) {
						for (ProcardBl pbl : fpblList) {
							pbl.setYlCount(0f);
							pbl.setStatus("未领完");
							totalDao.update(pbl);
						}
					}

					if (pt.getProcardStyle().equals("外购")
							&& (pt.getLingliaostatus() == null || !pt
									.getLingliaostatus().equals("否"))) {
						father.setHascount(father.getKlNumber());
						List<ProcardBl> pblList2 = totalDao
								.query(
										"from ProcardBl where procardId=? and ylCount>0",
										father.getId());
						if (pblList2 != null && pblList2.size() > 0) {
							for (ProcardBl pbl : pblList2) {
								pbl.setYlCount(0f);
								pbl.setStatus("未领完");
								totalDao.update(pbl);
							}
						}
					}
					if (pAbb != null) {
						pAbb.setStatus("完成");
						totalDao.update(pAbb);
					}
					return b;
				} else {
					if (changeyl) {
						if (!Util.isEquals(pd.getFilnalCount(), nowFinalCount)) {// 用量有变化
							updategxcount(pd, pd.getFilnalCount(),
									nowFinalCount);
							// 获取备料表信息
							if (pd.getFilnalCount() != null
									&& pd.getFilnalCount() > 0
									&& pd.getHasPlan() != null) {
								pd.setHasPlan(pd.getHasPlan() * nowFinalCount
										/ pd.getFilnalCount());
							}
							List<ProcardBl> pblList = totalDao.query(
									"from ProcardBl where procardId=?", pd
											.getId());
							if (pblList != null && pblList.size() > 0) {
								for (ProcardBl pbl : pblList) {
									if (pd.getFilnalCount() == null
											|| pd.getFilnalCount() == 0) {
										pbl.setPcCount(nowFinalCount);
									} else {
										pbl.setPcCount(pbl.getPcCount()
												* nowFinalCount
												/ pd.getFilnalCount());
									}
									if (pbl.getYlCount() != null
											&& pbl.getYlCount() >= pbl
													.getPcCount()) {
										pbl.setStatus("已领完");
									}
									totalDao.update(pbl);
								}
							}
						}
						pd.setFilnalCount(nowFinalCount);
						pd.setCorrCount(pt.getCorrCount());
					}
				}
			}
		}
		if (!sameBanci) {
			if (pd.getStatus().equals("初始") || pd.getStatus().equals("已发卡")) {// 未领料,则只需结构和工序
				if (pt.getProcardStyle() != null
						&& pt.getProcardStyle().equals("外购")
						&& pd.getNeedProcess() != null
						&& pd.getNeedProcess().equals("yes")
						&& (pt.getNeedProcess() == null || pt.getNeedProcess()
								.equals("no"))) {// 外购件原需要工序，现不要工序
					if (changeyl) {
						if (pd.getWwblCount() != null && pd.getWwblCount() > 0) {// 如果有外委包料的同步外委包料数据
							if (pd.getProcardStyle().equals("外购")) {
								Float bltosc = pd.getWwblCount()
										* pd.getQuanzi1() / pd.getQuanzi2();
								int bltoscint = Math.round(bltosc);
								Float blCount = bltoscint * pt.getQuanzi2()
										/ pt.getQuanzi1();
								Float ce = Math.abs(blCount - nowFinalCount);
								if ((ce * pt.getQuanzi1() / pt.getQuanzi2()) < 0.05) {// 防偏差
									blCount = nowFinalCount;
								}
								blCount = Util.FomartFloat(blCount, 4);
								pd.setWwblCount(blCount);
							} else {
								Float bltosc = pd.getWwblCount()
										/ pd.getCorrCount();
								int bltoscint = Math.round(bltosc);
								Float blCount = bltoscint * pt.getCorrCount();
								Float ce = Math.abs(blCount - nowFinalCount);
								if ((ce / pt.getCorrCount()) < 0.05) {// 防偏差
									blCount = nowFinalCount;
								}
								blCount = Util.FomartFloat(blCount, 4);
								pd.setWwblCount(blCount);
							}

						}
						// pd.setJihuoStatua(null);
						if (!haschangge) {
							if (pd.getHascount() != null) {
								if (pd.getKlNumber() == null) {
									pd.setHascount(nowFinalCount);
								} else {
									pd.setHascount(nowFinalCount
											- (pd.getKlNumber() - pd
													.getHascount()));
								}
							}
							pd.setKlNumber(pd.getFilnalCount());
						}
					}
					// 获取备料表信息
					List<ProcardBl> pblList = totalDao.query(
							"from ProcardBl where procardId=?", pd.getId());
					if (pblList != null && pblList.size() > 0) {
						for (ProcardBl pbl : pblList) {
							if (pd.getFilnalCount() == null
									|| pd.getFilnalCount() == 0) {
								pbl.setPcCount(nowFinalCount);
							} else {
								pbl.setPcCount(pbl.getPcCount() * nowFinalCount
										/ pd.getFilnalCount());
							}
							if (pbl.getYlCount() != null
									&& pbl.getYlCount() >= pbl.getPcCount()) {
								pbl.setStatus("已领完");
							}
							totalDao.update(pbl);
						}
					}
					pd.setFilnalCount(nowFinalCount);
					pd.setTjNumber(0F);
					pd.setMinNumber(0F);
					// }
				} else if (pt.getProcardStyle() != null
						&& pt.getProcardStyle().equals("外购")
						&& pt.getNeedProcess() != null
						&& pt.getNeedProcess().equals("yes")
						&& (pd.getNeedProcess() == null || pd.getNeedProcess()
								.equals("no"))) {// 外购件原不需要工序，现要工序
					if (changeyl) {
						if (!haschangge && pd.getHascount() != null) {
							if (pd.getKlNumber() == null) {
								pd.setHascount(nowFinalCount);
							} else {
								pd
										.setHascount(nowFinalCount
												- (pd.getKlNumber() - pd
														.getHascount()));
							}
						}
					}
					// 获取备料表信息
					List<ProcardBl> pblList = totalDao.query(
							"from ProcardBl where procardId=?", pd.getId());
					if (pblList != null && pblList.size() > 0) {
						for (ProcardBl pbl : pblList) {
							if (pd.getFilnalCount() == null
									|| pd.getFilnalCount() == 0) {
								pbl.setPcCount(nowFinalCount);
							} else {
								pbl.setPcCount(pbl.getPcCount() * nowFinalCount
										/ pd.getFilnalCount());
							}
							if (pbl.getYlCount() != null
									&& pbl.getYlCount() >= pbl.getPcCount()) {
								pbl.setStatus("已领完");
							}
							totalDao.update(pbl);
						}
					}
					pd.setFilnalCount(nowFinalCount);
					pd.setKlNumber(pd.getFilnalCount());
					pd.setTjNumber(0f);
					pd.setMinNumber(0f);
				}
				if (!haschangge
						&& (pt.getLingliaostatus() == null || pt
								.getLingliaostatus().equals("是"))
						&& pd.getLingliaostatus() != null
						&& pd.getLingliaostatus().equals("否")) {
					// 不领料变领料
					if (pd.getStatus().equals("已发料")) {
						boolean zj = false;
						// 获取备料表信息
						List<ProcardBl> pblList = totalDao.query(
								"from ProcardBl where procardId=?", pd.getId());
						if (pblList != null && pblList.size() > 0) {
							for (ProcardBl pbl : pblList) {
								if (pd.getFilnalCount() == null
										|| pd.getFilnalCount() == 0) {
									pbl.setPcCount(nowFinalCount);
								} else {
									pbl.setPcCount(pbl.getPcCount()
											* nowFinalCount
											/ pd.getFilnalCount());
								}
								pbl.setLingliaoStatus("是");
								pbl.setYlCount(0f);
								pbl.setStatus("未领");
								zj = true;
								totalDao.update(pbl);
							}
						}
						if (pd.getProcardStyle().equals("外购") && zj) {
							List<ProcardBl> pblList2 = totalDao.query(
									"from ProcardBl where procardId=?", pd
											.getFatherId());
							if (pblList2 != null && pblList2.size() > 0) {
								for (ProcardBl pbl : pblList2) {
									pbl.setYlCount(0f);
									pbl.setStatus("未领完");
									totalDao.update(pbl);
								}
							}
						}
						pd.setLingliaostatus("是");
						pd.setStatus("初始");
						pd.setFilnalCount(nowFinalCount);
						pd.setKlNumber(nowFinalCount);
						pd.setHascount(null);
					}
				}
				if (!haschangge
						&& (pd.getLingliaostatus() == null || pd
								.getLingliaostatus().equals("是"))
						&& pt.getLingliaostatus() != null
						&& pt.getLingliaostatus().equals("否")) {
					// 领料变不领料
					if (pd.getStatus().equals("已发卡")
							|| pd.getStatus().equals("初始")) {
						// 获取备料表信息
						List<ProcardBl> pblList = totalDao.query(
								"from ProcardBl where procardId=?", pd.getId());
						if (pblList != null && pblList.size() > 0) {
							for (ProcardBl pbl : pblList) {
								if (pd.getFilnalCount() == null
										|| pd.getFilnalCount() == 0) {
									pbl.setPcCount(nowFinalCount);
								} else {
									pbl.setPcCount(pbl.getPcCount()
											* nowFinalCount
											/ pd.getFilnalCount());
								}
								pbl.setStatus("已领完");
								totalDao.update(pbl);
							}
						}
						pd.setFilnalCount(nowFinalCount);
						pd.setLingliaostatus("否");
						if (!pd.getStatus().equals("初始")) {
							pd.setStatus("已发料");
							pd.setKlNumber(nowFinalCount);
							pd.setHascount(0f);
						}
					}
				}
				if (changeyl && !haschangge
						&& !pd.getFilnalCount().equals(nowFinalCount)) {
					if (pt.getLingliaostatus() != null
							&& pt.getLingliaostatus().equals("否")) {
						pd.setHascount(0f);
					} else {
						if (pd.getHascount() != null) {
							if (pd.getKlNumber() == null) {
								pd.setHascount(nowFinalCount);
							} else {
								pd
										.setHascount(nowFinalCount
												- (pd.getKlNumber() - pd
														.getHascount()));
							}
						}
						// 获取备料表信息
						if (pd.getFilnalCount() != null
								&& pd.getFilnalCount() > 0
								&& pd.getHasPlan() != null) {
							pd.setHasPlan(pd.getHasPlan() * nowFinalCount
									/ pd.getFilnalCount());
						}
						List<ProcardBl> pblList = totalDao.query(
								"from ProcardBl where procardId=?", pd.getId());
						if (pblList != null && pblList.size() > 0) {
							for (ProcardBl pbl : pblList) {
								if (pd.getFilnalCount() == null
										|| pd.getFilnalCount() == 0) {
									pbl.setPcCount(nowFinalCount);
								} else {
									pbl.setPcCount(pbl.getPcCount()
											* nowFinalCount
											/ pd.getFilnalCount());
								}
								if (pbl.getYlCount() != null
										&& pbl.getYlCount() >= pbl.getPcCount()) {
									pbl.setStatus("已领完");
								}
								totalDao.update(pbl);
							}
						}
					}
					pd.setFilnalCount(nowFinalCount);
					pd.setKlNumber(pd.getFilnalCount());
					pd.setTjNumber(0f);
					pd.setMinNumber(0f);
				}
				if (pt.getProcardStyle() != null
						&& pt.getProcardStyle().equals("外购")) {
					if (changeyl) {
						BeanUtils.copyProperties(pt, pd, new String[] { "id",
								"rootId", "fatherId", "status", "belongLayer",
								"corrCount", "hascount", "sbStatus",
								"rootMarkId", "ywMarkId", "productStyle",
								"procardStyle" });
						pd.setQuanzi2(nowq2);
						pd.setQuanzi1(nowq1);
					} else {
						BeanUtils.copyProperties(pt, pd, new String[] { "id",
								"rootId", "fatherId", "status", "belongLayer",
								"corrCount", "hascount", "sbStatus",
								"rootMarkId", "ywMarkId", "quanzi1", "quanzi2",
								"productStyle", "procardStyle" });
					}
				} else {
					if (changeyl) {
						BeanUtils.copyProperties(pt, pd, new String[] { "id",
								"rootId", "fatherId", "status", "belongLayer",
								"hascount", "sbStatus", "rootMarkId",
								"ywMarkId", "productStyle", "procardStyle" });
					} else {
						BeanUtils.copyProperties(pt, pd, new String[] { "id",
								"rootId", "fatherId", "status", "belongLayer",
								"hascount", "sbStatus", "rootMarkId",
								"ywMarkId", "corrCount", "productStyle",
								"procardStyle" });

					}
				}
				if (pt.getProcardStyle().equals("总成")) {
					if (!pd.getProcardStyle().equals("总成")) {
						pd.setProcardStyle("自制");
					}
				} else if (!pd.getProcardStyle().equals("总成")) {
					pd.setProcardStyle(pt.getProcardStyle());
				}

			} else {// 已发料之后的状态
				if (pt.getProcardStyle() != null
						&& pt.getProcardStyle().equals("外购")
						&& pd.getNeedProcess() != null
						&& pd.getNeedProcess().equals("yes")
						&& (pt.getNeedProcess() == null || pt.getNeedProcess()
								.equals("no"))) {// 外购件原需要工序，现不要工序
					if (pt.getLingliaostatus() != null
							&& pt.getLingliaostatus().equals("否")) {
						pd.setHascount(0f);
					} else {
						if (pd.getHascount() != null) {
							if (pd.getKlNumber() == null) {
								pd.setHascount(nowFinalCount);
							} else {
								pd
										.setHascount(nowFinalCount
												- (pd.getKlNumber() - pd
														.getHascount()));
							}
						}
						// 获取备料表信息
						List<ProcardBl> pblList = totalDao.query(
								"from ProcardBl where procardId=?", pd.getId());
						if (pblList != null && pblList.size() > 0) {
							for (ProcardBl pbl : pblList) {
								if (pd.getFilnalCount() == null
										|| pd.getFilnalCount() == 0) {
									pbl.setPcCount(nowFinalCount);
								} else {
									pbl.setPcCount(pbl.getPcCount()
											* nowFinalCount
											/ pd.getFilnalCount());
								}
								if (pbl.getYlCount() != null
										&& pbl.getYlCount() >= pbl.getPcCount()) {
									pbl.setStatus("已领完");
								}
								totalDao.update(pbl);
							}
						}
					}
					Float ylCount = 0f;// 已经领取的数量
					if (pd.getKlNumber() != null) {
						ylCount = pd.getKlNumber() - pd.getHascount();
					}
					String sumOld = "select sum(hascount) from Procard where status ='已发卡' and markId=?";
					Float blCount = (Float) totalDao
							.getObjectByCondition(
									"select sum(wwblCount) from Procard where status ='已发卡' and markId=?",
									pt.getMarkId());
					if (blCount == null) {
						blCount = 0f;
					}
					Object sumobj = null;
					sumobj = totalDao.getObjectByCondition(sumOld, pt
							.getMarkId());
					Float sumoldCount = 0F;
					if (sumobj != null) {
						sumoldCount = Float.parseFloat(sumobj.toString());
					}
					Float sumAll = sumoldCount - blCount;
					// 查询库存数量
					Object obj = totalDao
							.getObjectByCondition(
									"select sum(goodsCurQuantity) from Goods where goodsMarkId=? and goodsUnit=?",
									pt.getMarkId(), pt.getUnit());
					Float sumCount = 0F;
					if (obj != null) {
						sumCount = Float.parseFloat(obj.toString());
					}
					if (sumCount == null) {
						sumCount = Float
								.parseFloat(totalDao
										.getObjectByCondition(
												"select sum(goodsZhishu) from Goods where goodsMarkId=?",
												pt.getMarkId()).toString());
					}
					// 判断数量是否足够
					pd.setJihuoStatua("激活");
					pd.setFilnalCount(nowFinalCount);
					pd.setKlNumber(pd.getFilnalCount());
					pd.setTjNumber(ylCount);
					pd.setMinNumber(0F);
					if (sumCount != null && sumCount > 0) {
						// 数量充足
						if (sumCount - sumoldCount >= 0) {
							if (sumCount - sumAll >= 0) {
								pd.setTjNumber(pd.getFilnalCount());
							} else {
								// 数量不足，
								pd.setTjNumber(ylCount + sumAll - sumCount);
							}

						}
						if (pd.getTjNumber() > pd.getFilnalCount()) {
							pd.setTjNumber(pd.getFilnalCount());
						}
						Float minNumber = null;
						if (changeyl) {
							minNumber = pd.getTjNumber() / pt.getQuanzi2()
									* pt.getQuanzi1();// pt代表最新的权值
						} else {
							minNumber = pd.getTjNumber() / pd.getQuanzi2()
									* pd.getQuanzi1();// pt代表最新的权值
						}
						if (pd.getTjNumber() == pd.getFilnalCount()) {
							minNumber = (float) Math.ceil(minNumber);
						}
						pd.setMinNumber(minNumber);
					}

				} else if (pt.getProcardStyle() != null
						&& pt.getProcardStyle().equals("外购")
						&& pt.getNeedProcess() != null
						&& pt.getNeedProcess().equals("yes")
						&& (pd.getNeedProcess() == null || pd.getNeedProcess()
								.equals("no"))) {// 外购件原不需要工序，现要工序
					pd.setTjNumber(0f);
					pd.setMinNumber(0f);
				}
				if ((pd.getLingliaostatus() == null || pd.getLingliaostatus()
						.equals("是"))
						&& pt.getLingliaostatus() != null
						&& pt.getLingliaostatus().equals("否")) {
					// 领料变不领料
					if (pd.getStatus().equals("已发卡")) {
						pd.setLingliaostatus("否");
						pd.setStatus("已发料");
						pd.setKlNumber(nowFinalCount);
						pd.setHascount(0f);
					}

				}

			}
		} else {
			if (pd.getHascount() != null) {
				if (pd.getKlNumber() == null) {
					pd.setHascount(nowFinalCount);
				} else {
					Float hascount = nowFinalCount
							- (pd.getKlNumber() - pd.getHascount());
					if (hascount < 0) {
						hascount = 0f;
					}
					pd.setHascount(hascount);
				}
				pd.setKlNumber(nowFinalCount);
			}
			if (pd.getTjNumber() == null) {
				pd.setTjNumber(0f);
			}
			if (pd.getProcardStyle().equals("外购")) {
				pd.setMinNumber(pd.getTjNumber() * pd.getQuanzi1()
						/ pd.getQuanzi2());
			} else {
				if (pd.getProcardStyle().equals("总成")) {
					if (pd.getCorrCount() == null)
						pd.setCorrCount(1f);
				}
				pd.setMinNumber(pd.getTjNumber() / pd.getCorrCount());
			}
		}
		pd.setSbId(ptbbApplyId);
		pd.setSbNumber(sbNumber);
		pd.setHasChange("是");
		Set<ProcessInfor> processSet = pd.getProcessInforSet();
		Set<ProcessTemplate> processtSet = pt.getProcessTemplate();
		// 工序比对处理
		compareProcess(ptbbApplyId, sbNumber, processSet, processtSet,
				processabbSet, pd, nowTime, nowbfCount, sameBanci, clType);
		Set<Procard> sonSet = pd.getProcardSet();
		Set<ProcardTemplate> ptsonSet = pt.getProcardTSet();
		List<Integer> hasId = new ArrayList<Integer>();
		List<Integer> hasId2 = new ArrayList<Integer>();
		if (sonSet != null && sonSet.size() > 0) {
			if (ptsonSet != null && ptsonSet.size() > 0) {
				for (ProcardTemplate ptson : ptsonSet) {
					if (ptson.getBanbenStatus() != null
							&& ptson.getBanbenStatus().equals("历史")) {
						continue;
					}
					if (ptson.getDataStatus() != null
							&& ptson.getDataStatus().equals("删除")) {
						// 若上层被删除则不需要处理，若上层没有被删除这下层不会有删除数据
						continue;
					}
					String oldmarkId = ptson.getMarkId();
					if (markidMap != null) {// 件号替换map
						String newmarkId = markidMap.get(oldmarkId);
						if (newmarkId != null && newmarkId.length() > 0) {
							if (sbmarkIds.contains(oldmarkId)
									&& !sbmarkIds.contains(newmarkId)) {
								sbmarkIds.add(newmarkId);
							}
							oldmarkId = newmarkId;
						}
					}
					for (Procard son : sonSet) {
						if (son.getSbStatus() != null
								&& son.getSbStatus().equals("删除")) {
							continue;
						}
						if ((son.getProcardTemplateId() != null && son
								.getProcardTemplateId().equals(ptson.getId()))
								|| oldmarkId.equals(son.getMarkId())) {
							hasId.add(son.getId());
							hasId2.add(ptson.getId());
							ProcardAboutBanBenApply sonpAbb = null;
							for (ProcardAboutBanBenApply sonpabb2 : procardAboutBanBenApplyList) {
								if (sonpabb2.getProcardId().equals(son.getId())) {
									sonpAbb = sonpabb2;
									break;
								}
							}
							boolean wqbt = false;
							if (sbwqtb != null && sbwqtb.equals("是")) {
								wqbt = true;
							} else {
								wqbt = sbmarkIds.contains(son.getMarkId());
							}
							if (son.getThProcardId() == null && wqbt) {
								updateProcard(ptbbApplyId, sbNumber, ptson,
										son, nowFinalCount, nowbfCount,
										totalWlStatus, sonpAbb,
										procardAboutBanBenApplyList, nowTime,
										sbmarkIds, user, true, sbwqtb,
										markidMap, syProcardIds, nsyProcardIds);
							} else if (changeCount < -0.0001
									|| changeCount > 0.0001) {// 上层用量发生改变修改下层用量
								justUpdateProcardCount(ptbbApplyId, sbNumber,
										son, pd.getFilnalCount(), nowbfCount,
										nowTime);
							}
						}
					}
				}
				List<String> thMarkidList = new ArrayList<String>();// 替换件号，不处理
				for (Procard son : sonSet) {
					if (son.getThProcardId() != null) {
						String markId = (String) totalDao.getObjectByCondition(
								"select markId from Procard where id =?", son
										.getThProcardId());
						if (markId != null && !thMarkidList.contains(markId)) {
							thMarkidList.add(markId);
						}
					}
				}
				boolean add = false;
				// 模板新加
				for (ProcardTemplate ptson : ptsonSet) {
					if (ptson.getBanbenStatus() != null
							&& ptson.getBanbenStatus().equals("历史")) {
						continue;
					}
					if (ptson.getDataStatus() != null
							&& ptson.getDataStatus().equals("删除")) {
						continue;
					}
					boolean wqbt = false;
					if (sbwqtb != null && sbwqtb.equals("是")) {
						wqbt = true;
					} else {
						wqbt = sbmarkIds.contains(ptson.getMarkId());
					}
					if (!hasId2.contains(ptson.getId()) && wqbt
							&& !thMarkidList.contains(ptson.getMarkId())) {
						// 获取备料表信息
						List<ProcardBl> pblList = totalDao.query(
								"from ProcardBl where procardId=?", pd.getId());
						if (pblList != null && pblList.size() > 0) {
							for (ProcardBl pbl : pblList) {
								if (pd.getFilnalCount() == null
										|| pd.getFilnalCount() == 0) {
									pbl.setPcCount(nowFinalCount);
								} else {
									pbl.setPcCount(pbl.getPcCount()
											* nowFinalCount / oldFilnalCount);
								}
								if (pbl.getYlCount() != null
										&& pbl.getYlCount() >= pbl.getPcCount()) {
									pbl.setStatus("已领完");
								}
								totalDao.update(pbl);
							}
						}
						addProcardInFather(ptbbApplyId, sbNumber, pd, null,
								ptson, totalWlStatus, 0);
						add = true;
						pd.setHascount(pd.getKlNumber());
					}
				}
				if (add) {
					if (!pt.getProcardStyle().equals("外购")) {
						pd.setHascount(pd.getKlNumber());
						List<ProcardBl> pblList2 = totalDao
								.query(
										"from ProcardBl where procardId=? and ylCount>0",
										pd.getId());
						if (pblList2 != null && pblList2.size() > 0) {
							for (ProcardBl pbl : pblList2) {
								pbl.setYlCount(0f);
								pbl.setStatus("未领完");
								totalDao.update(pbl);
							}
						}
					}
				}
				// 模板删除
				for (Procard son : sonSet) {
					if (son.getSbStatus() != null
							&& son.getSbStatus().equals("删除")) {
						continue;
					}
					boolean wqbt = false;
					if (sbwqtb != null && sbwqtb.equals("是")) {
						wqbt = true;
					} else {
						wqbt = sbmarkIds.contains(son.getMarkId());
					}
					if (!hasId.contains(son.getId()) && wqbt
							&& !thMarkidList.contains(son.getMarkId())) {
						son.setSbStatus("删除");
						if (son.getFatherId() != null) {
							son.setOldFatherId(son.getFatherId());
							son.setOldRootId(son.getRootId());
						}
						son.setSbId(ptbbApplyId);
						son.setSbNumber(sbNumber);
						son.setFatherId(null);
						son.setRootId(null);
						Float scgxCount = 0f;
						if (son.getProcardStyle().equals("外购")) {
							if (son.getKlNumber() != null
									&& son.getHascount() != null
									&& son.getKlNumber() > son.getHascount()) {
								// 生成报废待处理在制品
								addProcardSbWaster(ptbbApplyId, sbNumber, pd,
										son.getKlNumber() - son.getHascount(),
										nowTime, null, "外购件", clType);
								sbData++;
							}
							// 处理外购订单
							addProcardSbWg(ptbbApplyId, sbNumber, son, -son
									.getFilnalCount(), nowTime);
							ProcardSbWwDetail psbwwd = addProcardSbWwDetail(pd);
							String msg = breakWWBysb(pd, ptbbApplyId, sbNumber,
									psbwwd);
							sbData++;
						} else {
							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											son.getId());
							for (int j = 0; j < processList.size(); j++) {
								float zzCount = 0f;
								ProcessInfor process = processList.get(j);
								if (j == 0) {
									scgxCount = process.getApplyCount();
								}
								if (process.getApplyCount() == 0) {
									continue;
								}
								// 此工序已领单未提交数量
								zzCount += process.getApplyCount()
										- process.getSubmmitCount();
								if ((j + 1) <= (processList.size() - 1)) {
									ProcessInfor process2 = processList
											.get((j + 1));
									// 此道工序做完未流转到下道工序的数量
									zzCount += process.getSubmmitCount()
											- process2.getApplyCount();
									if (zzCount < 0) {
										zzCount = 0;
									}
								} else {
									zzCount += process.getSubmmitCount();
								}
								if (zzCount > 0) {// 查询此工序当前的半成品数量
									if (zzCount > 0) {
										// 生成报废待处理在制品
										addProcardSbWaster(ptbbApplyId,
												sbNumber, pd, zzCount, nowTime,
												process.getProcessNO(), process
														.getProcessName(),
												clType);
										sbData++;
									}
								}
							}
						}
						// 遍历下层零件设置设变属性为删除
						Set<Procard> sonSet2 = son.getProcardSet();
						if (sonSet2 != null && sonSet2.size() > 0) {
							for (Procard son2 : sonSet2) {
								deleteDownProcard(ptbbApplyId, sbNumber, son2,
										nowTime, scgxCount, clType);
							}
						}
						totalDao.update(son);
					}
				}
			} else {
				// 删除下层
				for (Procard son : sonSet) {
					son.setSbId(ptbbApplyId);
					son.setSbNumber(sbNumber);
					son.setSbStatus("删除");
					if (son.getFatherId() != null) {
						son.setOldFatherId(son.getFatherId());
						son.setOldRootId(son.getRootId());
					}
					son.setFatherId(null);
					son.setRootId(null);
					Float scgxCount = 0f;
					if (son.getProcardStyle().equals("外购")) {
						if ((son.getWwblCount() == null || son.getWwblCount() == 0)
								&& son.getKlNumber() != null
								&& son.getHascount() != null
								&& son.getKlNumber() > son.getHascount()) {
							// 生成外购件退库数据
							GoodsStore gs = new GoodsStore();
							gs.setGoodsStoreWarehouse("外购件库");// 库别
							gs.setGoodsStoreMarkId(pd.getMarkId());// 件号
							gs.setBanBenNumber(pd.getBanBenNumber());// 版本号
							gs.setKgliao(pd.getKgliao());// 供料属性
							gs.setGoodsStoreLot("ECN" + pd.getSelfCard());// 批次
							gs.setGoodsStoreGoodsName(pd.getProName());// 名称
							gs.setGoodsStoreFormat(pd.getSpecification());// 规格
							gs.setWgType(pd.getWgType());// 物料类别
							gs.setGoodsStoreUnit(pd.getUnit());// 单位
							gs.setGoodsStoreCount(Util.Floatdelete(son
									.getKlNumber(), son.getHascount()));// 数量
							gs.setGoodsStoreDate(nowTime.substring(0, 10));// 时间
							gs.setGoodsStoreTime(nowTime);// 系统默认入库时间
							gs.setGoodsStorePerson(user.getName());// 负责人
							gs.setGoodsStoreGoodsMore(sbNumber);// 备注
							// gs.setGoodsStoreAdminId(user.getId());// 管理员ID
							// gs.setgoodsStoreAdminName;// 管理员姓名
							gs.setGoodsStoreCharger(user.getName());// 办理人
							gs.setGoodsStorePlanner(user.getName());// 入库人
							gs.setGoodsStoreArtsCard(pd.getMarkId());// 工艺卡片号
							gs.setGoodsStoreProMarkId(pd.getRootMarkId());// 总成件号
							gs.setTuhao(pd.getTuhao());// 图号(别称)
							gs.setNeiorderId(pd.getOrderNumber());// 内部订单号;
							gs.setWaiorderId(pd.getOutOrderNum());// 外部订单号;
							gs.setInputSource("设变退库");// 入库来源（手动入库(来源为手动入库时出库关联的订单id为order_Id)，生产入库(默认为生产入库)）
							gs.setStatus("待入库");// 状态（待入库/入库）
							gs.setStyle("设变退库");// 入库类型（领用、可借用。
							// /返修入库/退货入库/批产/试制/中间件）（冲销转库和半成品转库为特殊入库类型与功能相关慎用）
							gs.setApplyTime(nowTime);// 申请入库时间
							gs.setPrintStatus("NO");// 打印状态(YES/NO)
							gs.setSqUsersName(user.getName());// 申请入库人;
							gs.setSqUsrsId(user.getId());// 申请人姓名
							gs.setSqUsersCode(user.getCode());// 申请人工号
							gs.setSqUsersdept(user.getDept());// 申请人部门
							totalDao.save(gs);
							// addProcardSbWaster(ptbbApplyId, sbNumber, pd, son
							// .getKlNumber()
							// - son.getHascount(), nowTime, null, "外购件",
							// clType);
							sbData++;
						}
						// 处理外购订单
						addProcardSbWg(ptbbApplyId, sbNumber, son, -son
								.getFilnalCount(), nowTime);
						sbData++;
					} else {
						List<ProcessInfor> processList = totalDao
								.query(
										"from ProcessInfor where procard.id=? order by processNO",
										son.getId());
						for (int j = 0; j < processList.size(); j++) {
							float zzCount = 0f;
							ProcessInfor process = processList.get(j);
							if (j == 0) {
								scgxCount = process.getApplyCount();
							}
							if (process.getApplyCount() == 0) {
								continue;
							}
							// 此工序已领单未提交数量
							zzCount += process.getApplyCount()
									- process.getSubmmitCount();
							if ((j + 1) <= (processList.size() - 1)) {
								ProcessInfor process2 = processList
										.get((j + 1));
								// 此道工序做完未流转到下道工序的数量
								zzCount += process.getSubmmitCount()
										- process2.getApplyCount();
								if (zzCount < 0) {
									zzCount = 0;
								}
							} else {
								zzCount += process.getSubmmitCount();
							}
							if (zzCount > 0) {// 查询此工序当前的半成品数量
								if (zzCount > 0) {
									// 生成报废待处理在制品
									addProcardSbWaster(ptbbApplyId, sbNumber,
											pd, zzCount, nowTime, process
													.getProcessNO(), process
													.getProcessName(), clType);
									sbData++;
								}
							}
						}
					}
					ProcardSbWwDetail psbwwd = addProcardSbWwDetail(pd);
					String msg = breakWWBysb(pd, ptbbApplyId, sbNumber, psbwwd);
					// 遍历下层零件设置设变属性为删除
					Set<Procard> sonSet2 = son.getProcardSet();
					if (sonSet2 != null && sonSet2.size() > 0) {
						for (Procard son2 : sonSet2) {
							deleteDownProcard(ptbbApplyId, sbNumber, son2,
									nowTime, scgxCount, clType);
						}
					}
					totalDao.update(son);
				}
			}
		} else if (ptsonSet != null && ptsonSet.size() > 0) {
			// List<String> thMarkidList = new ArrayList<String>();// 替换件号，不处理
			// for (Procard son : sonSet) {
			// if (son.getThProcardId() != null) {
			// String markId = (String) totalDao.getObjectByCondition(
			// "select markId from Procard where id =?", son
			// .getThProcardId());
			// if (markId != null && !thMarkidList.contains(markId)) {
			// thMarkidList.add(markId);
			// }
			// }
			// }
			// 模板新加
			for (ProcardTemplate ptson : ptsonSet) {
				if (ptson.getBanbenStatus() != null
						&& ptson.getBanbenStatus().equals("历史")) {
					continue;
				}
				if (ptson.getDataStatus() != null
						&& ptson.getDataStatus().equals("删除")) {
					continue;
				}
				boolean wqbt = false;
				if (sbwqtb != null && sbwqtb.equals("是")) {
					wqbt = true;
				} else {
					wqbt = sbmarkIds.contains(ptson.getMarkId());
				}
				if (!hasId2.contains(ptson.getId()) && wqbt) {
					addProcardInFather(ptbbApplyId, sbNumber, pd, null, ptson,
							totalWlStatus, 0);
					if (pt.getProcardStyle().equals("外购")
							&& (pt.getLingliaostatus() == null || !pt
									.getLingliaostatus().equals("否"))) {
						pd.setHascount(pd.getKlNumber());
						List<ProcardBl> pblList2 = totalDao
								.query(
										"from ProcardBl where procardId=? and ylCount>0",
										pd.getId());
						if (pblList2 != null && pblList2.size() > 0) {
							for (ProcardBl pbl : pblList2) {
								pbl.setYlCount(0f);
								pbl.setStatus("未领完");
								totalDao.update(pbl);
							}
						}
					}
					pd.setHascount(pd.getKlNumber());
				}
			}
		}
		if (pd.getSbStatus() == null || !pd.getSbStatus().equals("删除")) {
			pd.setBanci(pt.getBanci());
			pd.setBanBenNumber(pt.getBanBenNumber());
		}
		if (pd.getStatus().equals("完成")) {
			if (pd.getProcardStyle().equals("外购")) {
				if (pd.getHascount() == null || pd.getHascount() > 0) {
					pd.setStatus("已发卡");
				}
			} else {
				if (pd.getTjNumber() == null
						|| pd.getTjNumber() < pd.getFilnalCount()) {
					pd.setStatus("已发卡");
				}
			}
		}
		totalDao.update(pd);
		if (pAbb != null) {
			pAbb.setStatus("完成");
			totalDao.update(pAbb);
		}
		return b;
	}

	private void updategxcount(Procard pd, Float filnalCount,
			Float nowFinalCount) {
		// TODO Auto-generated method stub
		List<ProcessInfor> processinforList = totalDao
				.query(
						"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除')",
						pd.getId());
		if (processinforList != null && processinforList.size() > 0) {
			for (ProcessInfor processinfor : processinforList) {
				try {
					Float nowtotalCount = Util.Floatdiv(processinfor
							.getTotalCount()
							* nowFinalCount, filnalCount, 4);
					if (nowtotalCount < processinfor.getApplyCount()) {
						nowtotalCount = processinfor.getApplyCount();
					}
					int t = 0;
					if (nowtotalCount % 1 > 0.95) {
						t = (int) (float) Math.ceil(nowtotalCount);
					} else {
						t = (int) (float) Math.floor(nowtotalCount);
					}
					processinfor.setTotalCount(t);
					if (processinfor.getStatus().equals("完成")) {
						if (processinfor.getSubmmitCount() < nowFinalCount) {
							processinfor.setStatus("自检");
						}
					}
					if (nowFinalCount.equals(processinfor.getSubmmitCount())) {
						processinfor.setStatus("完成");
					}
					totalDao.update(processinfor);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * 工序比对处理
	 * 
	 * @param ptbbApplyId
	 *            设变申请单id
	 * @param processSet3
	 *            生产工序
	 * @param processSet1
	 *            工艺BOM工序
	 * @param processabbSet
	 *            生产工序对设变处理
	 * @param pd
	 *            生产件
	 */
	private void compareProcess(Integer ptbbApplyId, String sbNumber,
			Set<ProcessInfor> processSet, Set<ProcessTemplate> processTSet,
			Set<ProcessAboutBanBenApply> processabbSet, Procard pd,
			String nowTime, Float nowbfCount, boolean samebanci, String clType) {
		// TODO Auto-generated method stub
		// 第一步处理设变判定结果
		if (processSet != null && processSet.size() > 0) {
			for (ProcessInfor process : processSet) {
				if (process.getDataStatus() != null
						&& process.getDataStatus().equals("删除")) {
					continue;
				}
				if (nowbfCount != null && nowbfCount > 0) {// 扣减报废数量
					process.setTotalCount(process.getTotalCount() - nowbfCount);
					process.setApplyCount(process.getApplyCount() - nowbfCount);
					process.setSubmmitCount(process.getSubmmitCount()
							- nowbfCount);
					if (process.getTotalCount() < 0) {
						process.setTotalCount(0f);
					}
					if (process.getApplyCount() < 0) {
						process.setApplyCount(0f);
					}
					if (process.getSubmmitCount() < 0) {
						process.setSubmmitCount(0f);
					}
					if (process.getStatus().equals("完成")) {
						process.setStatus("自检");
					}
				}
				if (processabbSet != null && processabbSet.size() > 0) {
					for (ProcessAboutBanBenApply processabb : processabbSet) {
						if (processabb.getProcessNo() != null) {
							if (processabb.getProcessNo() > process
									.getProcessNO()) {
								if (processabb.getClType().equals("报废重产")) {
									// 扣去已报废的
									Float ccCount = 0f;
									if (nowbfCount == null) {
										ccCount = processabb.getScCount();
									} else if (nowbfCount < processabb
											.getScCount()) {
										ccCount = processabb.getScCount()
												- nowbfCount;
									}
									if (ccCount > 0) {
										// 去除已生产的数量
										process.setTotalCount(process
												.getTotalCount()
												- ccCount);
										process.setApplyCount(process
												.getApplyCount()
												- ccCount);
										process.setSubmmitCount(process
												.getSubmmitCount()
												- ccCount);
										if (process.getTotalCount() < 0) {
											process.setTotalCount(0f);
										}
										if (process.getApplyCount() < 0) {
											process.setApplyCount(0f);
										}
										if (process.getSubmmitCount() < 0) {
											process.setSubmmitCount(0f);
										}
										if (process.getStatus().equals("完成")) {
											process.setStatus("自检");
										}
									}
									// 去除对应数量的外委记录
									Float wwCount = processabb.getScCount();
									if (wwCount > 0
											&& process.getAgreeWwCount() != null) {
										if (process.getAgreeWwCount() > wwCount) {
											process.setAgreeWwCount(process
													.getAgreeWwCount()
													- wwCount);
											wwCount = 0f;
										} else {
											wwCount -= process
													.getAgreeWwCount();
											process.setAgreeWwCount(0f);
										}
									}
									if (wwCount > 0
											&& process.getApplyWwCount() != null) {
										if (process.getApplyWwCount() > wwCount) {
											process.setApplyWwCount(process
													.getApplyWwCount()
													- wwCount);
											wwCount = 0f;
										} else {
											wwCount -= process
													.getApplyWwCount();
											process.setApplyWwCount(0f);
										}
									}
									if (wwCount > 0
											&& process.getSelectWwCount() != null) {
										if (process.getSelectWwCount() > wwCount) {
											process.setSelectWwCount(process
													.getSelectWwCount()
													- wwCount);
											wwCount = 0f;
										} else {
											wwCount -= process
													.getSelectWwCount();
											process.setSelectWwCount(0f);
										}
									}
									totalDao.update(process);
								}
							} else if (processabb.getProcessNo().equals(
									process.getProcessNO())) {
								if (processabb.getClType().equals("报废重产")) {
									// 去除已生产的数量
									Float ccCount = 0f;
									if (nowbfCount == null) {
										ccCount = processabb.getScCount();
									} else if (nowbfCount < processabb
											.getScCount()) {
										ccCount = processabb.getScCount()
												- nowbfCount;
									}
									if (ccCount > 0) {
										process.setTotalCount(process
												.getTotalCount()
												- ccCount);
										process.setApplyCount(process
												.getApplyCount()
												- ccCount);
										if (process.getTotalCount() < 0) {
											process.setTotalCount(0f);
										}
										if (process.getApplyCount() < 0) {
											process.setApplyCount(0f);
										}
										// 因为可能有已领未提交的
										process.setSubmmitCount(process
												.getApplyCount());
										if (process.getStatus().equals("完成")) {
											process.setStatus("自检");
										}
										totalDao.update(process);
										// 生成报废待处理在制品
										addProcardSbWaster(ptbbApplyId,
												sbNumber, pd, ccCount, nowTime,
												process.getProcessNO(), process
														.getProcessName(),
												clType);
									}
								} else if (processabb.getClType().equals(
										"更新(返修)")) {
									Float fxCount = 0f;
									if (nowbfCount == null) {
										fxCount = processabb.getScCount();
									} else if (nowbfCount < processabb
											.getScCount()) {
										fxCount = processabb.getScCount()
												- nowbfCount;
									} else {
										fxCount = 0f;
									}
									if (fxCount > 0) {
										// process.setTotalCount(process
										// .getTotalCount()
										// - fxCount);
										// process.setApplyCount(process
										// .getApplyCount()
										// - fxCount);
										// process.setSubmmitCount(process
										// .getSubmmitCount()
										// - fxCount);
										// 下挂返修单
										addProcardReProduct(ptbbApplyId,
												sbNumber, pd, fxCount, nowTime,
												process);
										// if(process.getStatus().equals("完成")||process.getStatus().equals("已领")){
										// process.setStatus("自检");
										// }
										// totalDao.update(process);
									}
								}
							}
						}
					}
				}
			}
			if (!samebanci) {
				// 第二步比对同工序号
				List<ProcessTemplate> addProcessTList = new ArrayList<ProcessTemplate>();
				List<Integer> processtNos = new ArrayList<Integer>();
				List<Integer> hadbdptIdList = new ArrayList<Integer>();
				if (processTSet != null && processTSet.size() > 0) {
					for (ProcessTemplate processt : processTSet) {
						if (processt.getDataStatus() != null
								&& processt.getDataStatus().equals("删除")) {
							continue;
						}
						processtNos.add(processt.getProcessNO());
						boolean has = false;
						if (processSet != null && processSet.size() > 0) {
							for (ProcessInfor process : processSet) {
								if (process.getDataStatus() != null
										&& process.getDataStatus().equals("删除")) {
									continue;
								}
								if (processt.getProcessNO().equals(
										process.getProcessNO())) {
									if (processt.getProcessName().equals(
											process.getProcessName())) {
										// 不变
										hadbdptIdList.add(processt.getId());
										process.setProcessTemplateId(processt.getId());
										has = true;
									} else if (process.getProcessTemplateId() != null
											&& process.getProcessTemplateId()
													.equals(processt.getId())) {
										// 工序名称发生改变
										process.setProcessName(processt
												.getProcessName());
										process.setNeedSave(processt
												.getNeedSave());
										process.setProcessTemplateId(processt
												.getId());
										process.setGuifanstatus(processt
												.getGuifanstatus());
										totalDao.update(process);
										hadbdptIdList.add(processt.getId());
										has = true;
									} else {
										if (process.getProcessTemplateId() != null) {
											ProcessTemplate hasProcessT = (ProcessTemplate) totalDao
													.getObjectById(
															ProcessTemplate.class,
															process.getId());
											if (hasProcessT != null) {
												if (processt.getDataStatus() != null
														&& processt
																.getDataStatus()
																.equals("删除")) {
													// 表示此工序被替换了
													process.setDataStatus("删除");
													totalDao.update(process);
												} else {
													process
															.setProcessNO(hasProcessT
																	.getProcessNO());
													process
															.setProcessName(hasProcessT
																	.getProcessName());
													process
															.setShebeistatus(hasProcessT
																	.getShebeistatus());// 设备验证
													process
															.setGongzhuangstatus(hasProcessT
																	.getGongzhuangstatus());// 工装验证
													process
															.setLiangjustatus(hasProcessT
																	.getLiangjustatus());// 量具验证
													process
															.setGuifanstatus(hasProcessT
																	.getGuifanstatus());// 规范验证
													process
															.setKaoqingstatus(hasProcessT
																	.getKaoqingstatus());// 考勤验证
													process
															.setNeedSave(hasProcessT
																	.getNeedSave());
													totalDao.update(process);
												}
											} else {
												// 表示此工序被替换了
												process.setDataStatus("删除");
												totalDao.update(process);
											}
										}
									}
								}
							}

						}
						if (!has && !hadbdptIdList.contains(processt.getId())) {
							addProcessTList.add(processt);
						}
					}
				}
				// 第三步去除BOM中没有的工序
				if (processSet != null && processSet.size() > 0) {
					for (ProcessInfor process : processSet) {
						if (process.getDataStatus() != null
								&& process.getDataStatus().equals("删除")) {
							continue;
						}
						if (!processtNos.contains(process.getProcessNO())) {
							process.setDataStatus("删除");
							totalDao.update(process);
						}
					}
				}
				// 第四步添加模板中新加的工序
				if (addProcessTList.size() > 0) {
					for (ProcessTemplate processt : addProcessTList) {
						ProcessInfor newProcess = new ProcessInfor();
						BeanUtils.copyProperties(processt, newProcess,
								new String[] { "id" });
						newProcess.setProcessTemplateId(processt.getId());
						// -----------------辅料开始------------------
						if (processt.getIsNeedFuliao() != null
								&& processt.getIsNeedFuliao().equals("yes")) {
							newProcess.setIsNeedFuliao("yes");
							Set<ProcessFuLiaoTemplate> fltmpSet = processt
									.getProcessFuLiaoTemplate();
							if (fltmpSet.size() > 0) {
								Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
								for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
									ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
									BeanUtils.copyProperties(fltmp, pinfoFl,
											new String[] { "id" });
									if (pinfoFl.getQuanzhi1() == null) {
										pinfoFl.setQuanzhi1(1f);
									}
									if (pinfoFl.getQuanzhi2() == null) {
										pinfoFl.setQuanzhi2(1f);
									}
									pinfoFl.setTotalCount(pd.getFilnalCount()
											* pinfoFl.getQuanzhi2()
											/ pinfoFl.getQuanzhi1());
									pinfoFl.setProcessInfor(newProcess);
									pinfoFl.setOutCount(0f);
									pinfoFlSet.add(pinfoFl);
								}
								newProcess.setProcessinforFuLiao(pinfoFlSet);
							}
						}
						// ------------辅料结束-----
						Float klCount = 0f;// 可领数量
						Integer chaju = 0;// 工序号的差距
						if (processSet != null) {
							for (ProcessInfor process37 : processSet) {// 寻找最近工序号的总数量和可领数量优先选择上条工序
								if (process37.getProcessNO() != null
										&& processt.getProcessNO() != null) {
									Integer chaju1 = process37.getProcessNO()
											- processt.getProcessNO();
									if ((chaju == 0)
											|| (chaju > 0 && chaju1 < 0)
											|| (chaju1 > 0 && chaju1 < chaju)
											|| (chaju1 < 0 && chaju1 > chaju)) {
										chaju = chaju1;
										klCount = process37.getTotalCount();
									}
								}
							}
						}
						newProcess.setTotalCount(klCount);
						newProcess.setStatus("初始");
						newProcess.setProcard(pd);
						if (pd.getStatus().equals("完成")) {
							pd.setStatus("领工序");
						}
						pd.setTjNumber(0f);
						pd.setMinNumber(0f);
						totalDao.save(newProcess);
						processSet.add(newProcess);

					}
					pd.setProcessInforSet(processSet);
					totalDao.update(pd);
				}
			}

		}
	}

	// 仅仅调整下层数量数量
	private void justUpdateProcardCount(Integer ptbbApplyId, String sbNumber,
			Procard son, Float filnalCount, Float bfCount, String time) {
		// TODO Auto-generated method stub
		Float nowFilnalCount = null;
		Float nowbfCount = 0f;
		Float oldFilnalCount = son.getFilnalCount();
		if (son.getProcardStyle().equals("外购")) {
			if (bfCount != null) {
				nowbfCount = bfCount * son.getQuanzi2() / son.getQuanzi1();
			}
		} else {
			if (bfCount != null) {
				nowbfCount = bfCount * son.getCorrCount();
				nowbfCount = (float) Math.ceil(nowbfCount);
			}
		}
		if (bfCount == null) {
			bfCount = 0f;
		}
		if (nowbfCount > 0) {
			if (son.getLingliaostatus() == null
					|| !son.getLingliaostatus().equals("否")) {
				if (son.getHascount() != null) {
					son.setHascount(son.getHascount() + nowbfCount);
					if (son.getHascount() > son.getKlNumber()) {
						son.setHascount(son.getKlNumber());
					}
				}
			}
			if (son.getTjNumber() != null) {
				son.setTjNumber(son.getTjNumber() - nowbfCount);
				if (son.getTjNumber() < 0) {
					son.setTjNumber(0f);
				}
			}
			if (!son.getStatus().equals("初始")) {
				if (son.getHascount() == null) {
					son.setStatus("已发卡");
				} else {
					son.setStatus("领工序");
				}
			}
		}
		if (son.getProcardStyle().equals("外购")) {
			nowFilnalCount = filnalCount * son.getQuanzi2() / son.getQuanzi1();
			if ((son.getLingliaostatus() == null || !son.getLingliaostatus()
					.equals("否"))
					&& son.getKlNumber() != null && son.getHascount() != null) {
				Float hascount = nowFilnalCount
						- (son.getKlNumber() - son.getHascount());
				if (hascount < 0) {
					hascount = 0f;
				}
				son.setHascount(hascount);
			}
			son.setKlNumber(nowFilnalCount);
		} else {
			nowFilnalCount = filnalCount * son.getCorrCount();
		}
		if (!son.getFilnalCount().equals(nowFilnalCount)) {
			// 获取备料表信息
			if (son.getFilnalCount() != null && son.getFilnalCount() > 0
					&& son.getHasPlan() != null) {
				son.setHasPlan(son.getHasPlan() * nowFilnalCount
						/ son.getFilnalCount());
			}
			List<ProcardBl> pblList = totalDao.query(
					"from ProcardBl where procardId=?", son.getId());
			boolean zj = false;
			if (pblList != null && pblList.size() > 0) {
				for (ProcardBl pbl : pblList) {
					if (son.getFilnalCount() == null
							|| son.getFilnalCount() == 0) {
						pbl.setPcCount(nowFilnalCount);
					} else {
						pbl.setPcCount(pbl.getPcCount() * nowFilnalCount
								/ oldFilnalCount);
					}
					if (pbl.getYlCount() != null
							&& pbl.getYlCount() >= pbl.getPcCount()) {
						pbl.setStatus("已领完");
					} else {
						zj = true;
					}
					totalDao.update(pbl);
				}
			}
			if (son.getProcardStyle().equals("外购") && zj) {
				List<ProcardBl> pblList2 = totalDao.query(
						"from ProcardBl where procardId=? and ylCount>0", son
								.getFatherId());
				if (pblList2 != null && pblList2.size() > 0) {
					for (ProcardBl pbl : pblList2) {
						pbl.setYlCount(0f);
						pbl.setStatus("未领完");
						totalDao.update(pbl);
					}
				}
			}
			if (son.getProcardStyle().equals("外购")
					&& !son.getStatus().equals("初始")) {
				Float cyCount = nowFilnalCount - son.getFilnalCount();
				if (cyCount > 0.0001 || cyCount < -0.0001 || bfCount > 0) {
					addProcardSbWg(ptbbApplyId, sbNumber, son, cyCount
							+ bfCount, time);

				}

			}
			if (!Util.isEquals(son.getFilnalCount(), nowFilnalCount)) {// 用量有变化
				updategxcount(son, son.getFilnalCount(), nowFilnalCount);
			}
			son.setFilnalCount(nowFilnalCount);
		}
		son.setHasChange("是");
		son.setFilnalCount(nowFilnalCount);
		if (son.getProcardStyle().equals("外购")) {
			if (son.getHascount() != null && son.getHascount() > 0
					&& son.getStatus().equals("完成")) {
				son.setStatus("已发卡");
			}
		} else {
			if ((son.getTjNumber() == null || son.getFilnalCount() > son
					.getTjNumber())
					&& son.getStatus().equals("完成")) {
				son.setStatus("领工序");
			}
		}
		totalDao.update(son);
		Set<Procard> thisSonSet = son.getProcardSet();
		if (thisSonSet != null && thisSonSet.size() > 0) {
			for (Procard thisSon : thisSonSet) {
				justUpdateProcardCount(ptbbApplyId, sbNumber, thisSon,
						nowFilnalCount, nowbfCount, time);
			}
		}
	}

	/**
	 * 根据模板替换零件
	 * 
	 * @param father
	 *            上层零件
	 * @param procard
	 *            发生设变被替换的零件(预留,原本是用来判断被替换的零件是否可以继续用来使用)
	 * @param procardTemplate
	 *            替换零件
	 * @param totalWlStatus
	 *            总成物料状态
	 * @param index
	 *            标记 0和1
	 * @return
	 */
	private Procard addProcardInFather(Integer ptbbApplyId, String sbNumber,
			Procard father, Procard pro, ProcardTemplate procardTemplate,
			String totalWlStatus, int index) {
		// TODO Auto-generated method stub
		Float hadCount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from Procard where markId=? and fatherId=? and (sbStatus is null or sbStatus !='删除')",
						procardTemplate.getMarkId(), father.getId());
		if (hadCount != null && hadCount > 0) {
			return null;
		}
		String message = "";
		// 生成流水卡片
		message += procardTemplate.getMarkId();
		Procard procard = new Procard();
		int youxiao = 0;// 之前生成的零件是否有效0无效,1有效
		// if(index>0
		// &&pro!=null&&pro.getMarkId().equals(procardTemplate.getMarkId())&&pro.getBanci().equals(procardTemplate.getBanci())){
		// if(pro.getProcard().getStatus().equals("初始")||pro.getProcard().getStatus().equals("已发卡")
		// ||pro.getProcard().getStatus().equals("已发料")){//上层零件未生产过
		// youxiao=1;
		// }else if(father.getStatus().equals("领工序")){
		// //查询是否有工序进行生产
		// Float count = (Float)
		// totalDao.getObjectByCondition("select count(*) from ProcessInfor where procard.id=? and applyCount>0",
		// pro.getProcard().getId());
		// if(count==null||count==0){
		// youxiao=1;
		// }
		// }
		// }
		if (youxiao == 0) {
			/**
			 * 将流水卡片模板转换为流水卡片
			 */
			BeanUtils.copyProperties(procardTemplate, procard, new String[] {
					"rootId", "fatherId", "id" });
			if (procard.getBanci() == null) {
				procard.setBanci(0);
			}
			Float needNumber = null;
			if ("外购".equals(procard.getProcardStyle())) {
				Map<String, String> markidMap = new HashMap();
				markidMap.put("1.01.30017", "1.01.30022");
				markidMap.put("1.01.30005", "1.01.30011");
				markidMap.put("1.01.30012", "1.01.30007");
				markidMap.put("1.01.10025", "1.01.10055");
				markidMap.put("1.01.10021", "1.01.10040");
				markidMap.put("1.01.10028", "1.01.10043");
				markidMap.put("1.01.10027", "1.01.10042");
				markidMap.put("1.01.10026", "1.01.10062");
				markidMap.put("1.01.10030", "1.01.12501");
				markidMap.put("1.01.10029", "1.01.12537");
				markidMap.put("1.01.30018", "1.01.30014");
				markidMap.put("1.01.30026", "1.01.30016");
				markidMap.put("1.01.30034", "1.01.30010");
				markidMap.put("2.09.00014", "30100005");
				markidMap.put("DKBA8.840.0069", "30040054");
				markidMap.put("QDKBA4.409.0670", "26020187");
				markidMap.put("1.04.00560", "28010212");
				markidMap.put("DKBA3360.1", "30060106");
				markidMap.put("1.01.30002", "1.01.30013");

				String zhmarkid = (String) markidMap.get(procard.getMarkId());
				if ((zhmarkid != null) && (zhmarkid.length() > 0)) {
					String sql1 = "from YuanclAndWaigj where  (banbenStatus is null or banbenStatus!='历史') and markId=? and kgliao =? ";
					YuanclAndWaigj yuanclAndWaigj = null;
					try {
						yuanclAndWaigj = (YuanclAndWaigj) totalDao
								.getObjectByCondition(sql1, new Object[] {
										zhmarkid, procard.getKgliao() });
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (yuanclAndWaigj != null) {
						BeanUtils.copyProperties(yuanclAndWaigj, procard,
								new String[] { "id" });

						procard.setProName(yuanclAndWaigj.getName());
					}
				}
				needNumber = Float.valueOf(father.getFilnalCount().floatValue()
						* procard.getQuanzi2().floatValue()
						/ procard.getQuanzi1().floatValue());
				if (procard.getSunhao() != null && procard.getSunhao() > 0) {
					if (procard.getSunhaoType() != null
							&& procard.getSunhaoType() == 1) {
						needNumber = needNumber + procard.getSunhao();
					} else {
						needNumber = needNumber * (procard.getSunhao() + 100)
								/ 100F;
					}
					Float qz2 = needNumber
							/ father.getFilnalCount().floatValue();
					procard.setQuanzi2(qz2);
					procard.setQuanzi1(1F);
				}
				if (procard.getUnit().equals("PCS")
						|| procard.getUnit().equals("套")
						|| procard.getUnit().equals("瓶")
						|| procard.getUnit().equals("个")) {
					needNumber = (float) Math.ceil(needNumber);
				} else {
					needNumber = Util.FomartFloat(needNumber, 4);
				}
				String result = String.format("%.4f",
						new Object[] { needNumber });
				needNumber = Float.valueOf(Float.parseFloat(result));
				if (!"否".equals(procard.getLingliaostatus())) {
					procard.setLingliaostatus("是");
				}
				// needNumber = father.getFilnalCount() * procard.getQuanzi2()
				// / procard.getQuanzi1();
			} else {
				needNumber = father.getFilnalCount() * procard.getCorrCount();
				needNumber = (float) Math.ceil(needNumber);
			}

			// Double d = Math.ceil(needNumber);
			// procard.setNeedCount(Float.parseFloat(d.toString()));//
			// 计算外购/自制的实际需求数量(自动进1取整)
			// procard.setFilnalCount(Float.parseFloat(d.toString()));
			// Double d = Math.ceil(needNumber);
			// procard.setNeedCount(needNumber);// 计算外购/自制的实际需求数量(自动进1取整)

			procard.setPlanOrderId(father.getPlanOrderId());// 内部计划单id
			procard.setPlanOrderNum(father.getPlanOrderNum());// 内部计划单号
			procard.setOrderNumber(father.getOrderNumber());// 订单编号
			procard.setOrderId(father.getOrderId());// 订单id
			procard.setProcardTemplateId(procardTemplate.getId());// bom模板id
			procard.setStatus("初始");
			procard.setProcardTime(Util.getDateTime());// 制卡时间
			procard.setSelfCard(findMaxSelfCard(procard.getMarkId()));// 批次号
			procard.setZhikaren(Util.getLoginUser().getName());// 制卡人(当前登录用户)
			procard.setBarcode(UUID.randomUUID().toString());// 条码
			procard.setFilnalCount(needNumber);// 生产数量

			// 设置调用关系
			procard.setFatherId(father.getId());// 父类id
			procard.setRootId(father.getRootId());// 更新rootId
			procard.setProcard(father);// 设置父类
			procard.setRootSelfCard(father.getRootSelfCard());
			procard.setRootMarkId(father.getRootMarkId());
			procard.setYwMarkId(father.getYwMarkId());
			procard.setProductStyle(father.getProductStyle());
			procard.setJioafuDate(Util.getDateTime());
			procard.setHasChange("是");
			procard.setSbId(ptbbApplyId);
			procard.setSbNumber(sbNumber);
			procard.setMinNumber(0f);
			procard.setBelongLayer(father.getBelongLayer() + 1);
			if (procard.getProcardStyle().equals("外购")) {
				// 添加外购件欲增数量
				// addProcardSbWg(ptbbApplyId, sbNumber, procard, needNumber,
				// Util
				// .getDateTime());
				sbData++;
			}
			totalDao.save(procard);// 添加
			// if(!"待定".equals(totalWlStatus)&&"外购".equals(procard.getProcardStyle())){//此生成BOM已被激活
			//				
			// }
			// 遍历该流水卡片对应工序并生成工序
			Set<ProcessTemplate> setProCess = procardTemplate
					.getProcessTemplate();
			for (ProcessTemplate processTem : setProCess) {
				ProcessInfor process = new ProcessInfor();
				BeanUtils.copyProperties(processTem, process, new String[] {
						"id", "procardTemplate" });
				// process.setProcessNO(processTem.getProcessNO());// 工序号
				// process.setProcessName(processTem.getProcessName());// 工序名称
				// process.setProcessStatus(processTem.getProcessStatus());//
				// 状态(并行/单独)
				// process.setParallelId(processTem.getParallelId());// 并行开始id

				// 人工节拍和设备节拍处理
				if (process.getProductStyle() != null
						&& process.getProductStyle().equals("外委")) {// 外委工序节拍设为1
					process.setOpcaozuojiepai(1f);
					process.setOpshebeijiepai(1f);
					process.setGzzhunbeijiepai(1f);
					process.setGzzhunbeicishu(1f);
					process.setAllJiepai(1f);
				} else {
					if (process.getOpcaozuojiepai() == null) {
						process.setOpcaozuojiepai(0F);
					}
					if (process.getOpshebeijiepai() == null) {
						process.setOpshebeijiepai(0F);
					}
				}
				process.setTotalCount(procard.getFilnalCount());// 可领取量
				process.setStatus("初始");
				process.setProcessTemplateId(processTem.getId());
				process.setProcard(procard);
				// -----------------辅料------------------
				if (processTem.getIsNeedFuliao() != null
						&& processTem.getIsNeedFuliao().equals("yes")) {
					process.setIsNeedFuliao("yes");
					Set<ProcessFuLiaoTemplate> fltmpSet = processTem
							.getProcessFuLiaoTemplate();
					if (fltmpSet.size() > 0) {
						Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
						for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
							ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
							BeanUtils.copyProperties(fltmp, pinfoFl,
									new String[] { "id" });
							if (pinfoFl.getQuanzhi1() == null) {
								pinfoFl.setQuanzhi1(1f);
							}
							if (pinfoFl.getQuanzhi2() == null) {
								pinfoFl.setQuanzhi2(1f);
							}
							pinfoFl.setTotalCount(procard.getFilnalCount()
									* pinfoFl.getQuanzhi2()
									/ pinfoFl.getQuanzhi1());
							pinfoFl.setProcessInfor(process);
							pinfoFl.setOutCount(0f);
							pinfoFlSet.add(pinfoFl);
						}
						process.setProcessinforFuLiao(pinfoFlSet);
					}
				}
				try {
					totalDao.save(process);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			// 遍历查询子类流水卡片
			// Set<ProcardTemplate> setProCard =
			// procardTemplate.getProcardTSet();
			List<ProcardTemplate> setProCard = totalDao
					.query(
							"  from ProcardTemplate where fatherId =?"
									+ " and  (dataStatus <> '删除' or dataStatus is null ) and (banbenstatus <> '历史'"
									+ "or banbenstatus is null )",
							procardTemplate.getId());
			// Set<Procard> sonSet = pro.getProcardSet();
			if (setProCard != null && setProCard.size() > 0) {
				for (ProcardTemplate procardTem2 : setProCard) {
					if (procardTem2.getBzStatus() == null
							|| !procardTem2.getBzStatus().equals("已批准")) {
						throw new RuntimeException("零件:"
								+ procardTem2.getMarkId() + "的当前编制状态为:"
								+ procardTem2.getBzStatus() + "不能用于生成或更新生产件!");
					}
					Procard thisProcard = null;
					// if(sonSet!=null&&sonSet.size()>0){
					// for(Procard son:sonSet){
					// if(son.getProcardTemplateId().equals(procardTem2.getId())){
					// thisProcard = son;
					// }
					// }
					// }
					addProcardInFather(ptbbApplyId, sbNumber, procard,
							thisProcard, procardTem2, totalWlStatus, 1);
				}
			}
		} else {
			copyProcard(procard, pro, procardTemplate.getQuanzi1(),
					procardTemplate.getQuanzi2(), procardTemplate
							.getCorrCount(), father);
		}
		return pro;

	}

	/**
	 * 复制生产件
	 * 
	 * @param procard
	 *            新生成的零件
	 * @param pro
	 *            被替换的零件
	 * @param quanzi1
	 *            新的比例
	 * @param quanzi2
	 *            新的比例
	 * @param corrCount
	 *            新的比例
	 * @param father
	 *            上层零件
	 * @return
	 */
	private String copyProcard(Procard procard, Procard pro, Float quanzi1,
			Float quanzi2, Float corrCount, Procard father) {
		// TODO Auto-generated method stub
		BeanUtils.copyProperties(pro, procard, new String[] { "id", "procard",
				"procardSet", "processInforSet", "wgwwPlanSet",
				"procardPartsSet", "procardPro", "oneProcardBonus",
				"processPeopleSet" });
		procard.setSbStatus(null);
		procard.setQuanzi1(quanzi1);
		procard.setQuanzi2(quanzi2);
		procard.setCorrCount(corrCount);
		Float nowFilnalCount = pro.getFilnalCount();
		if (father != null) {
			procard.setFatherId(father.getId());
			procard.setBelongLayer(father.getBelongLayer() + 1);
			procard.setProcard(father);
			Set<Procard> procardSet = father.getProcardSet();
			if (procardSet == null) {
				procardSet = new HashSet<Procard>();
				procardSet.add(procard);
			}
			if (procard.getProcardStyle().equals("外购")) {
				nowFilnalCount = father.getFilnalCount() * procard.getQuanzi1()
						/ procard.getQuanzi2();
			} else {
				nowFilnalCount = father.getFilnalCount()
						* procard.getCorrCount();
				nowFilnalCount = (float) Math.ceil(nowFilnalCount);
			}
			totalDao.save(procard);
			totalDao.update(father);
		}
		// 复制工序
		Set<ProcessInfor> processSet = pro.getProcessInforSet();
		if (processSet != null && processSet.size() > 0) {
			Set<ProcessInfor> newProcessSet = new HashSet<ProcessInfor>();
			for (ProcessInfor process : processSet) {
				ProcessInfor newProcess = new ProcessInfor();
				BeanUtils.copyProperties(process, newProcess, new String[] {
						"id", "procard", "osWork", "procardPro",
						"productProcess", "processinforFuLiao", "pg" });
				if (nowFilnalCount > process.getTotalCount()) {
					newProcess.setTotalCount(nowFilnalCount);
				}
				newProcessSet.add(newProcess);
				newProcess.setProcard(procard);
				totalDao.save(newProcess);
				// 复制工序日志记录
				List<ProcessInforReceiveLog> logList = totalDao.query(
						"from ProcessInforReceiveLog where fk_processInforId",
						process.getId());
				if (logList != null && logList.size() > 0) {
					for (ProcessInforReceiveLog log : logList) {
						ProcessInforReceiveLog newLog = new ProcessInforReceiveLog();
						BeanUtils.copyProperties(log, newLog, new String[] {
								"id", "fk_processInforId" });
						newLog.setFk_processInforId(newProcess.getId());
						totalDao.save(newLog);
					}
				}
				// 复制辅料
				Set<ProcessinforFuLiao> processinforFuLiaoSet = process
						.getProcessinforFuLiao();
				if (processinforFuLiaoSet != null
						&& processinforFuLiaoSet.size() > 0) {
					Set<ProcessinforFuLiao> newflSet = new HashSet<ProcessinforFuLiao>();
					for (ProcessinforFuLiao fl : processinforFuLiaoSet) {
						ProcessinforFuLiao newfl = new ProcessinforFuLiao();
						BeanUtils.copyProperties(fl, newfl, new String[] {
								"id", "processInfor" });
						newfl.setProcessInfor(newProcess);
						newflSet.add(newfl);
						totalDao.save(newfl);
					}
					newProcess.setProcessinforFuLiao(newflSet);
				}
			}
			procard.setProcessInforSet(newProcessSet);
			totalDao.update(procard);
		}

		// 复制领取工序的成员
		Set<ProcessinforPeople> processPeopleSet = pro.getProcessPeopleSet();
		if (processPeopleSet != null && processPeopleSet.size() > 0) {
			Set<ProcessinforPeople> newppSet = new HashSet<ProcessinforPeople>();
			for (ProcessinforPeople pp : processPeopleSet) {
				ProcessinforPeople newpp = new ProcessinforPeople();
				BeanUtils.copyProperties(pp, newpp, new String[] { "id",
						"procard" });
				newpp.setProcard(procard);
				newppSet.add(newpp);
				// totalDao.save(newppSet);
			}
			procard.setProcessPeopleSet(newppSet);
			totalDao.update(procard);
		}
		// 占用外购外委数据

		// 递归下层
		return null;
	}

	// 查询该件号最大批次号
	public String findMaxSelfCard(String markId) {
		String mouth = new SimpleDateFormat("yyyyMMdd").format(new Date());
		int yy = Integer.parseInt(mouth.substring(0, 4));
		int mm = Integer.parseInt(mouth.substring(4, 6));
		int dd = Integer.parseInt(mouth.substring(6, 8));
		if (dd >= 26) {
			if (mm == 12) {
				mm = 1;
				yy++;
			} else {
				mm++;
			}
		}
		if (mm < 10) {
			mouth = yy + "0" + mm;
		} else {
			mouth = yy + "" + mm;
		}
		String hql = "select max(selfCard) from Procard where markId=? and selfCard like '%"
				+ mouth + "%'";
		Object object = (Object) totalDao.getObjectByCondition(hql, markId);
		if (object != null) {
			Long selfCard = Long.parseLong(object.toString()) + 1;// 当前最大流水卡片
			return selfCard.toString();
		} else {
			return mouth + "00001";
		}
	}

	@Override
	public String getSpjdCount() {
		// TODO Auto-generated method stub
		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='BOM编制节点数'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode != null) {
			return valueCode;
		} else {
			return "4";
		}
	}

	@Override
	public String deleteSb(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply == null) {
			return "不存在你要删除的设变申请!";
		} else if (
		// ptbbApply.getProcessStatus().equals("资料更新")||
		ptbbApply.getProcessStatus().equals("关联并通知生产")
				|| ptbbApply.getProcessStatus().equals("关闭")) {
			return "设变申请进度当前状态为:" + ptbbApply.getProcessStatus() + "不允许删除!";
		} else {
			// 删除关联并通知生产数据
			List<ProcardAboutBanBenApply> pabbList = totalDao.query(
					"from ProcardAboutBanBenApply where bbapplyId=?", id);
			if (pabbList != null && pabbList.size() > 0) {
				for (ProcardAboutBanBenApply pabb : pabbList) {
					Procard p = (Procard) totalDao.getObjectById(Procard.class,
							pabb.getProcardId());
					if (p != null && p.getStatus().equals("设变锁定")) {
						p.setStatus(p.getOldStatus());
						totalDao.update(p);
					}
					Set<ProcessAboutBanBenApply> processabbSet = pabb
							.getProcessabbSet();
					pabb.setProcessabbSet(null);
					if (processabbSet != null && processabbSet.size() > 0) {
						for (ProcessAboutBanBenApply processabb : processabbSet) {
							processabb.setPabb(null);
							totalDao.delete(processabb);
						}
					}
					// 删除生产关联评语
					List<ProcardBanBenJudge> pbbjList = totalDao.query(
							"from ProcardBanBenJudge where pabbJudgeId=?", pabb
									.getId());
					if (pbbjList != null && pbbjList.size() > 0) {
						for (ProcardBanBenJudge pbbj : pbbjList) {
							totalDao.delete(pbbj);
						}
					}
					totalDao.delete(pabb);
				}

			}
			// 删除各评审人员
			Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply.getPtbbjset();
			ptbbApply.setPtbbjset(null);
			if (ptbbjSet != null && ptbbjSet.size() > 0) {
				for (ProcardTemplateBanBenJudges ptbbj : ptbbjSet) {
					ptbbj.setPtbbApply(null);
					totalDao.delete(ptbbj);
				}
			}
			// 删除设变零件明细
			Set<ProcardTemplateBanBen> procardTemplateBanBenSet = ptbbApply
					.getProcardTemplateBanBen();
			ptbbApply.setProcardTemplateBanBen(null);
			if (procardTemplateBanBenSet != null
					&& procardTemplateBanBenSet.size() > 0) {
				for (ProcardTemplateBanBen procardTemplateBanBen : procardTemplateBanBenSet) {
					procardTemplateBanBen.setProcardTemplateBanBenApply(null);
					List<ProcardTemplate> backptList = totalDao
							.query(
									"from ProcardTemplate where markId = ? and (banbenStatus is null or banbenStatus!='历史')",
									procardTemplateBanBen.getMarkId());
					if (backptList != null && backptList.size() > 0) {
						for (ProcardTemplate back : backptList) {
							back.setBzStatus("已批准");
							totalDao.update(back);
						}
					}
					totalDao.delete(procardTemplateBanBen);
				}
			}
			// 终极删除
			totalDao.delete(ptbbApply);
			return "true";
		}
	}

	/**
	 * 添加外购件设变关联订单情况
	 * 
	 * @param ptbbApplyId
	 * @param procard
	 * @param count
	 * @param time
	 * @return
	 */
	public String addProcardSbWg(Integer ptbbApplyId, String sbNumber,
			Procard procard, Float count, String time) {
		String sql = "from ProcardSbWg where ptbbApplyId=? and markId=? and banci=? ";
		ProcardSbWg procardSbWg = null;
		if (procard.getProcardStyle().equals("外购")) {
			procardSbWg = (ProcardSbWg) totalDao.getObjectByCondition(sql
					+ " and kgliao=?", ptbbApplyId, procard.getMarkId(),
					procard.getBanci() == null ? 0 : procard.getBanci(),
					procard.getKgliao() == null ? "TK" : procard.getKgliao());
		} else {
			procardSbWg = (ProcardSbWg) totalDao.getObjectByCondition(sql,
					ptbbApplyId, procard.getMarkId(),
					procard.getBanci() == null ? 0 : procard.getBanci());
		}
		if (procardSbWg != null && procardSbWg.getProcardSbWgDetails() != null
				&& procardSbWg.getProcardSbWgDetails().size() > 0) {
			for (ProcardSbWgDetail procardSbWgDetail : procardSbWg
					.getProcardSbWgDetails()) {
				if (procardSbWgDetail.getProcardId().equals(procard.getId())) {
					return "true";
				}
			}
		}
		ProcardSbWgDetail procardSbWgDetail = new ProcardSbWgDetail();
		procardSbWgDetail.setProcardId(procard.getId());
		procardSbWgDetail.setMarkId(procard.getMarkId());
		procardSbWgDetail.setSelfCard(procard.getSelfCard());
		procardSbWgDetail.setProName(procard.getProName());
		procardSbWgDetail.setBanbenNumber(procard.getBanBenNumber());
		procardSbWgDetail.setBanci(procard.getBanci());
		procardSbWgDetail.setSbCount(count);
		procardSbWgDetail.setClCount(0f);
		procardSbWgDetail.setAddTime(time);
		try {
			procardSbWgDetail
					.setOrderId(Integer.parseInt(procard.getOrderId()));// 订单Id;
		} catch (Exception e) {
			// TODO: handle exception
		}
		procardSbWgDetail.setOrderNumber(procard.getOrderNumber());// 订单号（内）
		procardSbWgDetail.setOrderOutNumber(procard.getOutOrderNum());// 订单号(外)
		procardSbWgDetail.setRootMarkId(procard.getRootMarkId());// 总成件号
		procardSbWgDetail.setRootSelfCard(procard.getRootSelfCard());// 总成批次
		procardSbWgDetail.setYwMarkId(procard.getYwMarkId());// 业务件号
		if (procardSbWg == null) {
			procardSbWg = new ProcardSbWg();
			procardSbWg.setSbNumber(sbNumber);
			procardSbWg.setPtbbApplyId(ptbbApplyId);
			procardSbWg.setMarkId(procard.getMarkId());// 件号
			procardSbWg.setProName(procard.getProName());// 零件名称
			procardSbWg.setSpecification(procard.getSpecification());
			procardSbWg.setUnit(procard.getUnit());
			procardSbWg.setWgType(procard.getWgType());
			procardSbWg.setBanbenNumber(procard.getBanBenNumber());// 版本号
			procardSbWg.setBanci(procard.getBanci() == null ? 0 : procard
					.getBanci());// 版次
			procardSbWg.setKgliao(procard.getKgliao());// 供料属性
			procardSbWg.setAddCount(count);// 如果为正数增加数量,如果为负数则为减少数量
			procardSbWg.setAddTime(time);// 添加时间
			procardSbWg.setStatus("待处理");// 待处理,完成
			procardSbWg.setTuhao(procard.getTuhao());
			procardSbWgDetail.setProcardSbWg(procardSbWg);
			Set<ProcardSbWgDetail> procardSbWgDetails = new HashSet<ProcardSbWgDetail>();
			procardSbWgDetails.add(procardSbWgDetail);
			procardSbWg.setProcardSbWgDetails(procardSbWgDetails);
			return totalDao.save(procardSbWg) + "";
		} else {
			procardSbWg.setAddCount(procardSbWg.getAddCount() + count);
			procardSbWgDetail.setProcardSbWg(procardSbWg);
			Set<ProcardSbWgDetail> procardSbWgDetails = procardSbWg
					.getProcardSbWgDetails();
			procardSbWgDetails.add(procardSbWgDetail);
			procardSbWg.setProcardSbWgDetails(procardSbWgDetails);
			return totalDao.update(procardSbWg) + "";
		}
	}

	/**
	 * 添加设变报废待处理生产件
	 * 
	 * @param ptbbApplyId
	 * @param procard
	 * @param count
	 * @param time
	 * @param processNo
	 * @param processName
	 * @return
	 */
	public String addProcardSbWaster(Integer ptbbApplyId, String sbNumber,
			Procard procard, Float count, String time, Integer processNo,
			String processName, String clType) {
		ProcardSbWaster procardSbWaster = new ProcardSbWaster();
		procardSbWaster.setSbNumber(sbNumber);
		procardSbWaster.setPtbbApplyId(ptbbApplyId);// 设变申请id
		procardSbWaster.setProcardId(procard.getId());// 生产件id
		procardSbWaster.setMarkId(procard.getMarkId());// 件号
		procardSbWaster.setProName(procard.getProName());// 名称
		procardSbWaster.setSpecification(procard.getSpecification());
		procardSbWaster.setUnit(procard.getUnit());
		procardSbWaster.setWgType(procard.getWgType());
		procardSbWaster.setBanbenNumber(procard.getBanBenNumber());// 版本号
		procardSbWaster.setBanci(procard.getBanci() == null ? 0 : procard
				.getBanci());// 版次
		procardSbWaster.setProcardStyle(procard.getProcardStyle());// 零件类型
		procardSbWaster.setKgliao(procard.getKgliao());// 供料属性
		procardSbWaster.setSelfCard(procard.getSelfCard());// 生产批次
		procardSbWaster.setOrderNumber(procard.getOrderNumber());// 订单号
		procardSbWaster.setFinalCount(procard.getFilnalCount());
		procardSbWaster.setBfCount(count);// 报废数量
		procardSbWaster.setProcessNo(processNo);// 工序号
		procardSbWaster.setProcessName(processName);// 工序名称
		procardSbWaster.setRootMarkId(procard.getRootMarkId());// 总成件号
		procardSbWaster.setRootSelfCard(procard.getRootSelfCard());// 总成批次
		procardSbWaster.setYwmarkId(procard.getYwMarkId());
		procardSbWaster.setAddTime(time);// 添加时间
		procardSbWaster.setTuhao(procard.getTuhao());
		procardSbWaster.setStatus("待处理");
		procardSbWaster.setClType(clType);
		Set<Procard> sonSet = procard.getProcardSet();
		Set<ProcardSbWasterxc> psbwxcSet = new HashSet<ProcardSbWasterxc>();
		// 本身作为下层用来判定
		ProcardSbWasterxc psbwxcself = new ProcardSbWasterxc();
		psbwxcself.setProcardId(procard.getId());//
		psbwxcself.setMarkId(procard.getMarkId());
		psbwxcself.setSelfCard(procard.getSelfCard());
		psbwxcself.setProName(procard.getProName());
		psbwxcself.setStatus(procard.getSbStatus());
		psbwxcself.setProcardStyle(procard.getProcardStyle());
		psbwxcself.setFinalCount(procard.getFilnalCount());
		psbwxcself.setKlnumber(procard.getKlNumber());
		psbwxcself.setHasCount(procard.getHascount());
		psbwxcself.setTjNmber(procard.getTjNumber());
		psbwxcself.setCorrCount(procard.getCorrCount());
		psbwxcself.setQuanzi1(procard.getQuanzi1());
		psbwxcself.setQuanzi2(procard.getQuanzi2());
		psbwxcself.setGlCount(count);
		psbwxcself.setProcardSbWaster(procardSbWaster);
		psbwxcself.setKgliao(procard.getKgliao());
		psbwxcself.setWgType(procard.getWgType());
		psbwxcself.setProcessNo(processNo);
		psbwxcself.setProcessName(processName);
		psbwxcself.setUtil(procard.getUnit());
		psbwxcself.setBanbenNumber(procard.getBanBenNumber());
		psbwxcself.setBanci(procard.getBanci());
		psbwxcself.setXcType("本身");
		psbwxcself.setClStatus("待处理");
		psbwxcSet.add(psbwxcself);
		if (sonSet != null && sonSet.size() > 0) {// 遍历下层情况
			for (Procard son : sonSet) {
				ProcardSbWasterxc psbwxc = new ProcardSbWasterxc();
				psbwxc.setProcardId(son.getId());//
				psbwxc.setMarkId(son.getMarkId());
				psbwxc.setUtil(son.getUnit());
				psbwxc.setSelfCard(son.getSelfCard());
				psbwxc.setProName(son.getProName());
				psbwxc.setStatus(son.getSbStatus());
				psbwxc.setProcardStyle(son.getProcardStyle());
				psbwxc.setFinalCount(son.getFilnalCount());
				psbwxc.setKlnumber(son.getKlNumber());
				psbwxc.setHasCount(son.getHascount());
				psbwxc.setTjNmber(son.getTjNumber());
				psbwxc.setCorrCount(son.getCorrCount());
				psbwxc.setQuanzi1(son.getQuanzi1());
				psbwxc.setQuanzi2(son.getQuanzi2());
				psbwxc.setKgliao(son.getKgliao());
				psbwxc.setWgType(son.getWgType());
				psbwxc.setBanbenNumber(son.getBanBenNumber());
				psbwxc.setBanci(son.getBanci());
				psbwxc.setClStatus("待处理");
				psbwxc.setXcType("下层");
				if (son.getKlNumber() == null || son.getHascount() == null) {
					psbwxc.setGlCount(0f);
				} else {
					Float yjCount = son.getKlNumber() - son.getHascount();
					if (yjCount < son.getFilnalCount()) {
						Float ce = son.getFilnalCount() - yjCount;
						Float singleCount = 0f;
						if (son.getProcardStyle().equals("外购")) {
							singleCount = son.getQuanzi2() / son.getQuanzi1();
						} else {
							singleCount = son.getCorrCount();
						}
						if (ce < (singleCount * 0.05)) {// 表示为小数点差额
							ce = 0f;
							psbwxc.setGlCount(count * singleCount);
						} else {
							// 查询之前对应的关联数量
							Float hasglCount = (Float) totalDao
									.getObjectByCondition(
											"select count(glCount) from ProcardSbWasterxc where procardId=?",
											son.getId());
							if (hasglCount == null) {
								hasglCount = 0f;
							}
							if (hasglCount > 0) {// 对应的缺领数量已经补足
								psbwxc.setGlCount(count * singleCount);
							} else {
								// 查询之前的报废数量
								Float scCount = (Float) totalDao
										.getObjectByCondition(
												"select count(bfCount) from ProcardSbWaster where procardId=?",
												procard.getId());
								if (scCount == null) {
									scCount = 0f;
								}
								Float newce = ce - scCount * singleCount;
								if (ce < (singleCount * 0.05)) {// 表示为小数点差额,之前缺领已经弥补
									ce = 0f;
									psbwxc.setGlCount(count * singleCount);
								} else {
									Float glCount = count * singleCount - ce;
									if (glCount < 0) {
										psbwxc.setGlCount(0f);
									} else {
										psbwxc.setGlCount(glCount);
									}
								}
							}
						}

					}
				}
				psbwxc.setProcardSbWaster(procardSbWaster);
				psbwxcSet.add(psbwxc);
			}
		}
		procardSbWaster.setProcardSbWasterxcs(psbwxcSet);
		totalDao.save(procardSbWaster);
		return "true";
	}

	/**
	 * 添加生产件返修单
	 * 
	 * @param ptbbApplyId
	 * @param procard
	 * @param count
	 * @param time
	 * @param process
	 * @return
	 */
	public String addProcardReProduct(Integer ptbbApplyId, String sbNumber,
			Procard procard, Float count, String time, ProcessInfor process) {
		ProcardReProduct procardReProduct = new ProcardReProduct();
		String before = "fxd_" + Util.getDateTime("yyyyMMdd");
		String maxnumber = (String) totalDao
				.getObjectByCondition("select max(fxNumber) from ProcardReProduct where fxNumber like '"
						+ before + "%'");
		if (maxnumber != null && !"".equals(maxnumber)) {
			// String number1 = paymentVoucher2.getNumber();
			String now_number = maxnumber.split(before)[1];
			Integer number2 = Integer.parseInt(now_number) + 1;
			DecimalFormat df = new DecimalFormat("0000");
			String number3 = df.format(number2);
			procardReProduct.setFxNumber(before + number3);
		} else {
			procardReProduct.setFxNumber(before + "0001");
		}

		procardReProduct.setSbNumber(sbNumber);
		procardReProduct.setSource("设变");
		procardReProduct.setPtbbApplyId(ptbbApplyId);// 设变申请id
		procardReProduct.setProcardId(procard.getId());// 生产件id
		procardReProduct.setMarkId(procard.getMarkId());// 件号
		procardReProduct.setProName(procard.getProName());// 名称
		procardReProduct.setSpecification(procard.getSpecification());
		procardReProduct.setUnit(procard.getUnit());
		procardReProduct.setWgType(procard.getWgType());
		procardReProduct.setBanbenNumber(procard.getBanBenNumber());// 版本号
		procardReProduct.setBanci(procard.getBanci() == null ? 0 : procard
				.getBanci());// 版次
		procardReProduct.setProcardStyle(procard.getProcardStyle());// 零件类型
		procardReProduct.setKgliao(procard.getKgliao());// 供料属性
		procardReProduct.setSelfCard(procard.getSelfCard());// 生产批次
		procardReProduct.setOrderNumber(procard.getOrderNumber());// 订单号
		procardReProduct.setFxCount(count);// 报废数量
		procardReProduct.setProcessId(process.getId());
		procardReProduct.setProcessNo(process.getProcessNO());// 工序号
		procardReProduct.setProcessName(process.getProcessName());// 工序名称
		procardReProduct.setRootMarkId(procard.getRootMarkId());// 总成件号
		procardReProduct.setRootSelfCard(procard.getRootSelfCard());// 总成批次
		procardReProduct.setAddTime(time);// 添加时间
		procardReProduct.setTuhao(procard.getTuhao());
		procardReProduct.setStatus("待处理");
		totalDao.save(procardReProduct);
		// 添加返修工序
		ProcessInfor reprocess = new ProcessInfor();
		reprocess.setZjStatus("yes");
		reprocess.setProcessNO(process.getProcessNO());
		reprocess.setProcessName("返修");
		reprocess.setReProductId(procardReProduct.getId());
		reprocess.setTotalCount(count);
		reprocess.setStatus("初始");
		reprocess.setProductStyle("自制");
		totalDao.save(reprocess);
		return "true";
	}

	@Override
	public Map<Integer, Object> findprocardsbwgsByCondition(
			ProcardSbWg procardsbwg, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		if (procardsbwg == null) {
			procardsbwg = new ProcardSbWg();
		}
		String ywmarkId = procardsbwg.getYwMarkId();
		procardsbwg.setYwMarkId(null);
		String hql = totalDao.criteriaQueries(procardsbwg, null, null);
		if (ywmarkId != null && ywmarkId.length() > 0) {
			hql += " and id in(select procardSbWg.id from ProcardSbWgDetail where ywMarkId like '%"
					+ ywmarkId + "%')";
		}
		hql += " order by id desc";
		int count = totalDao.getCount(hql);
		List<ProcardSbWg> list = totalDao.findAllByPage(hql, pageNo, pageSize);
		if (list != null && list.size() > 0) {
			for (ProcardSbWg psb : list) {
				Set<ProcardSbWgDetail> psbDetailSet = psb
						.getProcardSbWgDetails();
				StringBuffer sb = new StringBuffer();
				List<String> ywList = new ArrayList<String>();
				if (psbDetailSet != null && psbDetailSet.size() > 0) {
					for (ProcardSbWgDetail psbDetail : psbDetailSet) {
						if (!ywList.contains(psbDetail.getYwMarkId())) {
							ywList.add(psbDetail.getYwMarkId());
							if (sb.length() == 0) {
								sb.append(psbDetail.getYwMarkId());
							} else {
								sb.append("," + psbDetail.getYwMarkId());
							}
						}
					}
				}
				psb.setYwMarkId(sb.toString());
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, list);
		map.put(2, count);
		procardsbwg.setYwMarkId(ywmarkId);
		return map;
	}

	@Override
	public ProcardSbWg getProcardsbwgByid(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardSbWg) totalDao.getObjectById(ProcardSbWg.class, id);
	}

	@Override
	public String addsbcgTowlxq(ProcardSbWg procardsbwg) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWg old = (ProcardSbWg) totalDao.getObjectById(
				ProcardSbWg.class, procardsbwg.getId());
		if (old != null) {
			old.setClUser(user.getName());
			old.setClUserCode(user.getCode());
			old.setClUserId(user.getId());
			old.setClTime(Util.getDateTime());
			if (procardsbwg.getClCount() != null
					&& procardsbwg.getClCount() > 0) {
				old.setClCount(procardsbwg.getClCount());
				old.setCltype("增加采购");
				old.setStatus("完成");
				// 添加物料需求
				// ManualOrderPlan mp = new ManualOrderPlan();
				// mp.setMarkId(old.getMarkId());// 件号
				// mp.setProName(old.getProName());// 零件名称
				// mp.setSpecification(old.getSpecification());// 规格
				// mp.setBanben(old.getBanbenNumber());// 版本
				// mp.setNumber(procardsbwg.getClCount());//
				// mp.setTuhao(old.getTuhao());// 图号
				// mp.setUnit(old.getUnit());// 单位
				// mp.setKgliao(old.getKgliao());// //供料属性（外购件使用：是,否，null代表否）
				// mp.setBanci(old.getBanci());// 版次
				// mp.setWgType(old.getWgType());// 物料类别
				// mp.setOutcgNumber(0f);// 已采购数量
				// mp.setCategory("外购");
				// Set<ManualOrderPlanDetail> modSet = new
				// HashSet<ManualOrderPlanDetail>();// 明细
				// 一对多
				Float clCount = procardsbwg.getClCount();
				// 获取设变增加明细生成对应的物料需求明细
				Set<ProcardSbWgDetail> procardSbWgDetails = old
						.getProcardSbWgDetails();
				if (procardSbWgDetails != null && procardSbWgDetails.size() > 0) {
					for (ProcardSbWgDetail procardSbWgDetail : procardSbWgDetails) {
						if (procardSbWgDetail.getSbCount() <= 0
								|| (procardSbWgDetail.getSbCount() - procardSbWgDetail
										.getClCount()) <= 0) {
							continue;
						}
						ManualOrderPlanDetail mpDetail = new ManualOrderPlanDetail();
						mpDetail.setMarkId(old.getMarkId());// 件号
						mpDetail.setProName(old.getProName());// 零件名称
						mpDetail.setSpecification(old.getSpecification());// 规格
						mpDetail.setBanben(old.getBanbenNumber());// 版本
						mpDetail.setUnit(old.getUnit());// 单位
						mpDetail.setKgliao(old.getKgliao());// //供料属性（外购件使用：是,否，null代表否）
						mpDetail.setTuhao(old.getTuhao());// 图号
						mpDetail.setWgType(old.getWgType());// 物料类别
						if ((procardSbWgDetail.getSbCount() - procardSbWgDetail
								.getClCount()) >= clCount) {
							mpDetail.setCgnumber(clCount);// 采购数量
							procardSbWgDetail.setClCount(procardSbWgDetail
									.getClCount()
									+ clCount);
							clCount = 0f;
						} else {
							mpDetail.setCgnumber(procardSbWgDetail.getSbCount()
									- procardSbWgDetail.getClCount());// 采购数量
							clCount = clCount
									- (procardSbWgDetail.getSbCount() - procardSbWgDetail
											.getClCount());
							procardSbWgDetail.setClCount(procardSbWgDetail
									.getSbCount());
						}
						totalDao.update(procardSbWgDetail);
						mpDetail.setProcardId(procardSbWgDetail.getProcardId());
						mpDetail.setOutcgNumber(0f);// 已采购数量
						mpDetail.setAddTime(Util.getDateTime());// 添加时间
						mpDetail.setAddUsers(user.getName());// 添加人
						mpDetail.setAddUsersCode(user.getCode());// 添加人工号
						mpDetail.setType("设变");// （手动紧急）>正式订单>试制订单>预测订单>安全库存>手动添加（正常）
						// 优先级由左到右依次递减;
						mpDetail.setIsurgent("YES");// 是否紧急;(YES/NO)手动添加使用
						mpDetail.setRemarks("设变产生");// 备注
						mpDetail.setEpStatus("同意");//
						mpDetail.setOrderId(procardSbWgDetail.getOrderId());// 订单Id;
						mpDetail.setOrderNumber(procardSbWgDetail
								.getOrderNumber());// 订单号（内）
						mpDetail.setOrderOutNumber(procardSbWgDetail
								.getOrderOutNumber());// 订单号(外)
						mpDetail.setRootMarkId(procardSbWgDetail
								.getRootMarkId());// 总成件号
						mpDetail.setRootSelfCard(procardSbWgDetail
								.getRootSelfCard());// 总成批次
						mpDetail.setYwMarkId(procardSbWgDetail.getYwMarkId());// 业务件号
						mpDetail.setCategory("外购");
						// modSet.add(mpDetail);
						ProcardSbWgLog psbwLog = new ProcardSbWgLog();
						psbwLog.setPsbwgId(old.getId());// 设变外购增减表Id ProcardSbWg
						psbwLog.setForObj("物料需求");// 增减对象(物料需求,外购订单)
						psbwLog.setForObjId(mpDetail.getId());
						psbwLog.setCount(procardsbwg.getClCount());// 数量
						psbwLog.setClType("增加采购");// 处理方案
						psbwLog.setAddTime(Util.getDateTime());// 添加时间
						psbwLog.setUserName(user.getName());// 添加人
						psbwLog.setUserId(user.getId());// 添加人id
						psbwLog.setStatus("完成");
						totalDao.save(psbwLog);
						manualPlanServer.addmaualPlan1(mpDetail);
						if (mpDetail.getProcardId() != null) {
							Procard procard = (Procard) totalDao.get(
									Procard.class, mpDetail.getProcardId());
							procard.setWlqrtime(Util.getDateTime());
							totalDao.update(procard);
						}
						if (clCount == 0) {
							break;
						}
					}
				}
				// mp.setModSet(modSet);
				// totalDao.save(mp);
			} else {
				old.setClCount(0f);
				old.setCltype("不处理");
				old.setStatus("完成");
			}
			return totalDao.update(old) + "";
		}

		return "没有找到对应的记录!";
	}

	@Override
	public String closesbcg(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWg old = (ProcardSbWg) totalDao.getObjectById(
				ProcardSbWg.class, id);
		if (old == null) {
			return "没有找到对应的记录!";
		}
		if (old.getClCount() != null && old.getClCount() > 0) {
			old.setStatus("完成");
		} else {
			old.setClCount(0f);
			old.setCltype("不处理");
			old.setStatus("完成");
		}
		old.setClUser(user.getName());
		old.setClUserCode(user.getCode());
		old.setClUserId(user.getId());
		old.setClTime(Util.getDateTime());
		return totalDao.update(old) + "";
	}

	@Override
	public Map<Integer, Object> findSbdeleteAboutcg(Integer id) {
		// TODO Auto-generated method stub
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		ProcardSbWg old = (ProcardSbWg) totalDao.getObjectById(
				ProcardSbWg.class, id);
		if (old == null) {
			return null;
		}
		String banbenSql = "";
		if (old.getBanbenNumber() == null
				|| old.getBanbenNumber().length() == 0) {
			banbenSql = " and (banben is null or banben='')";
		} else {
			banbenSql = " and banben is not null and banben='"
					+ old.getBanbenNumber() + "'";
		}
		// 物料需求单
		List<ManualOrderPlan> mpList = totalDao
				.query(
						"from ManualOrderPlan where (outcgNumber is null or outcgNumber<number) and markId=? and kgliao=? "
								+ banbenSql, old.getMarkId(), old.getKgliao());
		// if(mpList!=null&&mpList.size()>0){
		// for(ManualOrderPlan mp:mpList){
		// if(mp.getModSet()!=null){
		// List<ManualOrderPlanDetail> list = new
		// ArrayList<ManualOrderPlanDetail>(mp.getModSet());
		// mp.setModLst(list);
		// }
		// }
		// }
		// 外购订单
		List<WaigouPlan> waigouPlanList = totalDao
				.query(
						" from WaigouPlan where (syNumber is null or syNumber>0) and  markId=? and kgliao=? and (waigouOrder.applystatus is null or waigouOrder.applystatus !='打回') "
								+ " or id in(select waigouPlanId from WaigouPlanclApply where procardSbWgId=?)"
								+ banbenSql, old.getMarkId(), old.getKgliao(),
						id);
		if (waigouPlanList != null && waigouPlanList.size() > 0) {
			for (WaigouPlan wp : waigouPlanList) {
				wp.setPlanNumber(wp.getWaigouOrder().getPlanNumber());
				List<WaigouPlanclApply> clApplyList = totalDao.query(
						"from WaigouPlanclApply where waigouPlanId=?", wp
								.getId());
				wp.setClApplyList(clApplyList);
			}
		}
		map.put(1, old);
		map.put(2, mpList);
		map.put(3, waigouPlanList);
		return map;
	}

	@Override
	public String manyalOrderPlansbld(Integer id, ManualOrderPlan mop) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWg oldsbwg = (ProcardSbWg) totalDao.getObjectById(
				ProcardSbWg.class, id);
		if (oldsbwg == null) {
			return "没有找到对应的记录!";
		}
		ManualOrderPlan oldmop = (ManualOrderPlan) totalDao.getObjectById(
				ManualOrderPlan.class, mop.getId());
		if (oldmop == null) {
			return "没有找到对应的记录!";
		}
		Set<ManualOrderPlanDetail> mopdSet = oldmop.getModSet();
		if (mopdSet == null) {
			mopdSet = new HashSet<ManualOrderPlanDetail>();
		}
		if (oldmop.getOutcgNumber() == null) {
			oldmop.setOutcgNumber(0f);
		}
		if ((oldmop.getNumber() - oldmop.getOutcgNumber()) < mop.getNumber()) {
			return "对不起数量超额!";
		}
		ManualOrderPlanDetail mopd = new ManualOrderPlanDetail();
		mopd.setMarkId(oldsbwg.getMarkId());// 件号
		mopd.setProName(oldsbwg.getProName());// 零件名称
		mopd.setSpecification(oldsbwg.getSpecification());// 规格
		mopd.setBanben(oldsbwg.getBanbenNumber());// 版本
		mopd.setUnit(oldsbwg.getUnit());// 单位
		mopd.setKgliao(oldsbwg.getKgliao());// //供料属性（外购件使用：是,否，null代表否）
		mopd.setTuhao(oldsbwg.getTuhao());// 图号
		mopd.setWgType(oldsbwg.getWgType());// 物料类别
		mopd.setCgnumber(-mop.getNumber());// 采购数量
		mopd.setOutcgNumber(0f);// 已采购数量
		mopd.setAddTime(Util.getDateTime());// 添加时间
		mopd.setAddUsers(user.getName());// 添加人
		mopd.setRukuNum(0f);
		mopd.setAddUsersCode(user.getCode());// 添加人工号
		mopd.setType("设变");// （手动紧急）>正式订单>试制订单>预测订单>安全库存>手动添加（正常） 优先级由左到右依次递减;
		mopd.setIsurgent("YES");// 是否紧急;(YES/NO)手动添加使用
		mopd.setRemarks("设变产生");// 备注
		mopd.setEpStatus("同意");//
		mopdSet.add(mopd);
		totalDao.save(mopd);
		totalDao.update(oldmop);
		oldmop.setNumber(oldmop.getNumber() - mop.getNumber());
		oldmop.setModSet(mopdSet);
		ProcardSbWgLog psbwLog = new ProcardSbWgLog();
		psbwLog.setPsbwgId(oldsbwg.getId());// 设变外购增减表Id ProcardSbWg
		psbwLog.setForObj("物料需求");// 增减对象(物料需求,外购订单)
		psbwLog.setForObjId(mopd.getId());
		psbwLog.setCount(mop.getNumber());// 数量
		psbwLog.setClType("减少采购");// 处理方案
		psbwLog.setAddTime(Util.getDateTime());// 添加时间
		psbwLog.setUserName(user.getName());// 添加人
		psbwLog.setUserId(user.getId());// 添加人id
		psbwLog.setStatus("完成");
		totalDao.save(psbwLog);
		oldsbwg.setClUser(user.getName());
		oldsbwg.setClUserCode(user.getCode());
		oldsbwg.setClUserId(user.getId());
		oldsbwg.setClTime(Util.getDateTime());
		if (oldsbwg.getClCount() == null) {
			oldsbwg.setClCount(mop.getNumber());
			oldsbwg.setCltype("减少采购");
		} else {
			oldsbwg.setClCount(oldsbwg.getClCount() + mop.getNumber());
			oldsbwg.setCltype("减少采购");

		}
		return totalDao.update(oldsbwg) + "";
	}

	@Override
	public String waigouPlansbld(Integer id, WaigouPlan waigouplan) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWg oldsbwg = (ProcardSbWg) totalDao.getObjectById(
				ProcardSbWg.class, id);
		if (oldsbwg == null) {
			return "没有找到对应的记录!";
		}
		WaigouPlan oldplan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, waigouplan.getId());
		if (oldplan == null) {
			return "没有找到对应的记录!";
		}
		if (oldplan.getSyNumber() == null) {
			oldplan.setSyNumber(0f);
		}
		if (oldplan.getSbjdApplyCount() == null) {
			oldplan.setSbjdApplyCount(0f);
		}
		if ((oldplan.getSyNumber() - oldplan.getSbjdApplyCount()) < waigouplan
				.getSbjdApplyCount()) {
			return "对不起数量超额!";
		}
		WaigouOrder waigouOrder = oldplan.getWaigouOrder();
		CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
				.getApplication().get("companyInfo");
		WaigouPlanclApply wgPlancl = new WaigouPlanclApply();
		String datetime = Util.getDateTime("yyyyMMdd");
		String hql = "select max(applyNumber) from WaigouPlanclApply";
		String contractNumber = (String) totalDao.getObjectByCondition(hql);
		String sc = "WGJD";
		if (contractNumber != null && contractNumber.length() > 0) {
			int num = Integer.parseInt(contractNumber.substring(12,
					contractNumber.length())) + 1;
			if (num >= 1000)
				contractNumber = sc + datetime + num;
			else if ((num >= 100))
				contractNumber = sc + datetime + "0" + num;
			else if ((num >= 10))
				contractNumber = sc + datetime + "00" + num;
			else
				contractNumber = sc + datetime + "000" + num;
		} else {
			contractNumber = sc + datetime + "0001";
		}
		wgPlancl.setApplyNumber(contractNumber);// 申请单编号
		wgPlancl.setWaigouPlanId(oldplan.getId());// 外购,外委明细Id
		wgPlancl.setPlanNumber(waigouOrder.getPlanNumber());// 外购外委单编号
		wgPlancl.setProcardSbWgId(oldsbwg.getId());// 外购设变待处理数据Id
		wgPlancl.setAllCount(oldplan.getNumber());// 总数量
		wgPlancl.setSyCount(oldplan.getSyNumber());// 剩余未送货数量
		wgPlancl.setClCount(waigouplan.getSbjdApplyCount());// 减单数量
		wgPlancl.setMarkId(oldplan.getMarkId());// 件号
		wgPlancl.setProcessNos(oldplan.getProcessNOs());// 工序号
		wgPlancl.setProcessNames(oldplan.getProcessNames());// 工序名称
		wgPlancl.setUnit(oldplan.getUnit());// 单位
		wgPlancl.setGysName(waigouOrder.getGysName());// 供应商名称
		wgPlancl.setCgname(waigouOrder.getTzUserName());// 采购员名称
		wgPlancl.setCgCode(waigouOrder.getTzUserCode());// 采购员工号
		wgPlancl.setAddUserId(user.getId());// 申请人Id
		wgPlancl.setAddUsercode(user.getCode());// 申请人工号
		wgPlancl.setAddUsername(user.getName());// 申请人名称
		wgPlancl.setProName(oldplan.getProName());// 零件名称
		wgPlancl.setTuhao(oldplan.getTuhao());// 零件图号
		wgPlancl.setWgType(oldplan.getWgType());// 物料类别
		wgPlancl.setKgliao(oldplan.getKgliao());// 供料属性
		wgPlancl.setSpecification(oldplan.getSpecification());// 规格
		wgPlancl.setEpStatus("未审批");
		totalDao.save(wgPlancl);
		Integer epId = null;
		try {
			Integer uIds = null;
			if (waigouOrder.getTzUserCode() != null
					&& waigouOrder.getTzUserCode().length() > 0) {
				uIds = (Integer) totalDao.getObjectByCondition(
						"select id from Users where code=?", waigouOrder
								.getTzUserCode());
			}
			if (uIds == null) {
				uIds = (Integer) totalDao.getObjectByCondition(
						"select id from Users where code=?", waigouOrder
								.getAddUserCode());
			}
			epId = CircuitRunServerImpl.createProcessbf(waigouOrder.getType()
					+ "减单处理申请", WaigouPlanclApply.class, wgPlancl.getId(),
					"epStatus", "id", "", waigouOrder.getType() + "减单处理申请",
					true, uIds + "");
			if (epId != null) {
				wgPlancl.setEpId(epId);
			} else {
				return "审批流程有误，申请失败!";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (!oldplan.getStatus().equals("减单申请中")) {
			oldplan.setOldStatus(oldplan.getStatus());
			oldplan.setStatus("减单申请中");
		}
		// 同时修改采购订单的状态
		WaigouOrder waigouordere = oldplan.getWaigouOrder();
		if (!"减单申请中".equals(waigouordere.getStatus())) {
			waigouordere.setOldstatus(waigouordere.getStatus());
			waigouordere.setStatus("减单申请中");
		}
		totalDao.update(oldplan);
		totalDao.update(waigouordere);

		ProcardSbWgLog psbwLog = new ProcardSbWgLog();
		psbwLog.setPsbwgId(oldsbwg.getId());// 设变外购增减表Id ProcardSbWg
		psbwLog.setForObj("外购订单");// 增减对象(物料需求,外购订单)
		psbwLog.setForObjId(oldplan.getId());
		psbwLog.setCount(waigouplan.getSbjdApplyCount());// 数量
		psbwLog.setClType("减少采购");// 处理方案
		psbwLog.setAddTime(Util.getDateTime());// 添加时间
		psbwLog.setUserName(user.getName());// 添加人
		psbwLog.setUserId(user.getId());// 添加人id
		psbwLog.setStatus("申请中");
		psbwLog.setClApplyId(wgPlancl.getId());
		totalDao.save(psbwLog);
		oldsbwg.setClUser(user.getName());
		oldsbwg.setClUserCode(user.getCode());
		oldsbwg.setClUserId(user.getId());
		oldsbwg.setClTime(Util.getDateTime());
		oldsbwg.setCltype("减少采购");
		return totalDao.update(oldsbwg) + "";
	}

	@Override
	public Object[] findPreProduct(ProcardReProduct preProduct, int pageNo,
			int pageSize, String pageStatus) {
		// TODO Auto-generated method stub
		if (preProduct == null) {
			preProduct = new ProcardReProduct();
		}
		String hql = totalDao.criteriaQueries(preProduct, null);
		hql += " order by id desc";
		List list = totalDao.findAllByPage(hql, pageNo, pageSize);
		int count = totalDao.getCount(hql);
		Object[] o = { list, count };
		return o;
	}

	@Override
	public ProcardReProduct getProcardReProductById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardReProduct) totalDao.getObjectById(
				ProcardReProduct.class, id);
	}

	@Override
	public Object[] getProcardReProductMsgById(Integer id) {
		// TODO Auto-generated method stub
		ProcardReProduct procardReProduct = (ProcardReProduct) totalDao
				.getObjectById(ProcardReProduct.class, id);
		if (procardReProduct != null) {
			// 获取工序
			ProcessInfor processInfor = (ProcessInfor) totalDao
					.getObjectByCondition(
							"from ProcessInfor where reProductId=?", id);
			// 获取图纸
			List<ProcardReProductFile> prpFileList = totalDao.query(
					"from ProcardReProductFile where reProductId=?", id);
			return new Object[] { procardReProduct, processInfor, prpFileList };
		}
		return null;
	}

	@Override
	public String savefxFile(ProcardReProductFile prpFile, Integer id) {
		// TODO Auto-generated method stub
		ProcardReProduct prp = (ProcardReProduct) totalDao.getObjectById(
				ProcardReProduct.class, id);
		if (prp == null) {
			return "没有找到对应的返修单";
		}
		ProcessInfor processInfor = (ProcessInfor) totalDao
				.getObjectByCondition("from ProcessInfor where reProductId=?",
						id);
		prpFile.setProcessNo(processInfor.getProcessNO());
		prpFile.setProcessName(processInfor.getProcessName());
		prpFile.setReProductId(id);
		prp.setStatus("文件上传");
		return totalDao.save(prpFile) + "";
	}

	@Override
	public Object[] sbzpgbm(ProcardTemplateBanBenApply bbAply,
			ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return new Object[] { "请先登录!" };
		}
		String nowTime = Util.getDateTime();
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		ptbbApply.setNeedDeptJudege(bbAply.getNeedDeptJudege());
		if (bbAply.getNeedDeptJudege().equals("是")) {
			String tzdeptS = gettzDept();
			String[] tzdeptS2 = null;
			if (!tzdeptS.equals("无")) {
				tzdeptS2 = tzdeptS.split(";");
			}
			// 内审人员
			Integer[] userId = null;
			if (ptbbJudges.getSelectUserId() != null
					&& ptbbJudges.getSelectUserId().length() > 0) {
				List<String> hadTzdeptList = new ArrayList<String>();
				String[] checkboxs = ptbbJudges.getSelectUserId().split(";");
				userId = new Integer[checkboxs.length];
				Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply
						.getPtbbjset();
				int j = 0;
				for (ProcardTemplateBanBenJudges had : ptbbjSet) {
					if (had.getId().equals(ptbbJudges.getId())) {
						had.setSelectUserId(ptbbJudges.getSelectUserId());
						had.setSelectUsers(ptbbJudges.getSelectUsers());
					}
				}
				for (String userIdStr : checkboxs) {
					Integer uId = Integer.parseInt(userIdStr);
					Users jUser = (Users) totalDao.getObjectById(Users.class,
							uId);
					if (jUser != null) {
						boolean hadj = false;
						for (ProcardTemplateBanBenJudges had : ptbbjSet) {
							if (had.getJudgeType().equals("内审")
									&& had.getUserId().equals(uId)) {
								hadj = true;
								break;
							}
						}
						if (hadj) {
							continue;
						}
						ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
						ptbbj.setDept(jUser.getDept());// 部门
						hadTzdeptList.add(jUser.getDept());
						ptbbj.setUserId(jUser.getId());// 评审人员id
						userId[j] = jUser.getId();
						ptbbj.setUserName(jUser.getName());// 评审名称
						ptbbj.setUserCode(jUser.getCode());// 用户
						// ptbbj.setremark;//评论
						// ptbbj.setsblv;//影响级别
						ptbbj.setAddTime(nowTime);// 添加时间
						// ptbbj.setplTime;//评论时间
						ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
						// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
						ptbbj.setJudgeType("内审");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
						ptbbj.setJudgedId(ptbbJudges.getId());
						ptbbjSet.add(ptbbj);
						totalDao.save(ptbbj);
						Calendar ca = new Calendar();
						ca.setTitle("各部门评审");
						ca.setThingContent(user.getName() + "对总成为:"
								+ ptbbApply.getMarkId() + ",业务件号为："
								+ bbAply.getYwMarkId() + "的BOM发起设变，请前去评审.设变单号:"
								+ ptbbApply.getSbNumber());
						ca.setStart(nowTime);
						ca.setAddDateTime(nowTime);
						ca.setUserId(jUser.getId());// 所属人id
						ca.setUserName(jUser.getName());// 所属人用户名称
						ca.setCode(jUser.getCode());// 所属人工号
						ca.setDept(jUser.getDept());// 所属人部门
						totalDao.save(ca);
						// 发送消息
						j++;
					}
				}
				if (tzdeptS2 != null) {
					StringBuffer notz = new StringBuffer();
					for (String must : tzdeptS2) {
						boolean tz = false;
						for (String tzdept : hadTzdeptList) {
							if (must.equals(tzdept)) {
								tz = true;
								break;
							}
						}
						if (!tz) {
							if (notz.length() == 0) {
								notz.append(must);
							} else {
								notz.append("," + must);
							}
						}
					}
					if (notz.length() > 0) {
						throw new RuntimeException(notz.toString()
								+ "为必要通知部门，请选择其下人员!");
					}
				}
				ptbbApply.setPtbbjset(ptbbjSet);
			} else {
				return new Object[] { "有关联生产数据请选择各部门评审人员!" };
			}
			AlertMessagesServerImpl.addAlertMessages("各部门评审", user.getName()
					+ "对总成为:" + ptbbApply.getMarkId() + ",业务件号为："
					+ ptbbApply.getYwMarkId() + "的BOM发起设变，请前去评审,设变单号:"
					+ ptbbApply.getSbNumber(), userId,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ ptbbApply.getId(), true, "no");
			ptbbApply.setProcessStatus("各部门评审");
			totalDao.update(ptbbApply);
			return new Object[] { "true", ptbbApply.getId() };
		} else {
			List<Procard> procardList = totalDao
					.query(
							"from Procard where id in(select procardId from ProcardAboutBanBenApply  where bbapplyId=?)",
							ptbbApply.getId());
			if (procardList != null && procardList.size() > 0) {
				return new Object[] { "有关联生产数据请选择各部门评审人员!" };
				// for (Procard procard : procardList) {
				// if (procard.getStatus().equals("设变锁定")) {
				// procard.setStatus(procard.getOldStatus());
				// totalDao.update(procard);
				// }
				// }
			}
			Float hasCount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcardAboutBanBenApply where bbapplyId=?",
							bbAply.getId());
			if (hasCount != null && hasCount > 0) {
				ptbbApply.setProcessStatus("结论");
			} else {
				ptbbApply.setProcessStatus("资料更新");
			}
			totalDao.update(ptbbApply);
			return new Object[] { "true", ptbbApply.getId() };
		}
	}

	@Override
	public Object[] findProcardSbWwList(ProcardSbWw procardSbWw, int cpage,
			int pageSize) {
		// TODO Auto-generated method stub
		if (procardSbWw == null) {
			procardSbWw = new ProcardSbWw();
		}
		String hql = totalDao.criteriaQueries(procardSbWw, null);
		List<ProcardSbWw> procardSbWwList = totalDao.query(hql
				+ " order by id desc");
		int count = totalDao.getCount(hql);
		return new Object[] { procardSbWwList, count };

	}

	@Override
	public Object[] findwwSbforJudeg(Integer id) {
		// TODO Auto-generated method stub
		ProcardSbWw procardSbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (procardSbww == null) {
			return null;
		}
		Set<ProcardSbWwDetail> procardSbWwDetailSet = procardSbww
				.getProcardSbWwDetailSet();
		if (procardSbWwDetailSet != null && procardSbWwDetailSet.size() > 0) {
			List<ProcardSbWwDetail> procardSbWwDetailList = new ArrayList<ProcardSbWwDetail>(
					procardSbWwDetailSet);
			procardSbww.setProcardSbWwDetailList(procardSbWwDetailList);
		}
		Object[] objs = new Object[2];
		objs[0] = procardSbww;
		if (procardSbww.getWwSource().equals("手动外委")) {
			if (procardSbww.getWaigouPlanId() != null) {
				WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
						WaigouPlan.class, procardSbww.getWaigouPlanId());
				wgPlan.setPlanNumber(wgPlan.getWaigouOrder().getPlanNumber());
				objs[1] = wgPlan;
			} else {
				ProcessInforWWApplyDetail wwapplyd = (ProcessInforWWApplyDetail) totalDao
						.getObjectById(ProcessInforWWApplyDetail.class,
								procardSbww.getWwDetailId());
				objs[1] = wwapplyd;
			}
		} else {
			if (procardSbww.getWaigouPlanId() != null) {
				WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
						WaigouPlan.class, procardSbww.getWaigouPlanId());
				wgPlan.setPlanNumber(wgPlan.getWaigouOrder().getPlanNumber());
				objs[1] = wgPlan;
			} else {
				String waigouWaiweiPlanIds = procardSbww
						.getWaigouWaiweiPlanIds();
				List<WaigouWaiweiPlan> wwPlanList = totalDao
						.query("from WaigouWaiweiPlan where id in("
								+ procardSbww.getWaigouWaiweiPlanIds() + ")");
				objs[1] = wwPlanList;
			}
		}
		return objs;
	}

	/**
	 * 终止外委订单
	 * 
	 * @param id
	 *            设变记录Id
	 * @return id2 外委订单Id
	 */
	@Override
	public String wwsbbreakwP(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id2);
		if (wgPlan == null || wgPlan.getWwSource() == null) {
			return "没有找到目标订单!";
		}
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, sbww
				.getProcardId());
		boolean tz = false;
		if (wgPlan.getWwSource().equals("手动外委")) {
			wgPlan.setStatus("协商取消");
			totalDao.update(wgPlan);
			ProcessInforWWApplyDetail wwApplyd = (ProcessInforWWApplyDetail) totalDao
					.getObjectById(ProcessInforWWApplyDetail.class, wgPlan
							.getWwDetailId());
			wwApplyd.setDataStatus("取消");
			totalDao.update(wwApplyd);
			sbww.setStatus("关闭");
			totalDao.update(sbww);
			tz = true;
		} else {
			// 查询BOM上的外委设定还在不在
			String msg = wwsbbreakBomwP(wgPlan, null, procard);
			if (msg.equals("true")) {
				List<ProcardWGCenter> pwgcList = totalDao
						.query(
								"from ProcardWGCenter where procardId=? and wgOrderId=?",
								procard.getId(), wgPlan.getId());
				if (pwgcList != null && pwgcList.size() > 0) {
					for (ProcardWGCenter pwgc : pwgcList) {
						if (procard.getZaizhikzkCount() > pwgc
								.getProcardCount()) {
							procard.setZaizhikzkCount(procard
									.getZaizhikzkCount()
									- pwgc.getProcardCount());
						} else {
							procard.setZaizhikzkCount(0f);
						}
						totalDao.update(procard);
						// 取消外委序列
						WaigouWaiweiPlan wwPlan = (WaigouWaiweiPlan) totalDao
								.getObjectById(WaigouWaiweiPlan.class, pwgc
										.getWwxlId());
						wwPlan.setStatus("取消");
						totalDao.update(procard);
					}
				}
				wgPlan.setStatus("协商取消");
				totalDao.update(wgPlan);
				sbww.setStatus("关闭");
				totalDao.update(sbww);
			} else {
				throw new RuntimeException(msg);
			}
		}
		if (tz) {// 通知供应商
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
					.getApplication().get("companyInfo");
			AlertMessagesServerImpl.addAlertMessages(wgPlan.getWaigouOrder()
					.getType()
					+ "订单设变停单协商", "尊敬的【"
					+ wgPlan.getWaigouOrder().getTzUserName() + "】,您好:\n订单号:【"
					+ wgPlan.getWaigouOrder().getPlanNumber()
					+ "】的订单发生设变需要停单请前往查看"
					+ wgPlan.getWaigouOrder().getLxPeople() + "("
					+ wgPlan.getWaigouOrder().getGysPhone() + ")。\n【"
					+ wgPlan.getWaigouOrder().getGysName() + "】\n["
					+ companyInfo.getName() + "]", "1", wgPlan.getWaigouOrder()
					.getAddUserCode());
		}
		// 查询此BOM下是否还有未处理完的外委待处理项
		Float wclcount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
						procard.getRootId());
		if (wclcount == null || wclcount == 0) {
			List<Procard> wgProcardList = totalDao
					.query(
							"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
							sbww.getPtbbApplyId(), procard.getRootId());
			if (wgProcardList != null && wgProcardList.size() > 0) {
				for (Procard wg : wgProcardList) {
					procardServer.jihuoSingleProcard(wg);
				}
			}
		}
		return "true";
	}

	public String wwsbbreakBomwP(WaigouPlan wgPlan, WaigouWaiweiPlan wwPlan,
			Procard procard) {
		// 1.工序拆分
		String[] processsnos = null;
		String[] processnames = null;
		if (wgPlan != null) {
			processsnos = wgPlan.getProcessNOs().split(";");
			processnames = wgPlan.getProcessNames().split(";");
		} else {
			processsnos = wwPlan.getProcessNo().split(";");
			processnames = wwPlan.getProcessName().split(";");
		}
		Integer fno = Integer.parseInt(processsnos[0]);
		String fname = processnames[0];
		List<String> processsNoAndName = new ArrayList<String>();
		if (processsnos.length != processnames.length) {
			return "外委工序号和工序名称不匹配,数据异常!";
		}
		for (int i = 0; i < processsnos.length; i++) {
			processsNoAndName.add(processsnos[i] + processnames[i]);
		}
		if (procard.getSbStatus() == null
				|| !procard.getSbStatus().equals("删除")) {
			List<ProcessInfor> processInforList = totalDao
					.query(
							"from ProcessInfor where procard.id = ? and (dataStatus is null or dataStatus!='删除') order by processNO",
							procard.getId());
			if (processInforList != null && processInforList.size() > 0) {
				// 初步遍历判断是否有同工序号同工序名称的
				boolean hadSame = false;
				for (ProcessInfor hadSamep : processInforList) {
					if (hadSamep.getProcessNO().equals(fno)
							|| hadSamep.getProcessName().equals(fname)) {
						hadSame = true;
					}
				}
				boolean ww = false;// 上道工序是否外委
				int wwn = 0;// 计数
				// Float zzhastjgx = null;//上道自制工序已提交数量
				// Float hastjgx = null;//上道工序已提交数量
				for (ProcessInfor process : processInforList) {
					if (process.getProductStyle().equals("自制")) {
						ww = false;
						// if(process.getProcessNO().equals(fno)){//工序号相同
						// break;//原外委起始工序变成自制此自制必定还未开始
						// }
						// hastjgx=process.getSubmmitCount();
						// zzhastjgx=process.getSubmmitCount();
						if (process.getSubmmitCount() == 0) {
							break;// 有前工序未提交可停止外委订单
						}
					} else {
						if (process.getProcessNO().equals(fno)) {// 工序号相同
							return "由于此外委单来源BOM设变,设变之后仍需外委不能取消此订单,请选择继续或者替换!";
						} else {
							if (hadSame) {// 存在同工序号通过工序名,继续往下看情况

							} else {
								if (process.getProcessName().equals(fname)) {// 名字相同的外委工序,此工序前置
									return "由于此外委单来源BOM设变,设变之后仍需外委不能取消此订单,请选择继续或者替换!";
								} else {
									// 继续往下看情况
								}
							}
							ww = true;
							// hastjgx=process.getSubmmitCount();
						}
					}
				}

			} else {// 没有工序则可以停止
				if (wwPlan != null) {
					wgPlan.setStatus("协商取消");
					totalDao.update(wgPlan);
				} else {
					wwPlan.setStatus("取消");
					totalDao.update(wwPlan);
				}
				return "true";
			}
		} else {
			// 表明之前的生产卡状态标记为删除则可停止
		}
		return "true";
	}

	@Override
	public Object[] toUpdateSbwp(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return new Object[] { "请先登录!" };
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return new Object[] { "没有找到目标设变外委影响记录!" };
		}
		WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id2);
		if (wgPlan == null || wgPlan.getWwSource() == null) {
			return new Object[] { "没有找到目标订单!" };
		}
		boolean tz = false;
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, sbww
				.getProcardId());
		if (wgPlan.getWwSource().equals("手动外委")) {
			if (procard.getSbStatus().equals("删除")) {// 设变删除了此零件
				// 获取此零件更新后的零件
				Procard newprocard = (Procard) totalDao
						.getObjectByCondition(
								"from Procard where procard.id=? and markId=? and (sbStatus is null or sbStatus!='删除')",
								procard.getFatherId(), procard.getMarkId());
				if (newprocard == null) {
					wgPlan.setStatus("协商取消");
					totalDao.update(wgPlan);
					ProcessInforWWApplyDetail wwApplyd = (ProcessInforWWApplyDetail) totalDao
							.getObjectById(ProcessInforWWApplyDetail.class,
									wgPlan.getWwDetailId());
					wwApplyd.setDataStatus("取消");
					totalDao.update(wwApplyd);
					sbww.setStatus("关闭");
					totalDao.update(sbww);
					// 查询此BOM下是否还有未处理完的外委待处理项
					Float wclcount = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
									procard.getRootId());
					if (wclcount == null || wclcount == 0) {
						List<Procard> wgProcardList = totalDao
								.query(
										"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
										sbww.getPtbbApplyId(), procard
												.getRootId());
						if (wgProcardList != null && wgProcardList.size() > 0) {
							for (Procard wg : wgProcardList) {
								procardServer.jihuoSingleProcard(wg);
							}
						}
					}
					// 通知供应商
					CompanyInfo companyInfo = (CompanyInfo) ActionContext
							.getContext().getApplication().get("companyInfo");
					AlertMessagesServerImpl.addAlertMessages(wgPlan
							.getWaigouOrder().getType()
							+ "订单设变停单协商", "尊敬的【"
							+ wgPlan.getWaigouOrder().getTzUserName()
							+ "】,您好:\n订单号:【"
							+ wgPlan.getWaigouOrder().getPlanNumber()
							+ "】的订单发生设变需要停单请前往查看"
							+ wgPlan.getWaigouOrder().getLxPeople() + "("
							+ wgPlan.getWaigouOrder().getGysPhone() + ")。\n【"
							+ wgPlan.getWaigouOrder().getGysName() + "】\n["
							+ companyInfo.getName() + "]", "1", wgPlan
							.getWaigouOrder().getAddUserCode());

					return new Object[] { "true1", "没有找到设变后此订单对应的生产零件,已取消此订单!" };
				} else {
					List<ProcessInfor> processList = totalDao
							.query(
									"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除' order by processNO)",
									newprocard.getId());
					return new Object[] { "true", sbww, wgPlan, newprocard,
							processList };
				}
			} else {// 获取工序并比对
				ProcessInforWWApplyDetail wwApplyd = (ProcessInforWWApplyDetail) totalDao
						.getObjectById(ProcessInforWWApplyDetail.class, wgPlan
								.getWwDetailId());
				procard.setWwCount(wwApplyd.getApplyCount());
				List<ProcessInfor> processList = totalDao
						.query(
								"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除' order by processNO)",
								procard.getId());
				return new Object[] { "true", sbww, wgPlan, procard,
						processList };
			}

		} else if (wgPlan.getWwSource().equals("BOM外委")) {
			if (procard.getSbStatus().equals("删除")) {
				wgPlan.setStatus("协商取消");
				totalDao.update(wgPlan);
				List<WaigouWaiweiPlan> wwplanList = totalDao
						.query(
								"from WaigouWaiweiPlan where id in(select wwxlId from ProcardWGCenter where id=)",
								wgPlan.getId());
				if (wwplanList != null && wwplanList.size() > 0) {
					for (WaigouWaiweiPlan wwplan : wwplanList) {
						wwplan.setStatus("取消");
						totalDao.update(wwplan);
					}
				}
				sbww.setStatus("关闭");
				totalDao.update(sbww);
				// 查询此BOM下是否还有未处理完的外委待处理项
				Float wclcount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
								procard.getRootId());
				if (wclcount == null || wclcount == 0) {
					List<Procard> wgProcardList = totalDao
							.query(
									"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
									sbww.getPtbbApplyId(), procard.getRootId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (Procard wg : wgProcardList) {
							procardServer.jihuoSingleProcard(wg);
						}
					}
				}
				// 通知供应商
				CompanyInfo companyInfo = (CompanyInfo) ActionContext
						.getContext().getApplication().get("companyInfo");
				AlertMessagesServerImpl.addAlertMessages(wgPlan
						.getWaigouOrder().getType()
						+ "订单设变停单协商", "尊敬的【"
						+ wgPlan.getWaigouOrder().getTzUserName()
						+ "】,您好:\n订单号:【"
						+ wgPlan.getWaigouOrder().getPlanNumber()
						+ "】的订单发生设变需要停单请前往查看"
						+ wgPlan.getWaigouOrder().getLxPeople() + "("
						+ wgPlan.getWaigouOrder().getGysPhone() + ")。\n【"
						+ wgPlan.getWaigouOrder().getGysName() + "】\n["
						+ companyInfo.getName() + "]", "1", wgPlan
						.getWaigouOrder().getAddUserCode());
				return new Object[] { "true1", "此生产件已被替换,已直接取消订单!" };
			}
			List<ProcessInfor> processList = totalDao
					.query(
							"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除' order by processNO)",
							procard.getId());
			return new Object[] { "true", sbww, wgPlan, procard, processList };
		}

		return null;
	}

	@Override
	public String updateSbsdwp(ProcessInforWWApplyDetail detail, Integer id,
			Integer id2, Procard procard, int[] checkboxs) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id2);
		if (wgPlan == null || wgPlan.getWwSource() == null) {
			return "没有找到目标订单!";
		}
		String msg = "";
		Procard oldProcard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getId());
		List<ProcessInfor> processList = totalDao
				.query(
						"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') order by processNO",
						procard.getId());
		ProcessInforWWApply wwApply = (ProcessInforWWApply) totalDao
				.getObjectByCondition(
						"from ProcessInforWWApply where status='未申请' and procardId=?",
						procard.getRootId());
		if (sbww.getProcardId().equals(procard.getId())) {
			String[] processsnos = wgPlan.getProcessNOs().split(";");
			String[] processnames = wgPlan.getProcessNames().split(";");
			Integer fno = Integer.parseInt(processsnos[0]);
			String fname = processnames[0];
			Integer eno = Integer.parseInt(processsnos[processsnos.length - 1]);
			String ename = processnames[processnames.length - 1];
			// 清理之前的申请数量
			boolean first = false;
			for (ProcessInfor process : processList) {
				if (process.getProcessNO() >= fno
						|| process.getProcessNO().equals(fname)) {// 初始外委工序之后
					boolean ww = false;
					if (process.getAgreeWwCount() > 0) {
						first = true;// 判断已经遇到初始外委工序
						ww = true;
						if (wgPlan.getNumber() >= process.getAgreeWwCount()) {
							process.setAgreeWwCount(0f);
						} else {
							process.setAgreeWwCount(process.getAgreeWwCount()
									- wgPlan.getNumber());
						}
					}
					if (process.getProcessNO().equals(eno)) {
						if (process.getProcessName().equals(ename)) {// 最后外委工序
							break;
						}
						if (first && !ww) {// 断掉外委工序之后
							break;
						}
					} else if (process.getProcessNO() > eno && first && !ww) {// 断掉外委工序之后
						break;
					}
				}
			}
		}
		Procard totalCard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getRootId());
		Set<ProcessInforWWApplyDetail> detailSet = null;
		if (wwApply == null) {
			wwApply = new ProcessInforWWApply();
			wwApply.setFinalCount(totalCard.getFilnalCount());
			wwApply.setProcardId(totalCard.getId());
			wwApply.setYwMarkId(totalCard.getYwMarkId());// 业务件号
			wwApply.setMarkId(totalCard.getMarkId());// 总成件号
			wwApply.setProName(totalCard.getProName());// 总成名称
			wwApply.setSelfCard(totalCard.getSelfCard());// 总成批次
			wwApply.setNumber(totalCard.getFilnalCount());// 数量
			wwApply.setOrderNumber(totalCard.getOrderNumber());// 内部订单号
			wwApply.setOrderNumber(totalCard.getOrderNumber());// 内部订单号
			wwApply.setProcessStatus("预选未审批");
			wwApply.setStatus("未申请");
			detailSet = new HashSet<ProcessInforWWApplyDetail>();
		} else {
			detailSet = wwApply.getProcessInforWWApplyDetails();
		}

		detail.setProcessStatus("预选未审批");
		detail.setProcardId(procard.getId());// 零件ID
		detail.setMarkId(procard.getMarkId());// 件号
		detail.setProName(procard.getProName());// 零件名
		detail.setSelfCard(procard.getSelfCard());// 批次
		detail.setUserId(user.getId());// 选择人id
		detail.setUserName(user.getName());// 选择人名字
		detail.setUserCode(user.getCode());// 选择人工号
		detail.setAddTime(Util.getDateTime());// 选择时间
		detail.setBanbenNumber(procard.getBanBenNumber());
		detail.setBanci(procard.getBanci());
		detail.setUnit(procard.getUnit());
		StringBuffer pnosb = new StringBuffer();
		StringBuffer pnamesb = new StringBuffer();
		for (int processId : checkboxs) {
			ProcessInfor old = (ProcessInfor) totalDao.getObjectByCondition(
					"from ProcessInfor where procard.id=? and id=?", procard
							.getId(), processId);
			if (old != null) {
				if (pnosb.length() == 0) {
					pnosb.append(old.getProcessNO());
					pnamesb.append(old.getProcessName());
				} else {
					pnosb.append(";" + old.getProcessNO());
					pnamesb.append(";" + old.getProcessName());

				}
				if (old.getSelectWwCount() == null) {
					old.setSelectWwCount(0f);
				}
				if (old.getApplyWwCount() == null) {
					old.setApplyWwCount(0f);
				}
				if (old.getAgreeWwCount() == null) {
					old.setAgreeWwCount(0f);
				}
				Float wwblCount = 0f;
				if (procard.getWwblCount() != null) {
					wwblCount = procard.getWwblCount();
				}
				Float syCount = procard.getFilnalCount() - wwblCount
						- old.getApplyCount() - old.getSelectWwCount()
						- old.getApplyWwCount() - old.getAgreeWwCount();
				if (syCount < detail.getApplyCount()) {
					msg += "第" + old.getProcessNO() + "工序:"
							+ old.getProcessName() + "最多可预选" + syCount
							+ procard.getUnit() + "!\n";
				} else {
					old.setSelectWwCount(old.getSelectWwCount()
							+ detail.getApplyCount());
					totalDao.update(old);
				}
			}
		}
		detail.setProcessNOs(pnosb.toString());
		detail.setProcessNames(pnosb.toString());
		Price price = (Price) totalDao
				.getObjectByCondition(
						"from Price where wwType=? and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
								+ Util.getDateTime() + "')  order by hsPrice",
						detail.getWwType(), detail.getMarkId(), detail
								.getProcessNOs());
		if (price != null) {
			detail.setPriceId(price.getId());
			detail.setGysId(price.getGysId());
			String gysName = (String) totalDao.getObjectByCondition(
					"select name from ZhUser where id=?", price.getGysId());
			detail.setGysName(gysName);
		}
		detail.setProcessInforWWApply(wwApply);
		totalDao.save(detail);
		// 关联外购件
		String wwMarkId = detail.getWwMarkId();
		if (wwMarkId != null && wwMarkId.length() > 0) {
			// 包含外购件
			// 拆分外购件回馈采购
			String[] markIds = wwMarkId.split(";");
			if (markIds != null && markIds.length > 0) {
				for (String wgMarkId : markIds) {
					Procard wgProcard = (Procard) totalDao
							.getObjectByCondition(
									"from Procard where markId=? and procard.id=?",
									wgMarkId, procard.getId());
					if (wgProcard != null) {
						ProcessInforWWProcard processwwprocard = new ProcessInforWWProcard();
						processwwprocard.setProcardId(wgProcard.getId());// 零件id
						processwwprocard.setMarkId(wgProcard.getMarkId());// 件号
						processwwprocard.setProcName(wgProcard.getProName());// 名称
						processwwprocard.setBanben(wgProcard.getBanBenNumber());// 版本号
						processwwprocard.setBanci(wgProcard.getBanci());// 版次
						processwwprocard.setApplyCount(detail.getApplyCount()
								* wgProcard.getQuanzi2()
								/ wgProcard.getQuanzi1());// 数量
						processwwprocard.setHascount(processwwprocard
								.getApplyCount());
						processwwprocard.setStatus("使用");// 状态
						processwwprocard.setApplyDtailId(detail.getId());
						totalDao.save(processwwprocard);
						if (detail.getWwType() != null
								&& detail.getWwType().equals("包工包料")) {// 包工包料回传采购
							if (wgProcard.getWwblCount() == null) {// 外委包料数量
								wgProcard.setWwblCount(detail.getApplyCount()
										* wgProcard.getQuanzi2()
										/ wgProcard.getQuanzi1());
							} else {
								wgProcard.setWwblCount(wgProcard.getWwblCount()
										+ detail.getApplyCount()
										* wgProcard.getQuanzi2()
										/ wgProcard.getQuanzi1());
							}
							totalDao.update(wgProcard);
						}
					} else {
						throw new RuntimeException("对不起该零件下没有找到件号为:" + wgMarkId
								+ "的外购件");
					}
				}
			}

		}
		detailSet.add(detail);
		if (detail.getWwType() != null && detail.getWwType().equals("包工包料")) {
			// 关联下层半成品,自制件和组合（下层组合将整体被包公包料）
			procardServer.updateProcardWwblCount(procard, detail
					.getApplyCount(), 0);
		}
		if (detail.getWwType().equals("工序外委")
				&& detail.getRelatDown().equals("是")) {
			// 外委下层
			msg += procardServer.updateDownWwProcess(procard, detail
					.getApplyCount(), detail.getId(), 0);

		}
		if (msg.length() > 0) {
			throw new RuntimeException(msg);
		}
		wgPlan.setStatus("协商取消");
		totalDao.update(wgPlan);
		sbww.setStatus("关闭");
		totalDao.update(sbww);
		// 通知供应商
		CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
				.getApplication().get("companyInfo");
		AlertMessagesServerImpl.addAlertMessages(wgPlan.getWaigouOrder()
				.getType()
				+ "订单设变停单协商", "尊敬的【" + wgPlan.getWaigouOrder().getTzUserName()
				+ "】,您好:\n订单号:【" + wgPlan.getWaigouOrder().getPlanNumber()
				+ "】的订单发生设变需要停单请前往查看" + wgPlan.getWaigouOrder().getLxPeople()
				+ "(" + wgPlan.getWaigouOrder().getGysPhone() + ")。\n【"
				+ wgPlan.getWaigouOrder().getGysName() + "】\n["
				+ companyInfo.getName() + "]", "1", wgPlan.getWaigouOrder()
				.getAddUserCode());
		// 查询此BOM下是否还有未处理完的外委待处理项
		Float wclcount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
						oldProcard.getRootId());
		if (wclcount == null || wclcount == 0) {
			List<Procard> wgProcardList = totalDao
					.query(
							"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
							sbww.getPtbbApplyId(), oldProcard.getRootId());
			if (wgProcardList != null && wgProcardList.size() > 0) {
				for (Procard wg : wgProcardList) {
					procardServer.jihuoSingleProcard(wg);
				}
			}
		}
		return "true";
	}

	@Override
	public String updateSbbomwp(Integer id, Integer id2, Procard procard,
			int[] checkboxs) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id2);
		if (wgPlan == null || wgPlan.getWwSource() == null) {
			return "没有找到目标订单!";
		}
		String msg = "";
		Procard oldProcard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getId());
		List<ProcessInfor> processList = totalDao
				.query(
						"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') order by processNO",
						procard.getId());
		int fid = checkboxs[0];
		boolean upww = false;//
		boolean haspp = false;
		Float upCount = null;// 上道工序已提交数量
		if (processList != null && processList.size() > 0) {
			StringBuffer pnosb = new StringBuffer();
			StringBuffer pnamesb = new StringBuffer();
			Float alljiepai = 0f;
			Float deliveryDuration = 0f;
			Float wwCount = 0f;
			Integer upPno = null;
			String upPname = null;
			for (ProcessInfor process : processList) {
				if (!false) {
					upCount = process.getSubmmitCount();
					upPno = process.getProcessNO();
					upPname = process.getProcessName();
				}
				if (process.getId().equals(fid)) {
					if (process.getProductStyle().equals("自制")) {
						return process.getProcessNO() + "工序不是外委工序请重新选择";
					}
					haspp = true;
					pnosb.append(process.getProcessNO());
					pnosb.append(process.getProcessName());
					if (process.getAllJiepai() != null) {
						alljiepai += process.getAllJiepai();
					}
					if (process.getDeliveryDuration() != null) {
						deliveryDuration += process.getDeliveryDuration();
					}
					wwCount = upCount;
					if (upww && process.getProcessStatus().equals("yes")) {// 上道工序为外委且与此工序并行
						return "上道工序为外委且与此工序并行,请一起选择!";
					}
					upww = true;
				} else if (process.getProductStyle().equals("外委")) {
					if (haspp) {// 此工序一起外委
						if (process.getProcessStatus().equals("yes")) {
							pnosb.append(";" + process.getProcessNO());
							pnosb.append(";" + process.getProcessName());
							if (process.getAllJiepai() != null) {
								alljiepai += process.getAllJiepai();
							}
							if (process.getDeliveryDuration() != null) {
								deliveryDuration += process
										.getDeliveryDuration();
							}
						} else {
							break;
						}
					}
					upww = true;
				} else if (haspp && process.getProductStyle().equals("自制")) {
					break;
				}
			}
			if (wwCount != null && wwCount > 0) {
				// 查询之前已外委的工序数量
				Float hasww = (Float) totalDao
						.getObjectByCondition(
								"select sum(beginCount) from WaigouWaiweiPlan where markId = ? and selfCard =? and processNo=? and processName=? and status !='取消'",
								oldProcard.getMarkId(), oldProcard
										.getSelfCard(), pnosb.toString(),
								pnamesb.toString());
				if (wwCount > hasww) {
					// 添加委外待激活序列
					WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
					wwp.setRootMarkId(oldProcard.getRootMarkId());
					wwp.setRootSelfCard(oldProcard.getRootSelfCard());
					wwp.setOrderNum(oldProcard.getOrderNumber());
					wwp.setYwMarkId(oldProcard.getYwMarkId());
					wwp.setBanben(oldProcard.getBanBenNumber());
					wwp.setBanci(oldProcard.getBanci());
					wwp.setMarkId(oldProcard.getMarkId());
					wwp.setProcessNo(pnosb.toString());
					wwp.setProName(oldProcard.getProName());
					wwp.setProcessName(pnamesb.toString());
					wwp.setType("外委");
					wwp.setNumber(wwCount - hasww);
					wwp.setUnit(oldProcard.getUnit());
					wwp.setBeginCount(wwCount - hasww);
					wwp.setAddTime(Util.getDateTime());
					wwp.setJihuoTime(Util.getDateTime());
					wwp.setShArrivalTime(oldProcard.getNeedFinalDate());// 应到货时间在采购确认通知后计算
					wwp.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
					String wwNumber = "";
					String before = null;
					Integer bIndex = 10;
					before = "ww" + Util.getDateTime("yyyyMMdd");
					Integer maxNo = 0;
					String maxNumber = (String) totalDao
							.getObjectByCondition("select max(planNumber) from WaigouWaiweiPlan where planNumber like '"
									+ before + "%'");
					if (maxNumber != null) {
						String wwnum = maxNumber.substring(bIndex, maxNumber
								.length());
						try {
							Integer maxNum = Integer.parseInt(wwnum);
							if (maxNum > maxNo) {
								maxNo = maxNum;
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					maxNo++;
					wwNumber = before + String.format("%03d", maxNo);
					wwp.setPlanNumber(wwNumber);// 采购计划编号
					wwp.setSelfCard(oldProcard.getSelfCard());// 批次
					// wwp.setGysId(nextWwProcessInfor
					// .getZhuserId());// 供应商id
					// wwp.setGysName(nextWwProcessInfor
					// .getGys());// 供应商名称
					wwp.setAllJiepai(alljiepai);// 单件总节拍
					wwp.setDeliveryDuration(deliveryDuration);// 耽误时长
					wwp.setSingleDuration(oldProcard.getSingleDuration());// 单班时长(工作时长)
					wwp.setProcardId(oldProcard.getId());
					wwp.setProcard(oldProcard);
					// if (wwckCount != null
					// && wwckCount > 0) {
					wwp.setStatus("待入库");
					// 在制品待入库
					if (oldProcard.getZaizhiApplyZk() == null) {
						oldProcard.setZaizhiApplyZk(0f);
					}
					if (oldProcard.getZaizhizkCount() == null) {
						oldProcard.setZaizhizkCount(0f);
					}
					if (oldProcard.getKlNumber() == null) {
						oldProcard.setKlNumber(oldProcard.getFilnalCount());
					}
					if (oldProcard.getHascount() == null) {
						oldProcard.setHascount(oldProcard.getKlNumber());
					}
					// procard.getKlNumber()-procard.getHascount()=已生产数量
					// 可转库数量=已生产数量-已转库数量-转库申请中数量
					// 先还原之前的数量
					if (sbww.getProcardId().equals(oldProcard.getId())) {
						if (oldProcard.getZaizhizkCount() != null) {
							Float aboutCount = (Float) totalDao
									.getObjectByCondition(
											"select sum(procardCount) from ProcardWGCenter where wgOrderId=? and procardId=?",
											wgPlan.getId(), oldProcard.getId());
							if (aboutCount != null) {
								if (oldProcard.getZaizhizkCount() > aboutCount) {
									oldProcard.setZaizhizkCount(oldProcard
											.getZaizhizkCount()
											- aboutCount);
								} else {
									oldProcard.setZaizhizkCount(0f);
								}
							}
						}
					}
					oldProcard.setZaizhikzkCount(oldProcard.getFilnalCount()
							- oldProcard.getZaizhizkCount()
							- oldProcard.getZaizhiApplyZk());
					if (oldProcard.getZaizhikzkCount() >= wwCount) {
						oldProcard.setZaizhiApplyZk(oldProcard
								.getZaizhiApplyZk()
								+ wwCount);
						String orderNum = (String) totalDao
								.getObjectByCondition(
										"select orderNumber from Procard where id=?",
										oldProcard.getRootId());
						// 成品待入库
						GoodsStore goodsStore2 = new GoodsStore();
						goodsStore2.setNeiorderId(orderNum);
						goodsStore2.setWaiorderId(procard.getOutOrderNum());
						goodsStore2.setProcardId(procard.getId());
						goodsStore2.setGoodsStoreMarkId(oldProcard.getMarkId());
						goodsStore2.setBanBenNumber(oldProcard
								.getBanBenNumber());
						goodsStore2.setGoodsStoreLot(oldProcard.getSelfCard());
						goodsStore2.setGoodsStoreGoodsName(oldProcard
								.getProName());
						goodsStore2.setApplyTime(Util.getDateTime());
						goodsStore2
								.setGoodsStoreArtsCard((String) totalDao
										.getObjectByCondition(
												"select selfCard from Procard where id=?",
												oldProcard.getRootId()));
						goodsStore2.setGoodsStorePerson(Util.getLoginUser()
								.getName());
						goodsStore2.setStatus("待入库");
						goodsStore2.setStyle("半成品转库");
						goodsStore2.setGoodsStoreWarehouse("委外库");// 库别
						// goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());//
						// 区名
						// goodsStore2.setGoodsStorePosition(goodsStore.getGoodsStorePosition());//
						// 库位
						goodsStore2.setGoodsStoreUnit(oldProcard.getUnit());
						goodsStore2.setGoodsStoreCount(wwCount);
						goodsStore2.setProcessNo(upPno);
						goodsStore2.setProcessName(upPname);
						totalDao.update(oldProcard);
						totalDao.save(goodsStore2);
						// 判断外委进委外入库是否要做
						String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='委外库接收半成品' and valueName='委外库接收半成品'";
						String valueCode = (String) totalDao
								.getObjectByCondition(hql1);
						if (valueCode != null && valueCode.equals("否")) {
							// 入库记录直接通过
							goodsStore2.setStatus("入库");
							goodsStore2.setPrintStatus("YES");
							totalDao.update(goodsStore2);
							// 增加库存记录
							String hqlgoods = "from Goods where goodsMarkId='"
									+ oldProcard.getMarkId()
									+ "' and goodsLotId='"
									+ oldProcard.getSelfCard()
									+ "' and goodsStyle='半成品转库' and processNo="
									+ upPno + " and goodsClass='委外库'";
							Goods goods = (Goods) totalDao
									.getObjectByCondition(hqlgoods);
							if (goods != null) {
								goods.setGoodsCurQuantity(goods
										.getGoodsCurQuantity()
										+ goodsStore2.getGoodsStoreCount());
								totalDao.update(goods);
							} else {
								goods = new Goods();
								goods.setGoodsMarkId(goodsStore2
										.getGoodsStoreMarkId());
								goods.setGoodsFormat(goodsStore2
										.getGoodsStoreFormat());
								goods.setBanBenNumber(goodsStore2
										.getBanBenNumber());
								goods.setGoodsFullName(goodsStore2
										.getGoodsStoreGoodsName());
								goods.setGoodsClass("委外库");
								goods.setGoodsBeginQuantity(goodsStore2
										.getGoodsStoreCount());
								goods.setGoodsCurQuantity(goodsStore2
										.getGoodsStoreCount());
								totalDao.save(goods);
							}
							// 添加零件与在制品关系表
							ProcardProductRelation pprelation = new ProcardProductRelation();
							pprelation.setAddTime(Util.getDateTime());
							pprelation.setProcardId(oldProcard.getId());
							pprelation.setGoodsId(goods.getGoodsId());
							pprelation.setZyCount(goodsStore2
									.getGoodsStoreCount());
							pprelation.setFlagType("本批在制品");
							totalDao.save(pprelation);
							// 将外购外委激活序列状态改为待激活
							wwp.setStatus("待激活");
							// totalDao.save(wwp);
						}
					} else {
						return "对不起超过可申请数量(" + procard.getZaizhikzkCount()
								+ ")";
					}
					// } else {
					// wwp.setStatus("待激活");
					// }
					totalDao.save(wwp);
					// wgSet.add(wwp);
					if (wwp.getId() != null) {
						// 匹配供应商
						Price price = (Price) totalDao
								.getObjectByCondition(
										"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
												+ Util.getDateTime()
												+ "')  order by hsPrice", wwp
												.getMarkId(), wwp
												.getProcessNo());
						if (price != null) {
							wwp.setPriceId(price.getId());
							wwp.setGysId(price.getGysId());
							ZhUser zhUser = (ZhUser) totalDao.getObjectById(
									ZhUser.class, price.getGysId());
							wwp.setGysName(zhUser.getName());
							wwp.setUserCode(zhUser.getUsercode());
							wwp.setUserId(zhUser.getUserid());
							totalDao.update(wwp);
						}
					}
					if (wwp.getStatus() != null
							&& wwp.getStatus().equals("待激活")) {// 说明自动跳过了半成品入委外库操作
						// 下一步操作
						procardServer.zijihuoww(wwp);
					}

				}
			}
			wgPlan.setStatus("协商取消");
			totalDao.update(wgPlan);
			List<WaigouWaiweiPlan> wwplanList = totalDao
					.query(
							"from WaigouWaiweiPlan where id in(select wwxlId from ProcardWGCenter where id=)",
							wgPlan.getId());
			if (wwplanList != null && wwplanList.size() > 0) {
				for (WaigouWaiweiPlan wwplan : wwplanList) {
					wwplan.setStatus("取消");
					totalDao.update(wwplan);
				}
			}
			sbww.setStatus("关闭");
			totalDao.update(sbww);
			// 通知供应商
			CompanyInfo companyInfo = (CompanyInfo) ActionContext.getContext()
					.getApplication().get("companyInfo");
			AlertMessagesServerImpl.addAlertMessages(wgPlan.getWaigouOrder()
					.getType()
					+ "订单设变停单协商", "尊敬的【"
					+ wgPlan.getWaigouOrder().getTzUserName() + "】,您好:\n订单号:【"
					+ wgPlan.getWaigouOrder().getPlanNumber()
					+ "】的订单发生设变需要停单请前往查看"
					+ wgPlan.getWaigouOrder().getLxPeople() + "("
					+ wgPlan.getWaigouOrder().getGysPhone() + ")。\n【"
					+ wgPlan.getWaigouOrder().getGysName() + "】\n["
					+ companyInfo.getName() + "]", "1", wgPlan.getWaigouOrder()
					.getAddUserCode());
			// 查询此BOM下是否还有未处理完的外委待处理项
			Float wclcount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
							oldProcard.getRootId());
			if (wclcount == null || wclcount == 0) {
				List<Procard> wgProcardList = totalDao
						.query(
								"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
								sbww.getPtbbApplyId(), oldProcard.getRootId());
				if (wgProcardList != null && wgProcardList.size() > 0) {
					for (Procard wg : wgProcardList) {
						procardServer.jihuoSingleProcard(wg);
					}
				}
			}
		}
		return "true";
	}

	@Override
	public String wwsbbreaksdd(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		ProcessInforWWApplyDetail wwApplyd = (ProcessInforWWApplyDetail) totalDao
				.getObjectById(ProcessInforWWApplyDetail.class, id2);
		if (wwApplyd == null) {
			return "没有找到目标委外申请单!";
		}
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, sbww
				.getProcardId());
		boolean tz = false;

		wwApplyd.setDataStatus("取消");
		totalDao.update(wwApplyd);
		sbww.setStatus("关闭");
		totalDao.update(sbww);
		// 查询此BOM下是否还有未处理完的外委待处理项
		Float wclcount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
						procard.getRootId());
		if (wclcount == null || wclcount == 0) {
			List<Procard> wgProcardList = totalDao
					.query(
							"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
							sbww.getPtbbApplyId(), procard.getRootId());
			if (wgProcardList != null && wgProcardList.size() > 0) {
				for (Procard wg : wgProcardList) {
					procardServer.jihuoSingleProcard(wg);
				}
			}
		}
		return "true";
	}

	@Override
	public String wwsbbreakbomwwp(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouWaiweiPlan wwPlan = (WaigouWaiweiPlan) totalDao.getObjectById(
				WaigouWaiweiPlan.class, id2);
		if (wwPlan == null) {
			return "没有找到目标订单!";
		}
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, sbww
				.getProcardId());
		boolean tz = false;
		// 查询BOM上的外委设定还在不在
		String msg = wwsbbreakBomwP(null, wwPlan, procard);
		if (msg.equals("true")) {
			List<ProcardWGCenter> pwgcList = totalDao.query(
					"from ProcardWGCenter where procardId=? and wwxlId=?",
					procard.getId(), wwPlan.getId());
			if (pwgcList != null && pwgcList.size() > 0) {
				for (ProcardWGCenter pwgc : pwgcList) {
					if (procard.getZaizhikzkCount() > pwgc.getProcardCount()) {
						procard.setZaizhikzkCount(procard.getZaizhikzkCount()
								- pwgc.getProcardCount());
					} else {
						procard.setZaizhikzkCount(0f);
					}
					totalDao.update(procard);
				}
			}
			sbww.setStatus("关闭");
			totalDao.update(sbww);
		} else {
			throw new RuntimeException(msg);
		}
		// 查询此BOM下是否还有未处理完的外委待处理项
		Float wclcount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
						procard.getRootId());
		if (wclcount == null || wclcount == 0) {
			List<Procard> wgProcardList = totalDao
					.query(
							"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
							sbww.getPtbbApplyId(), procard.getRootId());
			if (wgProcardList != null && wgProcardList.size() > 0) {
				for (Procard wg : wgProcardList) {
					procardServer.jihuoSingleProcard(wg);
				}
			}
		}
		return "true";
	}

	@Override
	public Object[] toUpdatesdd(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return new Object[] { "请先登录!" };
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return new Object[] { "没有找到目标设变外委影响记录!" };
		}
		ProcessInforWWApplyDetail wwApplyd = (ProcessInforWWApplyDetail) totalDao
				.getObjectById(ProcessInforWWApplyDetail.class, id2);
		if (wwApplyd == null) {
			return new Object[] { "没有找到目标订单!" };
		}
		boolean tz = false;
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, sbww
				.getProcardId());
		if (procard.getSbStatus().equals("删除")) {// 设变删除了此零件
			// 获取此零件更新后的零件
			Procard newprocard = (Procard) totalDao
					.getObjectByCondition(
							"from Procard where procard.id=? and markId=? and (sbStatus is null or sbStatus!='删除')",
							procard.getFatherId(), procard.getMarkId());
			if (newprocard == null) {
				wwApplyd.setDataStatus("取消");
				totalDao.update(wwApplyd);
				sbww.setStatus("关闭");
				totalDao.update(sbww);
				// 查询此BOM下是否还有未处理完的外委待处理项
				Float wclcount = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
								procard.getRootId());
				if (wclcount == null || wclcount == 0) {
					List<Procard> wgProcardList = totalDao
							.query(
									"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
									sbww.getPtbbApplyId(), procard.getRootId());
					if (wgProcardList != null && wgProcardList.size() > 0) {
						for (Procard wg : wgProcardList) {
							procardServer.jihuoSingleProcard(wg);
						}
					}
				}
				return new Object[] { "true1", "没有找到设变后此订单对应的生产零件,已取消此订单!" };
			} else {
				List<ProcessInfor> processList = totalDao
						.query(
								"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除' order by processNO)",
								newprocard.getId());
				return new Object[] { "true", sbww, wwApplyd, newprocard,
						processList };
			}
		} else {// 获取工序并比对
			procard.setWwCount(wwApplyd.getApplyCount());
			List<ProcessInfor> processList = totalDao
					.query(
							"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除' order by processNO)",
							procard.getId());
			return new Object[] { "true", sbww, wwApplyd, procard, processList };
		}
	}

	@Override
	public String updateSbsdd(ProcessInforWWApplyDetail detail, Integer id,
			Integer id2, Procard procard, int[] checkboxs) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		ProcessInforWWApplyDetail olddetail = (ProcessInforWWApplyDetail) totalDao
				.getObjectById(ProcessInforWWApplyDetail.class, id2);
		if (olddetail == null) {
			return "没有找到目标申请单!";
		}
		String msg = "";
		Procard oldProcard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getId());
		List<ProcessInfor> processList = totalDao
				.query(
						"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') order by processNO",
						procard.getId());
		ProcessInforWWApply wwApply = (ProcessInforWWApply) totalDao
				.getObjectByCondition(
						"from ProcessInforWWApply where status='未申请' and procardId=?",
						procard.getRootId());
		if (sbww.getProcardId().equals(procard.getId())) {
			String[] processsnos = olddetail.getProcessNOs().split(";");
			String[] processnames = olddetail.getProcessNames().split(";");
			Integer fno = Integer.parseInt(processsnos[0]);
			String fname = processnames[0];
			Integer eno = Integer.parseInt(processsnos[processsnos.length - 1]);
			String ename = processnames[processnames.length - 1];
			// 清理之前的申请数量
			boolean first = false;
			for (ProcessInfor process : processList) {
				if (process.getProcessNO() >= fno
						|| process.getProcessNO().equals(fname)) {// 初始外委工序之后
					boolean ww = false;
					if (process.getAgreeWwCount() > 0) {
						first = true;// 判断已经遇到初始外委工序
						ww = true;
						if (olddetail.getApplyCount() >= process
								.getAgreeWwCount()) {
							process.setAgreeWwCount(0f);
						} else {
							process.setAgreeWwCount(process.getAgreeWwCount()
									- olddetail.getApplyCount());
						}
					}
					if (process.getProcessNO().equals(eno)) {
						if (process.getProcessName().equals(ename)) {// 最后外委工序
							break;
						}
						if (first && !ww) {// 断掉外委工序之后
							break;
						}
					} else if (process.getProcessNO() > eno && first && !ww) {// 断掉外委工序之后
						break;
					}
				}
			}
		}
		Procard totalCard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getRootId());
		Set<ProcessInforWWApplyDetail> detailSet = null;
		if (wwApply == null) {
			wwApply = new ProcessInforWWApply();
			wwApply.setFinalCount(totalCard.getFilnalCount());
			wwApply.setProcardId(totalCard.getId());
			wwApply.setYwMarkId(totalCard.getYwMarkId());// 业务件号
			wwApply.setMarkId(totalCard.getMarkId());// 总成件号
			wwApply.setProName(totalCard.getProName());// 总成名称
			wwApply.setSelfCard(totalCard.getSelfCard());// 总成批次
			wwApply.setNumber(totalCard.getFilnalCount());// 数量
			wwApply.setOrderNumber(totalCard.getOrderNumber());// 内部订单号
			wwApply.setOrderNumber(totalCard.getOrderNumber());// 内部订单号
			wwApply.setProcessStatus("预选未审批");
			wwApply.setStatus("未申请");
			detailSet = new HashSet<ProcessInforWWApplyDetail>();
		} else {
			detailSet = wwApply.getProcessInforWWApplyDetails();
		}

		detail.setProcessStatus("预选未审批");
		detail.setProcardId(procard.getId());// 零件ID
		detail.setMarkId(procard.getMarkId());// 件号
		detail.setProName(procard.getProName());// 零件名
		detail.setSelfCard(procard.getSelfCard());// 批次
		detail.setUserId(user.getId());// 选择人id
		detail.setUserName(user.getName());// 选择人名字
		detail.setUserCode(user.getCode());// 选择人工号
		detail.setAddTime(Util.getDateTime());// 选择时间
		detail.setBanbenNumber(procard.getBanBenNumber());
		detail.setBanci(procard.getBanci());
		detail.setUnit(procard.getUnit());
		StringBuffer pnosb = new StringBuffer();
		StringBuffer pnamesb = new StringBuffer();
		for (int processId : checkboxs) {
			ProcessInfor old = (ProcessInfor) totalDao.getObjectByCondition(
					"from ProcessInfor where procard.id=? and id=?", procard
							.getId(), processId);
			if (old != null) {
				if (pnosb.length() == 0) {
					pnosb.append(old.getProcessNO());
					pnamesb.append(old.getProcessName());
				} else {
					pnosb.append(";" + old.getProcessNO());
					pnamesb.append(";" + old.getProcessName());

				}
				if (old.getSelectWwCount() == null) {
					old.setSelectWwCount(0f);
				}
				if (old.getApplyWwCount() == null) {
					old.setApplyWwCount(0f);
				}
				if (old.getAgreeWwCount() == null) {
					old.setAgreeWwCount(0f);
				}
				Float wwblCount = 0f;
				if (procard.getWwblCount() != null) {
					wwblCount = procard.getWwblCount();
				}
				Float syCount = procard.getFilnalCount() - wwblCount
						- old.getApplyCount() - old.getSelectWwCount()
						- old.getApplyWwCount() - old.getAgreeWwCount();
				if (syCount < detail.getApplyCount()) {
					msg += "第" + old.getProcessNO() + "工序:"
							+ old.getProcessName() + "最多可预选" + syCount
							+ procard.getUnit() + "!\n";
				} else {
					old.setSelectWwCount(old.getSelectWwCount()
							+ detail.getApplyCount());
					totalDao.update(old);
				}
			}
		}
		detail.setProcessNOs(pnosb.toString());
		detail.setProcessNames(pnosb.toString());
		Price price = (Price) totalDao
				.getObjectByCondition(
						"from Price where wwType=? and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
								+ Util.getDateTime() + "')  order by hsPrice",
						detail.getWwType(), detail.getMarkId(), detail
								.getProcessNOs());
		if (price != null) {
			detail.setPriceId(price.getId());
			detail.setGysId(price.getGysId());
			String gysName = (String) totalDao.getObjectByCondition(
					"select name from ZhUser where id=?", price.getGysId());
			detail.setGysName(gysName);
		}
		detail.setProcessInforWWApply(wwApply);
		totalDao.save(detail);
		if (price != null) {
			WaigouPlan waigouplan = (WaigouPlan) totalDao
					.getObjectByCondition(
							" from WaigouPlan where markId=? and processNOs=? and gysId=? order by banci desc",
							detail.getMarkId(), detail.getProcessNOs(), price
									.getGysId());
			List<WaigouPlanLock> wplList = totalDao
					.query(
							"from WaigouPlanLock where entityName='ProcessInforWWApplyDetail' and entityId=? and zxStatus='已执行' and sbApplyId is null and dataStatus<>'取消'",
							detail.getId());
			if (wplList != null && wplList.size() > 0) {
				for (WaigouPlanLock wpl : wplList) {
					totalDao.delete(wpl);
				}
			}
			Procard historyp = null;
			if (waigouplan != null) {
				historyp = (Procard) totalDao.getObjectByCondition(
						" from Procard where id in(select procardId from"
								+ " ProcardWGCenter where wgOrderId =?)",
						waigouplan.getId());
			}
			if (historyp == null) {
				detail.setHadChange("是");
			} else {
				String backdata = Util.compareProcardAndSons(detail
						.getProcardId(), historyp.getId(), null, null,
						totalDao, "ProcessInforWWApplyDetail", detail.getId(),
						detail.getMarkId());
				detail.setHadChange(backdata);
			}
			totalDao.update(detail);
		}
		// 关联外购件
		String wwMarkId = detail.getWwMarkId();
		if (wwMarkId != null && wwMarkId.length() > 0) {
			// 包含外购件
			// 拆分外购件回馈采购
			String[] markIds = wwMarkId.split(";");
			if (markIds != null && markIds.length > 0) {
				for (String wgMarkId : markIds) {
					Procard wgProcard = (Procard) totalDao
							.getObjectByCondition(
									"from Procard where markId=? and procard.id=?",
									wgMarkId, procard.getId());
					if (wgProcard != null) {
						ProcessInforWWProcard processwwprocard = new ProcessInforWWProcard();
						processwwprocard.setProcardId(wgProcard.getId());// 零件id
						processwwprocard.setMarkId(wgProcard.getMarkId());// 件号
						processwwprocard.setProcName(wgProcard.getProName());// 名称
						processwwprocard.setBanben(wgProcard.getBanBenNumber());// 版本号
						processwwprocard.setBanci(wgProcard.getBanci());// 版次
						processwwprocard.setApplyCount(detail.getApplyCount()
								* wgProcard.getQuanzi2()
								/ wgProcard.getQuanzi1());// 数量
						processwwprocard.setHascount(processwwprocard
								.getApplyCount());
						processwwprocard.setStatus("使用");// 状态
						processwwprocard.setApplyDtailId(detail.getId());
						totalDao.save(processwwprocard);
						if (detail.getWwType() != null
								&& detail.getWwType().equals("包工包料")) {// 包工包料回传采购
							if (wgProcard.getWwblCount() == null) {// 外委包料数量
								wgProcard.setWwblCount(detail.getApplyCount()
										* wgProcard.getQuanzi2()
										/ wgProcard.getQuanzi1());
							} else {
								wgProcard.setWwblCount(wgProcard.getWwblCount()
										+ detail.getApplyCount()
										* wgProcard.getQuanzi2()
										/ wgProcard.getQuanzi1());
							}
							totalDao.update(wgProcard);
						}
					} else {
						throw new RuntimeException("对不起该零件下没有找到件号为:" + wgMarkId
								+ "的外购件");
					}
				}
			}

		}
		detailSet.add(detail);
		if (detail.getWwType() != null && detail.getWwType().equals("包工包料")) {
			// 关联下层半成品,自制件和组合（下层组合将整体被包公包料）
			procardServer.updateProcardWwblCount(procard, detail
					.getApplyCount(), 0);
		}
		if (detail.getWwType().equals("工序外委")
				&& detail.getRelatDown().equals("是")) {
			// 外委下层
			msg += procardServer.updateDownWwProcess(procard, detail
					.getApplyCount(), detail.getId(), 0);

		}
		if (msg.length() > 0) {
			throw new RuntimeException(msg);
		}
		olddetail.setDataStatus("取消");
		totalDao.update(olddetail);
		sbww.setStatus("关闭");
		totalDao.update(sbww);
		// 查询此BOM下是否还有未处理完的外委待处理项
		Float wclcount = (Float) totalDao
				.getObjectByCondition(
						"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
						oldProcard.getRootId());
		if (wclcount == null || wclcount == 0) {
			List<Procard> wgProcardList = totalDao
					.query(
							"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
							sbww.getPtbbApplyId(), oldProcard.getRootId());
			if (wgProcardList != null && wgProcardList.size() > 0) {
				for (Procard wg : wgProcardList) {
					procardServer.jihuoSingleProcard(wg);
				}
			}
		}
		return "true";
	}

	@Override
	public Object[] toUpdatebomwwp(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return new Object[] { "请先登录!" };
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return new Object[] { "没有找到目标设变外委影响记录!" };
		}
		WaigouWaiweiPlan wwPlan = (WaigouWaiweiPlan) totalDao.getObjectById(
				WaigouWaiweiPlan.class, id2);
		if (wwPlan == null) {
			return new Object[] { "没有找到目标外委序列!" };
		}
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, sbww
				.getProcardId());
		if (procard.getSbStatus().equals("删除")) {
			wwPlan.setStatus("取消");
			totalDao.update(wwPlan);
			sbww.setStatus("关闭");
			totalDao.update(sbww);
			// 查询此BOM下是否还有未处理完的外委待处理项
			Float wclcount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
							procard.getRootId());
			if (wclcount == null || wclcount == 0) {
				List<Procard> wgProcardList = totalDao
						.query(
								"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
								sbww.getPtbbApplyId(), procard.getRootId());
				if (wgProcardList != null && wgProcardList.size() > 0) {
					for (Procard wg : wgProcardList) {
						procardServer.jihuoSingleProcard(wg);
					}
				}
			}
			return new Object[] { "true1", "此生产件已被替换,已直接取消订单!" };
		}
		List<ProcessInfor> processList = totalDao
				.query(
						"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除' order by processNO)",
						procard.getId());
		return new Object[] { "true", sbww, wwPlan, procard, processList };

	}

	@Override
	public String updateSbbomwwp(Integer id, Integer id2, Procard procard,
			int[] checkboxs) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouWaiweiPlan wwPlan = (WaigouWaiweiPlan) totalDao.getObjectById(
				WaigouWaiweiPlan.class, id2);
		if (wwPlan == null) {
			return "没有找到目标委外序列!";
		}
		String msg = "";
		Procard oldProcard = (Procard) totalDao.getObjectById(Procard.class,
				procard.getId());
		List<ProcessInfor> processList = totalDao
				.query(
						"from ProcessInfor where procard.id=? and (dataStatus is null or dataStatus!='删除') order by processNO",
						procard.getId());
		int fid = checkboxs[0];
		boolean upww = false;//
		boolean haspp = false;
		Float upCount = null;// 上道工序已提交数量
		if (processList != null && processList.size() > 0) {
			StringBuffer pnosb = new StringBuffer();
			StringBuffer pnamesb = new StringBuffer();
			Float alljiepai = 0f;
			Float deliveryDuration = 0f;
			Float wwCount = 0f;
			Integer upPno = null;
			String upPname = null;
			for (ProcessInfor process : processList) {
				if (!false) {
					upCount = process.getSubmmitCount();
					upPno = process.getProcessNO();
					upPname = process.getProcessName();
				}
				if (process.getId().equals(fid)) {
					if (process.getProductStyle().equals("自制")) {
						return process.getProcessNO() + "工序不是外委工序请重新选择";
					}
					haspp = true;
					pnosb.append(process.getProcessNO());
					pnosb.append(process.getProcessName());
					if (process.getAllJiepai() != null) {
						alljiepai += process.getAllJiepai();
					}
					if (process.getDeliveryDuration() != null) {
						deliveryDuration += process.getDeliveryDuration();
					}
					wwCount = upCount;
					if (upww && process.getProcessStatus().equals("yes")) {// 上道工序为外委且与此工序并行
						return "上道工序为外委且与此工序并行,请一起选择!";
					}
					upww = true;
				} else if (process.getProductStyle().equals("外委")) {
					if (haspp) {// 此工序一起外委
						if (process.getProcessStatus().equals("yes")) {
							pnosb.append(";" + process.getProcessNO());
							pnosb.append(";" + process.getProcessName());
							if (process.getAllJiepai() != null) {
								alljiepai += process.getAllJiepai();
							}
							if (process.getDeliveryDuration() != null) {
								deliveryDuration += process
										.getDeliveryDuration();
							}
						} else {
							break;
						}
					}
					upww = true;
				} else if (haspp && process.getProductStyle().equals("自制")) {
					break;
				}
			}
			if (wwCount != null && wwCount > 0) {
				// 查询之前已外委的工序数量
				Float hasww = (Float) totalDao
						.getObjectByCondition(
								"select sum(beginCount) from WaigouWaiweiPlan where markId = ? and selfCard =? and processNo=? and processName=? and status !='取消'",
								oldProcard.getMarkId(), oldProcard
										.getSelfCard(), pnosb.toString(),
								pnamesb.toString());
				if (wwCount > hasww) {
					// 添加委外待激活序列
					WaigouWaiweiPlan wwp = new WaigouWaiweiPlan();
					wwp.setRootMarkId(oldProcard.getRootMarkId());
					wwp.setRootSelfCard(oldProcard.getRootSelfCard());
					wwp.setOrderNum(oldProcard.getOrderNumber());
					wwp.setYwMarkId(oldProcard.getYwMarkId());
					wwp.setBanben(oldProcard.getBanBenNumber());
					wwp.setBanci(oldProcard.getBanci());
					wwp.setMarkId(oldProcard.getMarkId());
					wwp.setProcessNo(pnosb.toString());
					wwp.setProName(oldProcard.getProName());
					wwp.setProcessName(pnamesb.toString());
					wwp.setType("外委");
					wwp.setUnit(oldProcard.getUnit());
					wwp.setNumber(wwCount - hasww);
					wwp.setBeginCount(wwCount - hasww);
					wwp.setAddTime(Util.getDateTime());
					wwp.setJihuoTime(Util.getDateTime());
					wwp.setShArrivalTime(oldProcard.getNeedFinalDate());// 应到货时间在采购确认通知后计算
					wwp.setCaigouMonth(Util.getDateTime("yyyy-MM月"));// 采购月份
					String wwNumber = "";
					String before = null;
					Integer bIndex = 10;
					before = "ww" + Util.getDateTime("yyyyMMdd");
					Integer maxNo = 0;
					String maxNumber = (String) totalDao
							.getObjectByCondition("select max(planNumber) from WaigouWaiweiPlan where planNumber like '"
									+ before + "%'");
					if (maxNumber != null) {
						String wwnum = maxNumber.substring(bIndex, maxNumber
								.length());
						try {
							Integer maxNum = Integer.parseInt(wwnum);
							if (maxNum > maxNo) {
								maxNo = maxNum;
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					maxNo++;
					wwNumber = before + String.format("%03d", maxNo);
					wwp.setPlanNumber(wwNumber);// 采购计划编号
					wwp.setSelfCard(oldProcard.getSelfCard());// 批次
					// wwp.setGysId(nextWwProcessInfor
					// .getZhuserId());// 供应商id
					// wwp.setGysName(nextWwProcessInfor
					// .getGys());// 供应商名称
					wwp.setAllJiepai(alljiepai);// 单件总节拍
					wwp.setDeliveryDuration(deliveryDuration);// 耽误时长
					wwp.setSingleDuration(oldProcard.getSingleDuration());// 单班时长(工作时长)
					wwp.setProcardId(oldProcard.getId());
					wwp.setProcard(oldProcard);
					// if (wwckCount != null
					// && wwckCount > 0) {
					wwp.setStatus("待入库");
					// 在制品待入库
					if (oldProcard.getZaizhiApplyZk() == null) {
						oldProcard.setZaizhiApplyZk(0f);
					}
					if (oldProcard.getZaizhizkCount() == null) {
						oldProcard.setZaizhizkCount(0f);
					}
					if (oldProcard.getKlNumber() == null) {
						oldProcard.setKlNumber(oldProcard.getFilnalCount());
					}
					if (oldProcard.getHascount() == null) {
						oldProcard.setHascount(oldProcard.getKlNumber());
					}
					// procard.getKlNumber()-procard.getHascount()=已生产数量
					// 可转库数量=已生产数量-已转库数量-转库申请中数量
					// 先还原之前的数量
					if (sbww.getProcardId().equals(oldProcard.getId())) {
						if (oldProcard.getZaizhizkCount() != null) {
							Float aboutCount = wwPlan.getZzRukCount();
							if (aboutCount != null) {
								if (oldProcard.getZaizhizkCount() > aboutCount) {
									oldProcard.setZaizhizkCount(oldProcard
											.getZaizhizkCount()
											- aboutCount);
								} else {
									oldProcard.setZaizhizkCount(0f);
								}
							}
						}
					}
					oldProcard.setZaizhikzkCount(oldProcard.getFilnalCount()
							- oldProcard.getZaizhizkCount()
							- oldProcard.getZaizhiApplyZk());
					if (oldProcard.getZaizhikzkCount() >= wwCount) {
						oldProcard.setZaizhiApplyZk(oldProcard
								.getZaizhiApplyZk()
								+ wwCount);
						String orderNum = (String) totalDao
								.getObjectByCondition(
										"select orderNumber from Procard where id=?",
										oldProcard.getRootId());
						// 成品待入库
						GoodsStore goodsStore2 = new GoodsStore();
						goodsStore2.setNeiorderId(orderNum);
						goodsStore2.setWaiorderId(procard.getOutOrderNum());
						goodsStore2.setProcardId(procard.getId());
						goodsStore2.setGoodsStoreMarkId(oldProcard.getMarkId());
						goodsStore2.setBanBenNumber(oldProcard
								.getBanBenNumber());
						goodsStore2.setGoodsStoreLot(oldProcard.getSelfCard());
						goodsStore2.setGoodsStoreGoodsName(oldProcard
								.getProName());
						goodsStore2.setApplyTime(Util.getDateTime());
						goodsStore2
								.setGoodsStoreArtsCard((String) totalDao
										.getObjectByCondition(
												"select selfCard from Procard where id=?",
												oldProcard.getRootId()));
						goodsStore2.setGoodsStorePerson(Util.getLoginUser()
								.getName());
						goodsStore2.setStatus("待入库");
						goodsStore2.setStyle("半成品转库");
						goodsStore2.setGoodsStoreWarehouse("委外库");// 库别
						// goodsStore2.setGoodHouseName(goodsStore.getGoodHouseName());//
						// 区名
						// goodsStore2.setGoodsStorePosition(goodsStore.getGoodsStorePosition());//
						// 库位
						goodsStore2.setGoodsStoreUnit(oldProcard.getUnit());
						goodsStore2.setGoodsStoreCount(wwCount);
						goodsStore2.setProcessNo(upPno);
						goodsStore2.setProcessName(upPname);
						totalDao.update(oldProcard);
						totalDao.save(goodsStore2);
						// 判断外委进委外入库是否要做
						String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='委外库接收半成品' and valueName='委外库接收半成品'";
						String valueCode = (String) totalDao
								.getObjectByCondition(hql1);
						if (valueCode != null && valueCode.equals("否")) {
							// 入库记录直接通过
							goodsStore2.setStatus("入库");
							goodsStore2.setPrintStatus("YES");
							totalDao.update(goodsStore2);
							// 增加库存记录
							String hqlgoods = "from Goods where goodsMarkId='"
									+ oldProcard.getMarkId()
									+ "' and goodsLotId='"
									+ oldProcard.getSelfCard()
									+ "' and goodsStyle='半成品转库' and processNo="
									+ upPno + " and goodsClass='委外库'";
							Goods goods = (Goods) totalDao
									.getObjectByCondition(hqlgoods);
							if (goods != null) {
								goods.setGoodsCurQuantity(goods
										.getGoodsCurQuantity()
										+ goodsStore2.getGoodsStoreCount());
								totalDao.update(goods);
							} else {
								goods = new Goods();
								goods.setGoodsMarkId(goodsStore2
										.getGoodsStoreMarkId());
								goods.setGoodsFormat(goodsStore2
										.getGoodsStoreFormat());
								goods.setBanBenNumber(goodsStore2
										.getBanBenNumber());
								goods.setGoodsFullName(goodsStore2
										.getGoodsStoreGoodsName());
								goods.setGoodsClass("委外库");
								goods.setGoodsBeginQuantity(goodsStore2
										.getGoodsStoreCount());
								goods.setGoodsCurQuantity(goodsStore2
										.getGoodsStoreCount());
								totalDao.save(goods);
							}
							// 添加零件与在制品关系表
							ProcardProductRelation pprelation = new ProcardProductRelation();
							pprelation.setAddTime(Util.getDateTime());
							pprelation.setProcardId(oldProcard.getId());
							pprelation.setGoodsId(goods.getGoodsId());
							pprelation.setZyCount(goodsStore2
									.getGoodsStoreCount());
							pprelation.setFlagType("本批在制品");
							totalDao.save(pprelation);
							// 将外购外委激活序列状态改为待激活
							wwp.setStatus("待激活");
							// totalDao.save(wwp);
						}
					} else {
						return "对不起超过可申请数量(" + procard.getZaizhikzkCount()
								+ ")";
					}
					// } else {
					// wwp.setStatus("待激活");
					// }
					totalDao.save(wwp);
					// wgSet.add(wwp);
					if (wwp.getId() != null) {
						// 匹配供应商
						Price price = (Price) totalDao
								.getObjectByCondition(
										"from Price where wwType='工序外委' and partNumber=? and gongxunum=? and (pricePeriodEnd is null or pricePeriodEnd ='' or pricePeriodEnd>='"
												+ Util.getDateTime()
												+ "')  order by hsPrice", wwp
												.getMarkId(), wwp
												.getProcessNo());
						if (price != null) {
							wwp.setPriceId(price.getId());
							wwp.setGysId(price.getGysId());
							ZhUser zhUser = (ZhUser) totalDao.getObjectById(
									ZhUser.class, price.getGysId());
							wwp.setGysName(zhUser.getName());
							wwp.setUserCode(zhUser.getUsercode());
							wwp.setUserId(zhUser.getUserid());
							totalDao.update(wwp);
						}
					}
					if (wwp.getStatus() != null
							&& wwp.getStatus().equals("待激活")) {// 说明自动跳过了半成品入委外库操作
						// 下一步操作
						procardServer.zijihuoww(wwp);
					}

				}
			}
			wwPlan.setStatus("取消");
			totalDao.update(wwPlan);
			sbww.setStatus("关闭");
			totalDao.update(sbww);
			// 查询此BOM下是否还有未处理完的外委待处理项
			Float wclcount = (Float) totalDao
					.getObjectByCondition(
							"select count(*) from ProcardSbWw where procardId in (select id from Procard where rootId=? and (sbStatus is null or sbStatus !='删除'))",
							oldProcard.getRootId());
			if (wclcount == null || wclcount == 0) {
				List<Procard> wgProcardList = totalDao
						.query(
								"from Procard where sbId=? and rootId=? and (sbStatus is null or sbStatus !='删除') and status='初始'",
								sbww.getPtbbApplyId(), oldProcard.getRootId());
				if (wgProcardList != null && wgProcardList.size() > 0) {
					for (Procard wg : wgProcardList) {
						procardServer.jihuoSingleProcard(wg);
					}
				}
			}
		}
		return "true";
	}

	@Override
	public String jxsbwp(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouPlan wgPlan = (WaigouPlan) totalDao.getObjectById(
				WaigouPlan.class, id2);
		if (wgPlan == null) {
			return "没有找到目标委外序列!";
		}
		wgPlan.setStatus(wgPlan.getOldStatus());
		totalDao.update(wgPlan);
		sbww.setStatus("关闭");
		totalDao.update(sbww);
		return "true";
	}

	@Override
	public String jxsbsdd(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		ProcessInforWWApplyDetail detail = (ProcessInforWWApplyDetail) totalDao
				.getObjectById(ProcessInforWWApplyDetail.class, id2);
		if (detail == null) {
			return "没有找到目标委外序列!";
		}
		detail.setDataStatus("正常");
		totalDao.update(detail);
		sbww.setStatus("关闭");
		totalDao.update(sbww);
		return "true";
	}

	@Override
	public String jxsbbomwwp(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWw sbww = (ProcardSbWw) totalDao.getObjectById(
				ProcardSbWw.class, id);
		if (sbww == null) {
			return "没有找到目标设变外委影响记录!";
		}
		WaigouWaiweiPlan wwPlan = (WaigouWaiweiPlan) totalDao.getObjectById(
				WaigouWaiweiPlan.class, id2);
		if (wwPlan == null) {
			return "没有找到目标委外序列!";
		}
		wwPlan.setStatus(wwPlan.getOldStatus());
		totalDao.update(wwPlan);
		sbww.setStatus("关闭");
		totalDao.update(sbww);
		return "true";
	}

	@Override
	public String backsbApply(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply == null) {
			return "不存在你要打回的设变申请!";
		} else if (ptbbApply.getProcessStatus().equals("关联并通知生产")
				|| ptbbApply.getProcessStatus().equals("关闭")) {
			return "设变申请进度当前状态为:" + ptbbApply.getProcessStatus() + "不允许打回!";
		} else if (ptbbApply.getProcessStatus().equals("资料更新")) {
			ProcardTemplateBanBenJudges ptbbj = (ProcardTemplateBanBenJudges) totalDao
					.getObjectByCondition(
							"from ProcardTemplateBanBenJudges where ptbbApply.id=? and userId=? and judgeType='工程师'",
							id, user.getId());
			if (ptbbj == null) {
				return "当前设变申请进度当前状态为:" + ptbbApply.getProcessStatus()
						+ ",您没有权限打回!";
			} else {
				ptbbj.setBackTag("yes");
				totalDao.update(ptbbj);
			}
			ptbbApply.setProcessStatus("各部门评审");
			totalDao.update(ptbbApply);
		} else if (ptbbApply.getProcessStatus().equals("结论")) {
			ptbbApply.setProcessStatus("各部门评审");
			totalDao.update(ptbbApply);
		} else if (ptbbApply.getProcessStatus().equals("指派各部门")
				|| ptbbApply.getProcessStatus().equals("各部门评审")
				|| ptbbApply.getProcessStatus().equals("各部门评审")) {
			String usertype = null;
			if (ptbbApply.getProcessStatus().equals("指派各部门")) {
				usertype = "工程师";
			} else if (ptbbApply.getProcessStatus().equals("成本审核")) {
				usertype = "成本";
			} else {
				usertype = "内审";
			}
			ProcardTemplateBanBenJudges ptbbj = (ProcardTemplateBanBenJudges) totalDao
					.getObjectByCondition(
							"from ProcardTemplateBanBenJudges where ptbbApply.id=? and userId=? and judgeType=?",
							id, user.getId(), usertype);
			if (ptbbj == null) {
				return "当前设变申请进度当前状态为:" + ptbbApply.getProcessStatus()
						+ ",您没有权限打回!";
			} else {
				ptbbj.setBackTag("yes");
				totalDao.update(ptbbj);
			}
			// 删除关联并通知生产数据
			List<ProcardAboutBanBenApply> pabbList = totalDao.query(
					"from ProcardAboutBanBenApply where bbapplyId=?", id);
			if (pabbList != null && pabbList.size() > 0) {
				for (ProcardAboutBanBenApply pabb : pabbList) {
					Procard p = (Procard) totalDao.getObjectById(Procard.class,
							pabb.getProcardId());
					if (p != null && p.getStatus().equals("设变锁定")) {
						p.setStatus(p.getOldStatus());
						totalDao.update(p);
					}
					Set<ProcessAboutBanBenApply> processabbSet = pabb
							.getProcessabbSet();
					pabb.setProcessabbSet(null);
					if (processabbSet != null && processabbSet.size() > 0) {
						for (ProcessAboutBanBenApply processabb : processabbSet) {
							processabb.setPabb(null);
							totalDao.delete(processabb);
						}
					}
					// 删除生产关联评语
					List<ProcardBanBenJudge> pbbjList = totalDao.query(
							"from ProcardBanBenJudge where pabbJudgeId=?", pabb
									.getId());
					if (pbbjList != null && pbbjList.size() > 0) {
						for (ProcardBanBenJudge pbbj : pbbjList) {
							totalDao.delete(pbbj);
						}
					}
					totalDao.delete(pabb);
				}

			}
			ptbbApply.setLastop("打回");
			ptbbApply.setLastUserId(user.getId());
			ptbbApply.setLastUserName(user.getName());
			ptbbApply.setLastTime(Util.getDateTime());
			ptbbApply.setProcessStatus("项目工程师评审");
			totalDao.update(ptbbApply);
		}
		return "true";
		// else {
		//			
		// // 删除各评审人员
		// Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply.getPtbbjset();
		// ptbbApply.setPtbbjset(null);
		// if (ptbbjSet != null && ptbbjSet.size() > 0) {
		// for (ProcardTemplateBanBenJudges ptbbj : ptbbjSet) {
		// ptbbj.setPtbbApply(null);
		// totalDao.delete(ptbbj);
		// }
		// }
		// // 删除设变零件明细
		// Set<ProcardTemplateBanBen> procardTemplateBanBenSet = ptbbApply
		// .getProcardTemplateBanBen();
		// ptbbApply.setProcardTemplateBanBen(null);
		// if (procardTemplateBanBenSet != null
		// && procardTemplateBanBenSet.size() > 0) {
		// for (ProcardTemplateBanBen procardTemplateBanBen :
		// procardTemplateBanBenSet) {
		// procardTemplateBanBen.setProcardTemplateBanBenApply(null);
		// ProcardTemplate pt = (ProcardTemplate) totalDao
		// .getObjectById(ProcardTemplate.class,
		// procardTemplateBanBen.getPtId());
		// pt.setBzStatus("已批准");
		// totalDao.delete(procardTemplateBanBen);
		// }
		// }
		// // 终极删除
		// totalDao.delete(ptbbApply);
		// return "true";
		// }
	}

	@Override
	public Object[] findProcardSbWasterList(ProcardSbWaster procardSbWaster,
			int parseInt, int pageSize) {
		// TODO Auto-generated method stub
		if (procardSbWaster == null) {
			procardSbWaster = new ProcardSbWaster();
		}
		String hql = totalDao.criteriaQueries(procardSbWaster, null);
		List<ProcardSbWaster> procardSbWasterList = totalDao.query(hql
				+ " order by id desc");
		int count = totalDao.getCount(hql);
		return new Object[] { procardSbWasterList, count };
	}

	@Override
	public ProcardSbWaster getProcardSbWasterById(Integer id) {
		// TODO Auto-generated method stub
		ProcardSbWaster procardSbWaster = (ProcardSbWaster) totalDao
				.getObjectById(ProcardSbWaster.class, id);
		if (procardSbWaster != null) {
			Set<ProcardSbWasterxc> set = procardSbWaster
					.getProcardSbWasterxcs();
			if (set != null && set.size() > 0) {
				List<ProcardSbWasterxc> procardSbWasterxcList = new ArrayList(
						set);
				procardSbWaster.setProcardSbWasterxcList(procardSbWasterxcList);
			}
		}
		return procardSbWaster;
	}

	@Override
	public String judegWaster(ProcardSbWaster procardSbWaster) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		String nowTime = Util.getDateTime();
		if (user == null) {
			return "请先登录!";
		}
		ProcardSbWaster old = (ProcardSbWaster) totalDao.getObjectById(
				ProcardSbWaster.class, procardSbWaster.getId());
		if (old == null) {
			return "没有找到目标待处理设变在制品!";
		}
		procardSbWaster.setClId(user.getId());// 处理人Id;
		procardSbWaster.setClName(user.getName());// 处理人名称
		procardSbWaster.setClCode(user.getCode());// 处理人工号
		procardSbWaster.setClTime(nowTime);// 处理时间
		Set<ProcardSbWasterxc> procardSbWasterxcSet = old
				.getProcardSbWasterxcs();
		if (procardSbWaster.getTrueClType().equals("报废")) {// 在制品整体报废
			if (procardSbWasterxcSet != null && procardSbWasterxcSet.size() > 0) {
				for (ProcardSbWasterxc xc : procardSbWasterxcSet) {
					if (xc.getXcType().equals("本身")) {
						xc.setNeedcl("是");
						xc.setBfclCount(procardSbWaster.getBfCount());
						xc.setTkclcount(0f);
						xc.setClStatus("处理中");
						totalDao.update(xc);
					} else {
						xc.setNeedcl("否");
						xc.setClStatus("关闭");
						totalDao.update(xc);
					}
				}
			}
		} else {
			String msg = "";
			List<ProcardSbWasterxc> xcList = procardSbWaster
					.getProcardSbWasterxcList();
			if (xcList != null && xcList.size() > 0) {
				for (ProcardSbWasterxc xc1 : xcList) {
					for (ProcardSbWasterxc xc2 : procardSbWasterxcSet) {
						if (xc1.getId().equals(xc2.getId())) {
							if (xc1.getNeedcl().equals("是")) {
								xc2.setNeedcl("是");
								if (xc1.getBfclCount() == null) {
									xc1.setBfclCount(0f);
								}
								if (xc1.getTkclcount() == null) {
									xc1.setTkclcount(0f);
								}
								if ((xc1.getBfclCount() + xc1.getTkclcount()) > xc2
										.getGlCount()) {
									if (msg.length() > 0) {
										msg += "<br/>" + xc2.getMarkId()
												+ "数量超额!";
									} else {
										msg += xc2.getMarkId() + "数量超额!";
									}
								} else {
									xc2.setBfclCount(xc1.getBfclCount());
									xc2.setTkclcount(xc1.getTkclcount());
									xc2.setClStatus("处理中");
									totalDao.update(xc2);
								}
							} else {
								xc2.setNeedcl("否");
								xc2.setClStatus("关闭");
								totalDao.update(xc2);
							}
							break;
						}
					}
				}
			}
			if (msg.length() > 0) {
				throw new RuntimeException(msg);
			}
		}
		procardSbWaster.setStatus("处理中");
		totalDao.update(procardSbWaster);
		return "true";
	}

	@Override
	public Object[] getsbjdByProcardTId(Integer id, ProcardTemplate pt2) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = null;
		if (id != null) {
			pt = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id);
		} else {
			pt = pt2;
		}
		if (pt != null) {
			Integer sbId = null;
			Integer banci = pt.getBanci();
			if (banci == null) {
				banci = 0;
			}
			if (banci > 1) {
				banci--;
				sbId = (Integer) totalDao
						.getObjectByCondition(
								"select procardTemplateBanBenApply.id from ProcardTemplateBanBen where banci=? and  procardTemplateBanBenApply.processStatus in('关联并通知生产','生产后续','上传佐证','关闭') and markId=? order by id desc",
								banci, pt.getMarkId());
			} else if (banci == 1) {
				sbId = (Integer) totalDao
						.getObjectByCondition(
								"select procardTemplateBanBenApply.id from ProcardTemplateBanBen where (banci is null or banci=0) and  procardTemplateBanBenApply.processStatus in('关联并通知生产','生产后续','上传佐证','关闭') and markId=? order by id desc",
								pt.getMarkId());
			}
			if (sbId != null) {
				return showSbApplyJd(sbId);
			} else {
				if (pt.getProcardTemplate() != null) {
					return getsbjdByProcardTId(null, pt.getProcardTemplate());
				}
			}
		}
		return null;
	}

	@Override
	public Object[] finddrkProcardSbWasterxcList(
			ProcardSbWasterxc procardSbWasterxc,
			ProcardSbWaster procardSbWaster, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		if (procardSbWasterxc == null) {
			procardSbWasterxc = new ProcardSbWasterxc();
		}
		String hql = totalDao.criteriaQueries(procardSbWasterxc, null, null);
		hql = hql + " and clStatus='处理中'";
		if (procardSbWaster != null) {
			if (procardSbWaster.getSbNumber() != null
					&& procardSbWaster.getSbNumber().length() > 0
					&& !procardSbWaster.getSbNumber().contains("delete")
					&& !procardSbWaster.getSbNumber().contains("update")
					&& !procardSbWaster.getSbNumber().contains("select")) {
				hql = hql + " and procardSbWaster.sbNumbe like '%"
						+ procardSbWaster.getSbNumber() + "%'";
			}
			if (procardSbWaster.getOrderNumber() != null
					&& procardSbWaster.getOrderNumber().length() > 0
					&& !procardSbWaster.getOrderNumber().contains("delete")
					&& !procardSbWaster.getOrderNumber().contains("update")
					&& !procardSbWaster.getOrderNumber().contains("select")) {
				hql = hql + " and procardSbWaster.orderNumber like '%"
						+ procardSbWaster.getOrderNumber() + "%'";
			}
			if (procardSbWaster.getYwmarkId() != null
					&& procardSbWaster.getYwmarkId().length() > 0
					&& !procardSbWaster.getYwmarkId().contains("delete")
					&& !procardSbWaster.getYwmarkId().contains("update")
					&& !procardSbWaster.getYwmarkId().contains("select")) {
				hql = hql + " and procardSbWaster.ywmarkId like '%"
						+ procardSbWaster.getYwmarkId() + "%'";
			}
			if (procardSbWaster.getRootMarkId() != null
					&& procardSbWaster.getRootMarkId().length() > 0
					&& !procardSbWaster.getRootMarkId().contains("delete")
					&& !procardSbWaster.getRootMarkId().contains("update")
					&& !procardSbWaster.getRootMarkId().contains("select")) {
				hql = hql + " and procardSbWaster.rootMarkId like '%"
						+ procardSbWaster.getRootMarkId() + "%'";
			}
		}
		hql = hql + " order by id desc";
		int count = totalDao.getCount(hql);
		List<ProcardSbWasterxc> objs = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		if (objs != null && objs.size() > 0) {
			for (ProcardSbWasterxc obj : objs) {
				obj.setOrderNumber(obj.getProcardSbWaster().getOrderNumber());// 订单号
				obj.setSbNumber(obj.getProcardSbWaster().getSbNumber());// 设变申请id
				obj.setRootMarkId(obj.getProcardSbWaster().getRootMarkId());// 总成件号
				obj.setRootSelfCard(obj.getProcardSbWaster().getRootSelfCard());// 总成批次
				obj.setYwmarkId(obj.getProcardSbWaster().getYwmarkId());// 业务件号
			}
		}
		return new Object[] { objs, count };
	}

	@Override
	public ProcardSbWasterxc getProcardSbWasterxcById(Integer id) {
		// TODO Auto-generated method stub
		ProcardSbWasterxc procardSbWasterxc = (ProcardSbWasterxc) totalDao
				.getObjectById(ProcardSbWasterxc.class, id);
		return procardSbWasterxc;
	}

	@Override
	public List<ProcardTemplateChangeLog> getChangeLogshow(Integer id) {
		// TODO Auto-generated method stub
		ProcardTemplate pt = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id);
		if (pt != null) {
			String banciSql = null;
			if (pt.getBanci() == null) {
				banciSql = " and (sbbanci is null or sbbanci =0)";
			} else {
				banciSql = " and sbbanci = " + pt.getBanci();
			}
			return totalDao.query(
					"from ProcardTemplateChangeLog where sbMarkId=? ", pt
							.getMarkId());
		} else {
			return null;
		}
	}

	@Override
	public String quxiaoSb(ProcardTemplateBanBenApply bbAply) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, bbAply.getId());
		if (ptbbApply == null) {
			return "不存在你要删除的设变申请!";
		} else if (ptbbApply.getProcessStatus().equals("资料更新")
				|| ptbbApply.getProcessStatus().equals("关联并通知生产")
				|| ptbbApply.getProcessStatus().equals("生产后续")
				|| ptbbApply.getProcessStatus().equals("上传佐证")
				|| ptbbApply.getProcessStatus().equals("关闭")) {
			return "设变申请进度当前状态为:" + ptbbApply.getProcessStatus() + "不允许取消!";
		} else {
			if (ptbbApply.getProcessStatus().equals("设变发起")) {
				if (!ptbbApply.getApplicantId().equals(user.getId())) {
					return "对不起,您没有取消权限!";
				}

			} else if (ptbbApply.getProcessStatus().equals("分发项目组")) {
				if (!ptbbApply.getApplicantId().equals(user.getId())) {
					// 获取设变人
					Float bgry = (Float) totalDao
							.getObjectByCondition(
									"select count(*) from AssessPersonnel where usersGroup.groupName='设变关闭组' and usersGroup.status='sb' and userId=? ",
									user.getId());
					if (bgry == null || bgry == 0) {
						return "对不起,您没有取消权限!";
					}
				}
			} else if (ptbbApply.getProcessStatus().equals("项目主管初评")) {
				Float zgry = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardTemplateBanBenJudges where judgeType='初评' and ptbbApply.id=? and userId=? ",
								bbAply.getId(), user.getId());
				if (zgry == null || zgry == 0) {
					return "对不起,您没有取消权限!";
				}
			} else {
				Float zgry = (Float) totalDao
						.getObjectByCondition(
								"select count(*) from ProcardTemplateBanBenJudges where judgeType='工程师' and ptbbApply.id=? and userId=? ",
								bbAply.getId(), user.getId());
				if (zgry == null || zgry == 0) {
					return "对不起,您没有取消权限!";
				}
			}
			// 删除关联生产数据
			List<ProcardAboutBanBenApply> pabbList = totalDao.query(
					"from ProcardAboutBanBenApply where bbapplyId=?", bbAply
							.getId());
			if (pabbList != null && pabbList.size() > 0) {
				for (ProcardAboutBanBenApply pabb : pabbList) {
					Procard p = (Procard) totalDao.getObjectById(Procard.class,
							pabb.getProcardId());
					if (p != null && p.getStatus().equals("设变锁定")) {
						p.setStatus(p.getOldStatus());
						totalDao.update(p);
					}
					// Set<ProcessAboutBanBenApply> processabbSet = pabb
					// .getProcessabbSet();
					// pabb.setProcessabbSet(null);
					// if (processabbSet != null && processabbSet.size() > 0) {
					// for (ProcessAboutBanBenApply processabb : processabbSet)
					// {
					// processabb.setPabb(null);
					// totalDao.delete(processabb);
					// }
					// }
					// // 删除生产关联评语
					// List<ProcardBanBenJudge> pbbjList = totalDao.query(
					// "from ProcardBanBenJudge where pabbJudgeId=?", pabb
					// .getId());
					// if (pbbjList != null && pbbjList.size() > 0) {
					// for (ProcardBanBenJudge pbbj : pbbjList) {
					// totalDao.delete(pbbj);
					// }
					// }
					// totalDao.delete(pabb);
				}

			}
			// 删除各评审人员
			// Set<ProcardTemplateBanBenJudges> ptbbjSet =
			// ptbbApply.getPtbbjset();
			// ptbbApply.setPtbbjset(null);
			// if (ptbbjSet != null && ptbbjSet.size() > 0) {
			// for (ProcardTemplateBanBenJudges ptbbj : ptbbjSet) {
			// ptbbj.setPtbbApply(null);
			// totalDao.delete(ptbbj);
			// }
			// }
			// 返回设变零件状态
			Set<ProcardTemplateBanBen> procardTemplateBanBenSet = ptbbApply
					.getProcardTemplateBanBen();
			if (procardTemplateBanBenSet != null
					&& procardTemplateBanBenSet.size() > 0) {
				for (ProcardTemplateBanBen procardTemplateBanBen : procardTemplateBanBenSet) {
					// procardTemplateBanBen.setProcardTemplateBanBenApply(null);
					List<ProcardTemplate> backptList = totalDao
							.query(
									"from ProcardTemplate where markId = ? and (banbenStatus is null or banbenStatus!='历史')",
									procardTemplateBanBen.getMarkId());
					if (backptList != null && backptList.size() > 0) {
						for (ProcardTemplate back : backptList) {
							back.setBzStatus("已批准");
							totalDao.update(back);
						}
					}
					// totalDao.delete(procardTemplateBanBen);
				}
			}
			ptbbApply.setOldProcessStatus(ptbbApply.getProcessStatus());
			ptbbApply.setProcessStatus("取消");
			ptbbApply.setQxRemark(bbAply.getQxRemark());
			// 清除所有设变锁定的外委
			List<Integer> wpIdList = totalDao
					.query(
							"select entityId from WaigouPlanLock where entityName='WaigouPlan' and sbApplyId=? and dataStatus<>'取消'",
							ptbbApply.getId());
			List<Integer> wwpIdList = totalDao
					.query(
							"select entityId from WaigouPlanLock where entityName='WaigouWaiweiPlan' and sbApplyId=? and dataStatus<>'取消'",
							ptbbApply.getId());
			List<Integer> pwIdList = totalDao
					.query(
							"select entityId from WaigouPlanLock where entityName='ProcessInforWWApplyDetail' and sbApplyId=? and dataStatus<>'取消'",
							ptbbApply.getId());
			if (wpIdList != null && wpIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (Integer id : wpIdList) {
					if (sb.length() == 0) {
						sb.append(id);
					} else {
						sb.append("," + id);
					}
				}
				List<WaigouPlan> entityList = totalDao
						.query(" from WaigouPlan where id in(" + sb.toString()
								+ ")");
				for (WaigouPlan entity : entityList) {
					if (entity.getStatus().equals("设变锁定")) {
						Float sdCount = (Float) totalDao
								.getObjectByCondition(
										"select count(id) from WaigouPlanLock where entityName='WaigouPlan' and sbApplyId!=? and entityId=? and zxStatus='执行中' and dataStatus<>'取消'",
										ptbbApply.getId(), entity.getId());
						if (sdCount == null || sdCount == 0) {
							entity.setStatus(entity.getOldStatus());
							totalDao.update(entity);
						}
					}
				}
			}
			if (wwpIdList != null && wwpIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (Integer id : wwpIdList) {
					if (sb.length() == 0) {
						sb.append(id);
					} else {
						sb.append("," + id);
					}
				}
				List<WaigouWaiweiPlan> entityList = totalDao
						.query(" from WaigouWaiweiPlan where id in("
								+ sb.toString() + ")");
				for (WaigouWaiweiPlan entity : entityList) {
					if (entity.getStatus().equals("设变锁定")) {
						Float sdCount = (Float) totalDao
								.getObjectByCondition(
										"select count(id) from WaigouPlanLock where entityName='WaigouWaiweiPlan' and sbApplyId!=? and entityId=? and zxStatus='执行中' and dataStatus<>'取消'",
										ptbbApply.getId(), entity.getId());
						if (sdCount == null || sdCount == 0) {
							entity.setStatus(entity.getOldStatus());
							totalDao.update(entity);
						}
					}
				}
			}
			if (pwIdList != null && pwIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (Integer id : pwIdList) {
					if (sb.length() == 0) {
						sb.append(id);
					} else {
						sb.append("," + id);
					}
				}
				List<ProcessInforWWApplyDetail> entityList = totalDao
						.query(" from ProcessInforWWApplyDetail where id in("
								+ sb.toString() + ")");
				for (ProcessInforWWApplyDetail entity : entityList) {
					if (entity.getProcessStatus().equals("设变锁定")) {
						Float sdCount = (Float) totalDao
								.getObjectByCondition(
										"select count(id) from WaigouPlanLock where entityName='ProcessInforWWApplyDetail' and sbApplyId!=? and entityId=? and zxStatus='执行中' and dataStatus<>'取消'",
										ptbbApply.getId(), entity.getId());
						if (sdCount == null || sdCount == 0) {
							entity.setProcessStatus(entity
									.getOldprocessStatus());
							totalDao.update(entity);
						}
					}
				}
			}
			List<WaigouPlanLock> wplList = totalDao
					.query(
							"from WaigouPlanLock where sbApplyId=? and dataStatus<>'取消'",
							ptbbApply.getId());
			if (wplList != null && wplList.size() > 0) {
				for (WaigouPlanLock wpl : wplList) {
					wpl.setDataStatus("取消");
					totalDao.update(wpl);
				}
			}
			// 终极删除
			totalDao.update(ptbbApply);
			return "true";
		}
	}

	private void fenmojisuan(YuanclAndWaigj fenmowgj1, List<String> sonMarkIds,
			String sqlhistory, ProcardTemplate historyPt1, String sqlhistory1,
			String ywmarkId, ProcardTemplate sonPt, Float mianji,
			Integer miancount, Set<ProcardTemplate> sonsetPt0, Users user) {
		if (fenmowgj1 != null && !sonMarkIds.contains(fenmowgj1.getMarkId())) {
			if (fenmowgj1.getBanbenhao() == null
					|| fenmowgj1.getBanbenhao().length() == 0) {// 无版本号
				sqlhistory += " and (banBenNumber is null or banBenNumber='')";
			} else {
				sqlhistory += " and banBenNumber ='" + fenmowgj1.getBanbenhao()
						+ "'";
			}
			ProcardTemplate wgpt = new ProcardTemplate();
			historyPt1 = (ProcardTemplate) totalDao.getObjectByCondition(
					sqlhistory1, fenmowgj1.getMarkId());
			if (historyPt1 != null) {
				BeanUtils.copyProperties(historyPt1, wgpt,
						new String[] { "id", "procardTemplate", "procardTSet",
								"processTemplate", "zhUsers", "quanzi1",
								"quanzi2", "maxCount", "corrCount", "rootId",
								"fatherId", "belongLayer", "cardXiaoHao",
								"loadMarkId", "ywMarkId", "historyId",
								"thisLength", "thisWidth", "thisHight",
								"productStyle" });
				wgpt.setYwMarkId(ywmarkId);
				wgpt.setHistoryId(historyPt1.getId());
			} else {
				wgpt.setMarkId(fenmowgj1.getMarkId());// 件号
				wgpt.setWgType(fenmowgj1.getWgType());// 物料类别
				wgpt.setCaizhi(fenmowgj1.getCaizhi());// 材质
				wgpt.setProName(fenmowgj1.getName());// 名称\
				wgpt.setBanBenNumber(fenmowgj1.getBanbenhao());// 版本
				wgpt.setKgliao(fenmowgj1.getKgliao());// 供料属性
				wgpt.setSpecification(fenmowgj1.getSpecification());// 规格
				wgpt.setUnit(fenmowgj1.getUnit());// 单位
				wgpt.setProcardStyle("外购");
			}
			wgpt.setBelongLayer(sonPt.getBelongLayer() + 1);// 层数
			wgpt.setFatherId(sonPt.getId());
			wgpt.setRootMarkId(sonPt.getRootMarkId());
			wgpt.setRootId(sonPt.getRootId());
			wgpt.setProductStyle(sonPt.getProductStyle());
			wgpt.setQuanzi1(1f);
			Float quanzhi2 = 0f;
			if (fenmowgj1.getAreakg() == null) {
				throw new RuntimeException(fenmowgj1.getMarkId()
						+ "没有填写每公斤喷粉面积");
			}
			quanzhi2 = (mianji * miancount / fenmowgj1.getAreakg()) / 1000000;
			wgpt.setQuanzi2(quanzhi2);
			sonsetPt0.add(wgpt);
			wgpt.setProcardTemplate(sonPt);
			if (wgpt.getBzStatus() == null) {
				wgpt.setBzStatus("待编制");
			}
			if (!wgpt.getBzStatus().equals("已批准")
					&& !wgpt.getBzStatus().equals("设变发起中")) {
				wgpt.setBianzhiName(user.getName());
				wgpt.setBianzhiId(user.getId());
			}
			wgpt.setRemark("板材粉末自动计算添加");
			totalDao.save(wgpt);
			sonPt.setProcardTSet(sonsetPt0);
			if ("试制".equals(sonPt.getProductStyle())) {
				List<ProcessTemplateFile> ptFileList = totalDao
						.query(
								"from ProcessTemplateFile where processNO is null "
										+ " and status='默认' and markId=? and glId is null ",
								wgpt.getMarkId());
				if (ptFileList != null && ptFileList.size() > 0) {
					for (ProcessTemplateFile ptFile : ptFileList) {
						ProcessTemplateFile ptFile2 = new ProcessTemplateFile();
						BeanUtils.copyProperties(ptFile, ptFile2, new String[] {
								"id", "productStyle", "glId" });
						ptFile2.setGlId(wgpt.getId());
						ptFile2.setProductStyle("试制");
						totalDao.save(ptFile2);
					}
				}

			}
		}
	}

	private boolean isChartNOGzType(String markId) {
		boolean flag = false;
		if (markId == null || "".equals(markId)) {
			return flag;
		}
		// 查出编码库所有编码规则的字符长度
		List<Integer> lenList = totalDao
				.query("SELECT distinct	LENGTH(type) FROM ChartNOGzType  where LENGTH(type)>0 ");
		int markId_len = markId.length();
		if (lenList != null && lenList.size() > 0) {
			out: // 跳出外循环的标志
			for (Integer len : lenList) {
				// 判断传过来的件号的字符长度与编码规则的字符长度是否相等
				if (len == markId_len) {
					// 根据该字符长度查出所有等于改长度所有编码规则
					List<ChartNOGzType> gzTypeList = totalDao.query(
							" from ChartNOGzType where  LENGTH(type) =?", len);
					if (gzTypeList != null && gzTypeList.size() > 0) {
						// 截取该字符前面两个字符
						String beforMarkId = markId.substring(0, 2);
						// 遍历所有改长度的编码规则
						for (ChartNOGzType gzType : gzTypeList) {
							String type = gzType.getType();
							// 判断该字符最前面两个是否属于在规则类型前两个
							if (type.indexOf(beforMarkId) == 0) {
								// 字符串转char数组
								char[] markIds = markId.toCharArray();
								char[] types = type.toCharArray();
								// 分别记录.出现的下标是多少；就是.所处的位置是在哪的
								String type_index = "";
								String markId_index = "";
								for (int i = 0; i < types.length; i++) {
									if ('.' == types[i]) {
										type_index += i;
									}
									if ('.' == markIds[i]) {
										markId_index += i;
									}
								}
								// 比较两则的下标是否完全一致；
								if (markId_index.equals(type_index)) {
									// 1 字符长度一致;
									// 2 前两位一致;
									// 3 分割点.所处位置一致
									// 则说明该件号所用的规则为编码规则返回true;
									flag = true;
									break out;
								}
							}
						}
					}
				}
			}
		} else {// lenList 为null 或者size（）==0说明该公司不用编码库编码直接返回false;
			flag = false;
		}
		return flag;

	}

	@Override
	public String deleteerrorFile() {
		// TODO Auto-generated method stub
		List<ProcessTemplateFile> fileList1 = totalDao
				.query("from ProcessTemplateFile  f, ProcardTemplate p where f.processNO is null and f.glId is not null "
						+ "and p.productStyle='试制' and f.glId = p.getId and f.markId !=p.markId");
		List<ProcessTemplateFile> fileList2 = totalDao
				.query("from ProcessTemplateFile  f, (select p1.markId markId, p2.id from ProcardTemplate p1 ,ProcessTemplate p2 where p1.id=p2.procardTemplate.id and p.productStyle='试制' ) p"
						+ " where f.processNO is not null and f.glId is not null  and f.glId = p.getId and f.markId !=p.markId");
		List<ProcessTemplateFile> deleteList = new ArrayList<ProcessTemplateFile>();
		deleteList.addAll(fileList1);
		deleteList.addAll(fileList2);
		if (deleteList.size() > 0) {
			int n = 0;
			for (int i = (deleteList.size() - 1); i >= 0; i--) {
				ProcessTemplateFile f = deleteList.get(i);
				String fileRealPath = ServletActionContext.getServletContext()
						.getRealPath("/upload/file/processTz");
				File file = new File(fileRealPath + "/" + f.getMonth() + "/"
						+ f.getFileName());
				if (file.exists()) {
					file.delete();
				}
				totalDao.delete(f);
				if (n >= 200) {
					n = 0;
					totalDao.clear();
				}
			}
		}

		return null;
	}

	@Override
	public String gettzDept() {
		// TODO Auto-generated method stub
		String hql1 = "select valueCode from CodeTranslation where type = 'sys' and keyCode='设变必须通知部门'";
		String valueCode = (String) totalDao.getObjectByCondition(hql1);
		if (valueCode != null && valueCode.length() > 0) {
			return valueCode;
		} else {
			return "无";
		}
	}

	public String getJsbcmsg() {
		return jsbcmsg;
	}

	public void setJsbcmsg(String jsbcmsg) {
		this.jsbcmsg = jsbcmsg;
	}

	@Override
	public String mibuglsc(Integer id) {
		// TODO Auto-generated method stub
		Users users = Util.getLoginUser();
		if (users == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply != null) {
			Set<ProcardTemplateBanBen> ptbbSet = ptbbApply
					.getProcardTemplateBanBen();
			Integer rootId = ptbbApply.getRootId();
			List<Integer> hadIdList = totalDao
					.query(
							"select procardId from ProcardAboutBanBenApply where bbapplyId=?",
							id);
			if (ptbbSet != null & ptbbSet.size() > 0) {
				for (ProcardTemplateBanBen ptbb : ptbbSet) {
					List<Procard> procardSameList = null;
					ProcardTemplate pt = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, ptbb
									.getPtId());
					if (pt == null) {
						continue;
					}
					// if (pt.getProductStyle().equals("试制")) {
					// // 同总成生产任务
					// procardSameList = totalDao
					// .query(
					// "from Procard where markId =? and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where procardTemplateId=? and (tjNumber is null or tjNumber<filnalCount) and status in('初始','已发卡','设变锁定','已发料','领工序','待入库'))",
					// ptbb.getMarkId(), rootId);
					// } else {
					// }
					// 同总成生产任务
					procardSameList = totalDao
							.query(
									"from Procard where markId =? and productStyle=? and (sbStatus is null or sbStatus !='删除') and rootId in(select id from Procard where markId=? "
											+ " and status in('初始','已发卡','已发料','设变锁定','领工序','完成','待入库'))",
									ptbb.getMarkId(), ptbbApply
											.getProductStyle(), ptbbApply
											.getMarkId());
					if (procardSameList != null && procardSameList.size() > 0) {
						for (Procard same : procardSameList) {
							if (hadIdList.contains(same.getId())) {
								continue;
							}
							if (same.getStatus().equals("完成")
									&& same.getJihuoStatua() == null) {// 判定为补料报废记录零件
								continue;
							}
							Object[] totalabout = (Object[]) totalDao
									.getObjectByCondition(
											"select markId,ywMarkId,selfCard,status,oldStatus from Procard where id=?",
											same.getRootId());
							ProcardAboutBanBenApply pabb = new ProcardAboutBanBenApply();
							pabb.setProcardId(same.getId());// 零件id
							pabb.setFprocardId(same.getFatherId());
							pabb.setOrderNumber(same.getOrderNumber());// 订单号
							pabb.setRootMarkId(totalabout[0].toString());// 总成件号
							if (totalabout[1] != null) {
								pabb.setYwMarkId(totalabout[1].toString());// 业务件号
							}
							pabb.setRootSelfCard(totalabout[2].toString());// 总成批次
							pabb.setMarkId(same.getMarkId());// 件号
							pabb.setSelfCard(same.getSelfCard());// 批次
							pabb.setProName(same.getProName());// 零件名称
							pabb.setBanebenNumber(same.getBanBenNumber());// 版本号
							if (same.getBanci() != null) {
								pabb.setBanci(same.getBanci());// 版次
							} else {
								pabb.setBanci(0);// 版次
							}
							pabb.setScCount(same.getFilnalCount());

							// pabb.setclType;//处理方案
							pabb.setStatus("待处理");// 状态
							if (totalabout[3].toString().equals("设变锁定")) {
								pabb.setRootStatus(totalabout[4].toString());// 总成状态
							} else {
								pabb.setRootStatus(totalabout[3].toString());// 总成状态
							}
							pabb.setProcardStatus(same.getStatus());// 零件状态
							pabb.setBbapplyId(ptbbApply.getId());// ProcardTemplateBanBenApply
							pabb.setBbId(ptbb.getId());// ProcardTemplateBanBen
							pabb.setProcardStyle(same.getProcardStyle());

							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											same.getId());
							Set<ProcessAboutBanBenApply> processabbjSet = new HashSet<ProcessAboutBanBenApply>();
							if (processList != null && processList.size() > 0) {
								for (int j = 0; j < processList.size(); j++) {
									float zzCount = 0f;
									ProcessInfor process = processList.get(j);
									if (j == 0) {
										if (process.getApplyCount() < same
												.getFilnalCount()) {// 有还没有领的工序
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(null);// 工序号
											processabb.setProcessName("未生产");// 工序名称
											processabb.setScCount(same
													.getFilnalCount()
													- process.getApplyCount());// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
									if (process.getApplyCount() == 0) {
										continue;
									}
									// 此工序已领单未提交数量
									zzCount += process.getApplyCount()
											- process.getSubmmitCount();
									if ((j + 1) <= (processList.size() - 1)) {
										ProcessInfor process2 = processList
												.get((j + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount += process.getSubmmitCount()
												- process2.getApplyCount();
										if (zzCount < 0) {
											zzCount = 0;
										}
									} else {
										zzCount += process.getSubmmitCount();
									}
									if (zzCount > 0) {// 查询此工序当前的转库半成品数量
										if (zzCount > 0) {// 可转库的数量
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(process
													.getProcessNO());// 工序号
											processabb.setProcessName(process
													.getProcessName());// 工序名称
											processabb.setScCount(zzCount);// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
								}
							} else {
								ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
								processabb.setProcessNo(null);// 工序号
								processabb.setProcessName("无");// 工序名称
								processabb.setScCount(same.getFilnalCount());// 生产数量
								processabb.setPabb(pabb);// 生产关联零件
								processabbjSet.add(processabb);
							}
							pabb.setProcessabbSet(processabbjSet);
							if (!same.getStatus().equals("设变锁定")) {
								// 记录原生产状态
								same.setOldStatus(same.getStatus());
								// 暂停生产
								same.setStatus("设变锁定");
							}
							same.setSduser(users.getName());
							totalDao.save(pabb);
							totalDao.update(same);
						}
					}
					// 同件号非同总成零件(对到明细上面)
					List<Procard> procardList = totalDao
							.query(
									"from Procard where (sbStatus is null or sbStatus !='删除') and productStyle=? and rootId not in(select id from Procard where markId =?) and markId =?"
											+ "and rootId in(select id from Procard where procardStyle='总成' and status in('初始','已发卡','已发料','设变锁定','领工序','完成','待入库'))",
									ptbbApply.getProductStyle(), ptbbApply
											.getMarkId(), pt.getMarkId());
					if (procardList != null && procardList.size() > 0) {
						for (Procard other : procardList) {
							if (hadIdList.contains(other.getId())) {
								continue;
							}
							if (other.getStatus().equals("完成")
									&& other.getJihuoStatua() == null) {// 判定为补料报废记录零件
								continue;
							}
							ProcardAboutBanBenApply pabb = new ProcardAboutBanBenApply();
							Object[] totalabout = (Object[]) totalDao
									.getObjectByCondition(
											"select markId,ywMarkId,selfCard,status,oldStatus from Procard where id=?",
											other.getRootId());
							pabb.setProcardId(other.getId());// 零件id
							pabb.setFprocardId(other.getFatherId());
							pabb.setOrderNumber(other.getOrderNumber());// 订单号
							pabb.setRootMarkId(totalabout[0].toString());// 总成件号
							if (totalabout[1] != null) {
								pabb.setYwMarkId(totalabout[1].toString());// 业务件号
							}
							pabb.setRootSelfCard(totalabout[2].toString());// 总成批次
							pabb.setMarkId(other.getMarkId());// 件号
							pabb.setSelfCard(other.getSelfCard());// 批次
							pabb.setProName(other.getProName());// 零件名称
							pabb.setBanebenNumber(other.getBanBenNumber());// 版本号
							if (other.getBanci() != null) {
								pabb.setBanci(other.getBanci());// 版次
							} else {
								pabb.setBanci(0);// 版次
							}
							pabb.setScCount(other.getFilnalCount());
							pabb.setStatus("待处理");
							if (totalabout[3].toString().equals("设变锁定")) {
								pabb.setRootStatus(totalabout[4].toString());// 总成状态
							} else {
								pabb.setRootStatus(totalabout[3].toString());// 总成状态
							}
							pabb.setProcardStatus(other.getStatus());// 零件状态
							pabb.setBbapplyId(ptbbApply.getId());
							;// ProcardTemplateBanBenApply
							pabb.setBbId(ptbb.getId());// ProcardTemplateBanBen
							pabb.setProcardStyle(other.getProcardStyle());
							List<ProcessInfor> processList = totalDao
									.query(
											"from ProcessInfor where procard.id=? order by processNO",
											other.getId());
							Set<ProcessAboutBanBenApply> processabbjSet = new HashSet<ProcessAboutBanBenApply>();
							if (processList != null && processList.size() > 0) {
								for (int j = 0; j < processList.size(); j++) {
									float zzCount = 0f;
									ProcessInfor process = processList.get(j);
									if (j == 0) {
										if (process.getSubmmitCount() == 0) {
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											if (process.getApplyCount() == 0) {
												processabb.setProcessNo(null);// 工序号
												processabb
														.setProcessName("未生产");// 工序名称
											} else {
												processabb.setProcessNo(process
														.getProcessNO());// 工序号
												processabb
														.setProcessName(process
																.getProcessName());// 工序名称
											}
											processabb.setScCount(other
													.getFilnalCount());// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
											break;
										}
									}
									// 此工序已领单未提交数量
									zzCount += process.getApplyCount()
											- process.getSubmmitCount();
									if ((j + 1) <= (processList.size() - 1)) {
										ProcessInfor process2 = processList
												.get((j + 1));
										// 此道工序做完未流转到下道工序的数量
										zzCount += process.getSubmmitCount()
												- process2.getApplyCount();
										if (zzCount < 0) {
											zzCount = 0;
										}
									} else {
										zzCount += process.getSubmmitCount();
									}
									if (zzCount > 0) {// 查询此工序当前的转库半成品数量
										if (zzCount > 0) {// 可转库的数量
											ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
											processabb.setProcessNo(process
													.getProcessNO());// 工序号
											processabb.setProcessName(process
													.getProcessName());// 工序名称
											processabb.setScCount(zzCount);// 生产数量
											processabb.setPabb(pabb);// 生产关联零件
											processabbjSet.add(processabb);
										}
									}
								}
							} else {
								ProcessAboutBanBenApply processabb = new ProcessAboutBanBenApply();
								processabb.setProcessNo(null);// 工序号
								processabb.setProcessName("无");// 工序名称
								processabb.setScCount(other.getFilnalCount());// 生产数量
								processabb.setPabb(pabb);// 生产关联零件
								processabbjSet.add(processabb);
							}
							pabb.setProcessabbSet(processabbjSet);
							// 记录原生产状态
							other.setOldStatus(other.getStatus());
							// 暂停生产
							if (!other.getStatus().equals("设变锁定")) {
								// 记录原生产状态
								other.setOldStatus(other.getStatus());
								// 暂停生产
								other.setStatus("设变锁定");
							}
							other.setSduser(users.getName());
							totalDao.save(pabb);
							totalDao.update(other);
						}
					}
				}
			}
		}
		return null;
	}

	// @Override
	public String getbomCompare(File bomFile, String fileName) {
		// TODO Auto-generated mSethod stub
		String jymsg = "";
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + fileName);
		try {
			FileCopyUtils.copy(bomFile, file2);
			org.apache.poi.ss.usermodel.Workbook wb = null;
			org.apache.poi.ss.usermodel.Sheet sheet = null;
			org.apache.poi.ss.usermodel.Row row = null;
			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			String ext = fileName.substring(fileName.lastIndexOf("."));
			if (".xls".equals(ext)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(ext)) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = null;
			}
			sheet = wb.getSheetAt(wb.getActiveSheetIndex());
			int excellastIndex = sheet.getLastRowNum();// 获得总行数
			if (excellastIndex > 2) {
				String hql_wgType = "select name from Category where type = '物料' and id not in (select fatherId from Category where fatherId  is not NULL)";
				List<String> umList = totalDao.query(hql_wgType);
				StringBuffer sb = new StringBuffer();
				if (umList != null && umList.size() > 0) {
					for (String um : umList) {
						sb.append("," + um);
					}
					sb.append(",");
				}
				String umStr = sb.toString();
				row = sheet.getRow(3);
				String ywMarkId = row.getCell(4).toString();
				if (ywMarkId != null && ywMarkId.length() > 0) {
					ywMarkId = ywMarkId.replaceAll(" ", "");
					ywMarkId = ywMarkId.replaceAll("：", ":");
					// ywMarkId = ywMarkId.replaceAll("（", "(");
					// ywMarkId = ywMarkId.replaceAll("）", ")");
					// ywMarkId = ywMarkId.replaceAll("x", "X");
					String[] ywMarkIds = ywMarkId.split(":");
					if (ywMarkIds != null && ywMarkIds.length == 2) {
						ywMarkId = ywMarkIds[1];
					} else {
						ywMarkId = null;
					}
				}
				List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
				boolean isbegin = false;
				int base = 0;
				// key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				for (int i = base; i <= excellastIndex; i++) {
					row = sheet.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					ProcardTemplate pt = new ProcardTemplate();
					pt.setYwMarkId(ywMarkId);
					String number = "";// 序号
					try {
						number = row.getCell(0).toString();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					number = number.replaceAll(" ", "");
					number = number.replaceAll("	", "");
					try {
						if (number.indexOf(".") > 0) {
							number = number.substring(0, number.indexOf("."));
						}
						Integer num = Integer.valueOf(number);
						isbegin = true;
					} catch (Exception e) {
						// TODO: handle exception
						if (isbegin) {
							break;
						} else {
							base++;
							continue;
						}
					}
					String t1 = null;
					String t2 = null;
					String t3 = null;
					if (row.getCell(1) != null) {
						t1 = row.getCell(1).toString();
					}
					if (row.getCell(2) != null) {
						t2 = row.getCell(2).toString();
					}
					if (row.getCell(3) != null) {
						t3 = row.getCell(3).toString();
					}
					pt.setT1(t1);
					pt.setT2(t2);
					pt.setT3(t3);
					String markId = t3; // 件号
					if (markId != null) {
						markId = markId.replaceAll(" ", "");
						markId = markId.replaceAll("	", "");
						markId = markId.replaceAll("_", "-");
						markId = markId.replaceAll("_s", "-S");
						markId = markId.replaceAll("-s", "-S");
						markId = markId.replaceAll("dkba", "DKBA");
					}
					String hwdrMarkId = t2;
					if (hwdrMarkId != null) {
						hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
						hwdrMarkId = hwdrMarkId.replaceAll("	", "");
						hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
						hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
					}
					boolean zouzui = true;
					if (markId == null || markId.length() == 0) {
						jymsg += "第" + (i + 1) + "行,件号不能为空!</br>";
					} else {
						if (markId.startsWith("DKBA") && markId.length() >= 12
								&& !markId.equals("DKBA3359")
								&& markId.indexOf(".") < 0) {
							markId = markId.substring(0, 5) + "."
									+ markId.substring(5, 8) + "."
									+ markId.substring(8, markId.length());
						}
						if ((markId.equals("DKBA0.480.0128")
								|| markId.equals("DKBA0.480.0251")
								|| markId.equals("DKBA0.480.0236")
								|| markId.equals("DKBA0.480.0345")
								|| markId.equals("DKBA0.480.1381") || markId
								.equals("*"))
								&& hwdrMarkId != null
								&& hwdrMarkId.length() > 0) {
							zouzui = false;
							markId = hwdrMarkId;
						}
					}
					String banben = "";
					try {
						banben = row.getCell(4).toString();
					} catch (Exception e1) {
						// e1.printStackTrace();
					}
					if (!zouzui) {
						banben = null;
					}
					String proName = row.getCell(5).toString();
					proName = proName.replaceAll(" ", "");
					proName = proName.replaceAll("	", "");
					// proName = proName.replaceAll("（", "(");
					// proName = proName.replaceAll("）", ")");
					// proName = proName.replaceAll("x", "X");
					Integer belongLayer = null;// 层数
					Float corrCount = null;// 消耗量
					for (int jc = 6; jc <= 15; jc++) {
						String xiaohao = row.getCell(jc).toString();
						if (xiaohao != null && xiaohao.length() > 0) {
							try {
								corrCount = Float.parseFloat(xiaohao);
								belongLayer = jc - 5;
								break;
							} catch (Exception e) {
								jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
							}
						}
						// else if(xiaohao==null||xiaohao.length()==0){
						// jymsg+= "消耗量有异常,第" + (i + 1)
						// + "行错误!</br>";
						// }
					}
					if (corrCount == null || corrCount == 0) {
						jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
					}
					String source = row.getCell(16).toString();
					String unit = row.getCell(18).toString();
					unit = unit.replaceAll(" ", "");
					unit = unit.replaceAll("	", "");
					String biaochu = "";
					try {
						biaochu = row.getCell(19).toString();
						if ("A000".equals(biaochu)) {
							biaochu = "";
						}
						// biaochu = biaochu.replaceAll("）", ")");
						// biaochu = biaochu.replaceAll("x", "X");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String clType = "";
					try {
						clType = row.getCell(20).toString();
						// if ("DKBA8.052.6323".equals(markId)) {
						// System.out
						// .println(markId + " ====================");
						// }
						// clType = clType.replaceAll("（", "(");
						// clType = clType.replaceAll("）", ")");
						// clType = clType.replaceAll("x", "X");
					} catch (Exception e) {
						e.printStackTrace();
					}
					clType = clType.replaceAll(" ", "");
					clType = clType.replaceAll("	", "");
					String thislengthStr = row.getCell(21).toString();
					String thiswidthStr = row.getCell(22).toString();
					String thishigthStr = row.getCell(23).toString();
					Float thislength = null;
					Float thiswidth = null;
					Float thishigth = null;
					try {
						thislength = Float.parseFloat(thislengthStr);

					} catch (Exception e) {
					}
					try {
						thiswidth = Float.parseFloat(thiswidthStr);
					} catch (Exception e) {
					}
					try {
						thishigth = Float.parseFloat(thishigthStr);
					} catch (Exception e) {
					}
					String loadMarkId = "";
					try {
						loadMarkId = row.getCell(24).toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
					loadMarkId = loadMarkId.replaceAll(" ", "");
					loadMarkId = loadMarkId.replaceAll("	", "");
					loadMarkId = loadMarkId.replaceAll("（", "(");
					loadMarkId = loadMarkId.replaceAll("）", ")");
					loadMarkId = loadMarkId.replaceAll("x", "X");
					loadMarkId = loadMarkId.replaceAll("_", "-");
					pt.setRemark(loadMarkId);
					String wgType = null;// 外购件类型
					try {
						wgType = row.getCell(25).toString();
						wgType = wgType.replaceAll(" ", "");
						wgType = wgType.replaceAll("	", "");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					String specification = null;// 规格
					try {
						specification = row.getCell(26).toString();
						specification = specification.replaceAll(" ", "");
						specification = specification.replaceAll("	", "");
						specification = specification.replaceAll("（", "(");
						specification = specification.replaceAll("）", ")");
						specification = specification.replaceAll("x", "X");
						specification = specification.replaceAll("_", "-");
					} catch (Exception e) {
						// e.printStackTrace();
					}
					for (int n = 10; n < 14; n++) {
						markId = markId
								.replaceAll(String.valueOf((char) n), "");
						proName = proName.replaceAll(String.valueOf((char) n),
								"");
						// tuhao = tuhao.replaceAll(String.valueOf((char) n),
						// "");
						if (banben != null && banben.length() > 0) {
							banben = banben.replaceAll(
									String.valueOf((char) n), "");
						}
						if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
						// type = type.replaceAll(String.valueOf((char) n), "");
					}
					pt.setSpecification(specification);
					pt.setProcardStyle("待定");
					boolean hasTrue = false;
					String useguige = specification;
					if (useguige == null || useguige.length() == 0) {
						useguige = loadMarkId;
					}
					if (markId.startsWith("DKBA0")
							|| markId.startsWith("dkba0")) {// 标准件不启用版本号
						banben = null;
					}
					// 技术规范号查找大类
					String procardStyle = null;
					List<String> sameMax = bmmap.get(markId + useguige);
					if (sameMax != null) {
						proName = sameMax.get(3);
						procardStyle = sameMax.get(0);
						if (sameMax.get(1).length() == 5) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%05d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setSpecification(useguige);
							pt.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							pt.setSpecification(useguige);
							pt.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)));
							pt.setProcardStyle("外购");
							// }
						}
						// 存放到编码库
						// ChartNOSC chartnosc = (ChartNOSC) totalDao
						// .getObjectByCondition(
						// " from ChartNOSC where chartNO  =? ",
						// markId);
						// if (chartnosc == null && isChartNOGzType(markId)) {
						// 同步添加到编码库里
						// chartnosc = new ChartNOSC();
						// chartnosc.setChartNO(markId);
						// chartnosc.setChartcode(Util.Formatnumber(markId));
						// chartnosc.setIsuse("YES");
						// chartnosc.setRemak("BOM导入");
						// chartnosc.setSjsqUsers(user.getName());
						// totalDao.save(chartnosc);
						// }
					} else {
						CodeTranslation codeTranslation = (CodeTranslation) totalDao
								.getObjectByCondition(
										"from CodeTranslation where keyCode=? and type='技术规范'",
										markId);
						String jsType = null;
						if (codeTranslation != null) {
							jsType = codeTranslation.getValueCode();
						}
						if (jsType != null && jsType.length() > 0) {
							String hasMarkId = (String) totalDao
									.getObjectByCondition("select markId from YuanclAndWaigj where (tuhao ='"
											+ markId
											+ "-"
											+ useguige
											+ "' or tuhao ='"
											+ markId
											+ useguige
											+ "'"
											+ " or (tuhao like '"
											+ markId
											+ "-%' and specification ='"
											+ useguige + "'))");
							if (hasMarkId != null && hasMarkId.length() > 0) {
								pt.setProcardStyle("外购");
								pt.setTuhao(markId + "-" + useguige);
								pt.setSpecification(useguige);
								markId = hasMarkId;
							} else {
								proName = codeTranslation.getValueName();
								if (jsType.length() == 4) {
									jsType = jsType + ".";
								}
								// 先去原材料外购件库里查询
								String dlType = "外购件";
								// if (hasMarkId == null) {
								hasMarkId = (String) totalDao
										.getObjectByCondition("select markId from ProcardTemplate where (dataStatus is null or dataStatus!='删除') and (tuhao ='"
												+ markId
												+ "-"
												+ useguige
												+ "' or tuhao ='"
												+ markId
												+ useguige
												+ "'"
												+ " or (tuhao like '"
												+ markId
												+ "-%' and specification ='"
												+ useguige + "'))");
								// }
								if (hasMarkId != null && hasMarkId.length() > 0) {
									markId = hasMarkId;
									dlType = "自制";
								} else {
									// 需要新增
									Integer maxInteger = 0;
									// 先遍历map
									for (String key : bmmap.keySet()) {
										List<String> samjsType = bmmap.get(key);
										if (samjsType != null
												&& samjsType.get(1).equals(
														jsType)) {
											Integer nomax = Integer
													.parseInt(samjsType.get(2));
											pt.setProcardStyle("外购");
											if (maxInteger < nomax) {
												maxInteger = nomax;
											}
										}
									}
									if (maxInteger == 0) {
										String maxNo1 = (String) totalDao
												.getObjectByCondition("select max(markId) from YuanclAndWaigj where markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null) {
											// maxNo1 = (String) totalDao
											// .getObjectByCondition("select max(trademark) from YuanclAndWaigj where clClass='原材料' and tuhao like'"
											// + jsType + "%'");
											// if (hasMarkId != null) {
											// dlType = "原材料";
											// }
											// } else {
											dlType = "外购件";
											pt.setProcardStyle("外购");
										}
										String maxNo2 = (String) totalDao
												.getObjectByCondition("select max(markId) from ProcardTemplate where  markId like'"
														+ jsType
														+ "%' and markId not like '"
														+ jsType + "%.%' ");// 因为当零件类型为待定时原材料外购件库里没有办法查询
										String maxNo3 = (String) totalDao
												.getObjectByCondition("select max(chartNO) from ChartNOSC where  chartNO like'"
														+ jsType
														+ "%' and chartNO not like '"
														+ jsType + "%.%'");
										if (maxNo1 != null
												&& maxNo1.length() > 0) {
											String[] maxStrs = maxNo1
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo2 != null
												&& maxNo2.length() > 0) {
											String[] maxStrs = maxNo2
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
										if (maxNo3 != null
												&& maxNo3.length() > 0) {
											String[] maxStrs = maxNo3
													.split(jsType);
											if (maxStrs.length > 0
													&& maxStrs[1] != null) {
												try {
													Integer nowMax = Integer
															.parseInt(maxStrs[1]);
													if (maxInteger < nowMax) {
														maxInteger = nowMax;
													}
												} catch (Exception e) {
													// TODO: handle exception
												}
											}
										}
									}
									maxInteger++;
									List<String> list = new ArrayList<String>();
									if ((dlType == null || dlType.equals(""))
											&& (procardStyle == null || procardStyle
													.equals(""))) {
										procardStyle = "待定";
										list.add("待定");
									} else {
										list.add(dlType);
									}
									pt.setSpecification(useguige);
									pt.setTuhao(markId + "-" + useguige);
									list.add(jsType);
									list.add(maxInteger + "");
									list.add(proName);
									bmmap.put(markId + useguige, list);
									if (jsType.length() == 5) {
										markId = jsType
												+ String.format("%05d",
														maxInteger);

									} else if (jsType.length() == 6) {
										markId = jsType
												+ String.format("%04d",
														maxInteger);
									}

									pt.setShowSize(loadMarkId);
								}
								hasTrue = true;
								// totalDao.clear();
							}
						}

					}
					// if (loadMarkId != null) {
					// if (zouzui
					// & (!markId.equals("DKBA0.480.0128")
					// && !markId.equals("DKBA0.480.0251")
					// && !markId.equals("DKBA0.480.0236")
					// && !markId.equals("DKBA0.480.0345")
					// && !markId.equals("DKBA0.480.1381"))) {
					// if (loadMarkId.contains("Al")
					// && !markId.endsWith("-AL")) {
					// markId += "-AL";
					// loadMarkId = "";
					// } else if ((loadMarkId.contains("stainless") ||
					// loadMarkId
					// .contains("Stainless"))
					// && !markId.endsWith("-S")) {
					// markId += "-S";
					// loadMarkId = "";
					// } else if (loadMarkId.contains("zinc")
					// && loadMarkId.contains("yellow")
					// && !markId.endsWith("-ZC")) {
					// markId += "-ZC";
					// loadMarkId = "";
					// }
					// }
					// }
					if (!hasTrue) {
						// 尝试将国标markId转换为系统使用的markId
						Object[] change = (Object[]) totalDao
								.getObjectByCondition(
										"select valueCode,valueName from CodeTranslation where keyCode=? or valueCode=? and type='国标'",
										markId, markId);
						if (change != null && change.length == 2
								&& change[0] != null
								&& change[0].toString().length() > 0) {
							pt.setTuhao(markId);
							markId = change[0].toString();
							if (change[1] != null
									&& change[1].toString().length() > 0) {
								proName = change[1].toString();
							}
							banben = null;// 国标件不要版本
						}

					}
					if (markId != null
							&& (markId.startsWith("GB") || markId
									.startsWith("gb"))) {
						jymsg += "第" + (i + 1) + "行,国标件:" + markId
								+ "无法在国标编码库中查到!</br>";
					}

					if (markId != null
							&& (markId.startsWith("DKBA0") || markId
									.equals("DKBA3359"))) {

						jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
								+ "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
					}
					pt.setBelongLayer(belongLayer);
					pt.setProName(proName);
					// pt.setSpecification(type);
					if (source != null) {
						if (source.equals("外购") || source.equals("原材料")) {
							pt.setProcardStyle("外购");
						} else if (source.equals("半成品")) {
							pt.setProcardStyle("外购");
							pt.setNeedProcess("yes");
						} else if (source.equals("生产") || source.equals("自制")
								|| source.equals("组合")) {
							pt.setProcardStyle("自制");
						}
					}
					if (procardStyle != null) {
						pt.setProcardStyle(procardStyle);
					}
					if (pt.getProcardStyle() == null
							|| pt.getProcardStyle().length() == 0) {
						pt.setProcardStyle("待定");
					}
					pt.setMarkId(markId);
					pt.setBanBenNumber(banben);
					pt.setUnit(unit);
					pt.setCorrCount(corrCount);
					if (pt.getTuhao() == null || pt.getTuhao().length() <= 0) {
						pt.setTuhao(hwdrMarkId);
					}

					// if (bizhong != null && bizhong.length() > 0) {
					// pt.setBili(Float.parseFloat(bizhong.toString()));
					// }
					pt.setAddcode(user.getCode());
					pt.setAdduser(user.getName());
					pt.setAddtime(nowTime);
					pt.setClType(clType);
					pt.setBiaochu(biaochu);
					pt.setBzStatus("待编制");
					pt.setBianzhiId(user.getId());
					pt.setBianzhiName(user.getName());
					pt.setProductStyle("批产");
					pt.setCardXiaoHao(1f);
					pt.setLoadMarkId(loadMarkId);
					pt.setXuhao(0f);
					pt.setThisLength(thislength);
					pt.setThisWidth(thiswidth);
					pt.setThisHight(thishigth);
					if (pt.getProcardStyle().equals("外购")
							|| pt.getProcardStyle().equals("半成品")) {
						String addSql = null;
						if (banben == null || banben.length() == 0) {
							addSql = " and (banbenhao is null or banbenhao='')";
						} else {
							addSql = " and banbenhao='" + banben + "'";
						}
						// 库中查询现有类型
						String haswgType = (String) totalDao
								.getObjectByCondition(
										"select wgType from YuanclAndWaigj where  productStyle='批产' and (status is null or status!='禁用') and markId=?  "
												+ addSql, markId);
						if (haswgType != null && haswgType.length() > 0) {
							if (wgType != null && wgType.length() > 0) {
								if (!haswgType.equals(wgType)) {
									jymsg += "第" + (i + 1) + "行错误，物料类别:"
											+ wgType + "与现有物料类别:" + haswgType
											+ "不符<br/>";
								}
							} else {
								wgType = haswgType;
							}
						} else {
							if (wgType == null || wgType.length() == 0) {
								jymsg += "第" + (i + 1) + "行错误，该外购件未填写物料类别<br/>";
							} else if (umStr == null || umStr.length() == 0
									|| !umStr.contains("," + wgType + ",")) {
								jymsg += "第" + (i + 1)
										+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
							}
						}
					}
					pt.setWgType(wgType);
					// 生产节拍 单班时长
					if (wgType != null && wgType.length() > 0) {
						Category category = (Category) totalDao
								.getObjectByCondition(
										" from Category where name = ? and id not in (select fatherId from Category where fatherId  is not NULL) and avgDeliveryTime is not null order by id desc ",
										wgType);
						if (category != null) {
							if (category.getAvgProductionTakt() != null
									&& category.getAvgDeliveryTime() != null) {
								pt.setAvgProductionTakt(category
										.getAvgProductionTakt());
								pt.setAvgDeliveryTime(category
										.getAvgDeliveryTime());
							} else {
								jymsg += "第" + (i + 1) + "行错误，该外购件物料类别("
										+ wgType + "),未填写生产节拍和配送时长!<br/>";
							}
						} else {
							jymsg += "第" + (i + 1)
									+ "行错误，该外购件物料类别,不在现有物料类别库中!<br/>";
						}
					}
					ptList.add(pt);

				}

				if (ptList.size() > 0) {
					HttpServletResponse response = (HttpServletResponse) ActionContext
							.getContext().get(
									ServletActionContext.HTTP_RESPONSE);
					OutputStream os = response.getOutputStream();
					response.reset();
					response.setHeader("Content-disposition",
							"attachment; filename="
									+ new String(fileName.getBytes("GB2312"),
											"8859_1"));
					response.setContentType("application/msexcel");
					WritableWorkbook wwb = Workbook.createWorkbook(os);
					// WritableSheet ws1 = wwb.createSheet("sheet1", 0);
					WritableSheet ws = wwb.createSheet("生产物料BOM表", 0);
					ws.setColumnView(4, 20);
					ws.setColumnView(3, 10);
					ws.setColumnView(2, 20);
					ws.setColumnView(1, 12);
					WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
							WritableFont.NO_BOLD, false,
							UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
					WritableCellFormat wc = new WritableCellFormat(wf);
					wc.setAlignment(Alignment.LEFT);
					// ws.addCell(new jxl.write.Label(0, 0, "物料代码", wc));
					// ws.addCell(new jxl.write.Label(1, 0, "物料名称", wc));
					// ws.addCell(new jxl.write.Label(2, 0, "规格型号", wc));
					// ws.addCell(new jxl.write.Label(3, 0, "物料属性", wc));
					ws.addCell(new jxl.write.Label(0, 3, "版本:", wc));
					ws
							.addCell(new jxl.write.Label(4, 3, "产品编码:"
									+ ywMarkId, wc));
					ws.addCell(new jxl.write.Label(22, 3, "客户名称：华为", wc));
					ws.mergeCells(4, 2, 7, 2);
					ws.mergeCells(0, 2, 3, 2);
					// ws.addCell(new jxl.write.Label(7, 0, "建立日期", wc));
					// ws.addCell(new jxl.write.Label(8, 0, "使用人员", wc));
					// ws.addCell(new jxl.write.Label(0, 1,
					// totalCard.getYwMarkId(),
					// wc));
					// ws.addCell(new jxl.write.Label(1, 1,
					// totalCard.getProName(),
					// wc));
					// ws.addCell(new jxl.write.Label(2, 1,
					// totalCard.getSpecification(),
					// wc));
					// ws.addCell(new jxl.write.Label(3, 1,
					// totalCard.getProcardStyle(),
					// wc));
					// ws.addCell(new jxl.write.Label(4, 1, totalCard.getUnit(),
					// wc));
					// ws.addCell(new jxl.write.Label(5, 1,
					// totalCard.getYwMarkId(),
					// wc));
					// ws.addCell(new jxl.write.Label(6, 1,
					// totalCard.getCreateUserName(),
					// wc));
					// ws
					// .addCell(new jxl.write.Label(7, 1, totalCard
					// .getCreateDate(), wc));
					// ws.addCell(new jxl.write.Label(8, 1,
					// totalCard.getCreateUserName(),
					// wc));
					base--;
					ws.addCell(new jxl.write.Label(0, base, "序号", wc));
					ws.addCell(new jxl.write.Label(1, base, "物料编号", wc));
					ws.addCell(new jxl.write.Label(2, base, "模型图号", wc));
					ws.addCell(new jxl.write.Label(3, base, "图号", wc));
					ws.addCell(new jxl.write.Label(4, base, "版本", wc));
					ws.addCell(new jxl.write.Label(5, base, "名称", wc));
					ws.mergeCells(6, 1, 15, 1);// 合并单元格
					WritableCellFormat wc2 = new WritableCellFormat(wf);
					wc2.setAlignment(Alignment.CENTRE);
					ws
							.addCell(new jxl.write.Label(6, base,
									"阶层/单台数量6-15", wc2));
					ws.addCell(new jxl.write.Label(16, base, "来源", wc));
					ws.addCell(new jxl.write.Label(17, base, "实际用量", wc));
					ws.addCell(new jxl.write.Label(18, base, "单位", wc));
					ws.addCell(new jxl.write.Label(19, base, "表处", wc));
					ws.addCell(new jxl.write.Label(20, base, "材质", wc));
					ws.addCell(new jxl.write.Label(21, base, "长", wc));
					ws.addCell(new jxl.write.Label(22, base, "宽", wc));
					ws.addCell(new jxl.write.Label(23, base, "高", wc));
					ws.addCell(new jxl.write.Label(24, base, "备注", wc));
					ws.addCell(new jxl.write.Label(25, base, "材料类别", wc));
					ws.addCell(new jxl.write.Label(26, base, "规格(导入截止列)", wc));
					ws.addCell(new jxl.write.Label(27, base, "零件类型", wc));
					ws.addCell(new jxl.write.Label(28, base, "差异类别", wc));
					ws.addCell(new jxl.write.Label(29, base, "差异明细", wc));
					hasbd = new ArrayList<Integer>();
					List<String> passwgTypeList = totalDao
							.query("select name from Category where type='物料' and(name='粉末' or fatherId in(select id from Category where type='物料' and name='板材'))");
					comparept(ws, wc, ptList, 0, null, (base + 1),
							passwgTypeList);
					wwb.write();
					wwb.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "true";
	}

	public void comparept(WritableSheet ws, WritableCellFormat wc,
			List<ProcardTemplate> ptList, int i, ProcardTemplate oldPt,
			int exclebase, List<String> passwgTypeList) {
		for (int j = i; j < ptList.size(); j++) {
			if (hasbd.contains(j)) {
				continue;
			} else {
				hasbd.add(j);
			}
			try {
				ProcardTemplate pt = ptList.get(j);
				if (oldPt == null || !oldPt.getMarkId().equals(pt.getMarkId())) {
					oldPt = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where productStyle='批产' and markId=?  "
											+ "and (dataStatus <> '删除' or dataStatus is null ) and (banbenstatus <> '历史' or banbenstatus is null )",
									pt.getMarkId());
				}
				if (oldPt == null) {
					setExcleMsg(j, ws, wc, pt, 1, "新零件", "", exclebase);
					// 比对下层
					if (ptList.size() > (j + 1)) {
						for (int k = (j + 1); k < ptList.size(); k++) {
							ProcardTemplate son = ptList.get(k);
							if (pt.getBelongLayer().equals(
									(son.getBelongLayer() - 1))) {// 下一层
								comparept(ws, wc, ptList, k, null, exclebase,
										passwgTypeList);
							} else if (pt.getBelongLayer() < (son
									.getBelongLayer() - 1)) {// 下下阶层
							} else {
								return;
							}
						}
					}
				} else {
					int changeCount = 0;
					StringBuffer changetypesb = new StringBuffer();
					StringBuffer changeDetailsb = new StringBuffer();
					if (!Util.isEquals(pt.getBanBenNumber(), oldPt
							.getBanBenNumber())) {
						changeCount++;
						if (changetypesb.length() == 0) {
							changetypesb.append("版本");
						} else {
							changetypesb.append("、" + "版本");
						}
						changeDetailsb.append(changeCount + "、原版本："
								+ oldPt.getBanBenNumber() + ",新版本："
								+ pt.getBanBenNumber() + "。");
					}
					if (!pt.getProcardStyle().equals("待定")
							&& !Util.isEquals(pt.getProcardStyle(), oldPt
									.getProcardStyle())) {
						changeCount++;
						if (changetypesb.length() == 0) {
							changetypesb.append("零件类型");
						} else {
							changetypesb.append("、" + "零件类型");
						}
						if (changeCount > 1) {
							changeDetailsb.append("\n");
						}
						changeDetailsb.append(changeCount + "、原零件类型："
								+ oldPt.getProcardStyle() + ",新零件类型："
								+ pt.getProcardStyle() + "。");
					}
					// // 长，宽，高
					// if (!Util.isEquals(pt.getThisLength(), oldPt
					// .getThisLength())) {
					// changeCount++;
					// if (changetypesb.length() == 0) {
					// changetypesb.append("长");
					// } else {
					// changetypesb.append("、" + "长");
					// }
					// changeDetailsb.append(changeCount + "、原长："
					// + oldPt.getThisLength() + ",新长："
					// + pt.getThisLength() + "。");
					// }
					// if (!Util.isEquals(pt.getThisWidth(),
					// oldPt.getThisWidth())) {
					// changeCount++;
					// if (changetypesb.length() == 0) {
					// changetypesb.append("宽");
					// } else {
					// changetypesb.append("、" + "宽");
					// }
					// changeDetailsb.append(changeCount + "、原宽："
					// + oldPt.getThisWidth() + ",新宽："
					// + pt.getThisWidth() + "。");
					// }
					// if (!Util.isEquals(pt.getThisHight(),
					// oldPt.getThisHight())) {
					// changeCount++;
					// if (changetypesb.length() == 0) {
					// changetypesb.append("高");
					// } else {
					// changetypesb.append("、" + "高");
					// }
					// changeDetailsb.append(changeCount + "、原高："
					// + oldPt.getThisHight() + ",新高："
					// + pt.getThisHight() + "。");
					// }
					if (pt.getProcardStyle().equals("外购")) {
						// if (pt.getWgType() != null
						// && pt.getWgType().length() > 0
						// && !Util.isEquals(pt.getWgType(), oldPt
						// .getWgType())) {
						// changeCount++;
						// if (changetypesb.length() == 0) {
						// changetypesb.append("物料类别");
						// } else {
						// changetypesb.append("、" + "物料类别");
						// }
						// changeDetailsb.append(changeCount + "、原物料类别："
						// + oldPt.getWgType() + ",新物料类别："
						// + pt.getWgType() + "。");
						// }
						Float yongliang = null;
						if (oldPt.getQuanzi1() == null
								|| oldPt.getQuanzi2() == null
								|| oldPt.getQuanzi1() == 0) {
							changeCount++;
							if (changetypesb.length() == 0) {
								changetypesb.append("用量");
							} else {
								changetypesb.append("、" + "用量");
							}
							changeDetailsb.append(changeCount + "、原用量：异常,新用量："
									+ pt.getCorrCount() + "。");
						} else {
							yongliang = oldPt.getQuanzi2() / oldPt.getQuanzi1();
							Float cy = yongliang / pt.getCorrCount();
							if (cy < 0.99 || cy > 1.0001) {
								changeCount++;
								if (changetypesb.length() == 0) {
									changetypesb.append("用量");
								} else {
									changetypesb.append("、" + "用量");
								}
								if (changeCount > 1) {
									changeDetailsb.append("\n");
								}
								changeDetailsb.append(changeCount + "、原用量："
										+ yongliang + ",新用量："
										+ pt.getCorrCount() + "。");
							}
						}

					} else {
						// if (!Util.isEquals(pt.getBiaochu(),
						// oldPt.getBiaochu())) {
						// changeCount++;
						// if (changetypesb.length() == 0) {
						// changetypesb.append("表处");
						// } else {
						// changetypesb.append("、" + "表处");
						// }
						// changeDetailsb.append(changeCount + "、原表处："
						// + oldPt.getBiaochu() + ",新表处："
						// + pt.getBiaochu() + "。");
						// }
						// if (!Util.isEquals(pt.getClType(),
						// oldPt.getClType())) {
						// changeCount++;
						// if (changetypesb.length() == 0) {
						// changetypesb.append("材质");
						// } else {
						// changetypesb.append("、" + "材质");
						// }
						// changeDetailsb.append(changeCount + "、原材质："
						// + oldPt.getClType() + ",新材质："
						// + pt.getClType() + "。");
						// }
						Float yongliang = null;
						if (oldPt.getCorrCount() == null) {
							changeCount++;
							if (changetypesb.length() == 0) {
								changetypesb.append("用量");
							} else {
								changetypesb.append("、" + "用量");
							}
							if (changeCount > 1) {
								changeDetailsb.append("\n");
							}
							changeDetailsb.append(changeCount + "、原用量：异常,新用量："
									+ pt.getCorrCount() + "。");
						} else {
							yongliang = oldPt.getCorrCount();
							Float cy = yongliang / pt.getCorrCount();
							if (cy < 0.99 || cy > 1.0001) {
								changeCount++;
								if (changetypesb.length() == 0) {
									changetypesb.append("用量");
								} else {
									changetypesb.append("、" + "用量");
								}
								if (changeCount > 1) {
									changeDetailsb.append("\n");
								}
								changeDetailsb.append(changeCount + "、原用量："
										+ yongliang + ",新用量："
										+ pt.getCorrCount() + "。");
							}
						}
					}
					int nowj = j;
					// 比对下层
					Set<ProcardTemplate> oldsonSet = oldPt.getProcardTSet();
					List<String> sonMarkIdList = new ArrayList<String>();
					boolean needReturn = false;
					if (ptList.size() > (j + 1)) {
						for (int k = (j + 1); k < ptList.size(); k++) {
							ProcardTemplate son = ptList.get(k);
							if (pt.getBelongLayer().equals(
									(son.getBelongLayer() - 1))) {// 下一层
								sonMarkIdList.add(son.getMarkId());
								boolean had = false;
								for (ProcardTemplate oldSon : oldsonSet) {
									if (oldSon.getDataStatus() == null
											|| !oldSon.getDataStatus().equals(
													"删除")) {
										if (oldSon.getMarkId().equals(
												son.getMarkId())) {
											comparept(ws, wc, ptList, k,
													oldSon, exclebase,
													passwgTypeList);
											had = true;
										}
									}
								}
								if (!had) {// 下阶层新增物料
									if (hasbd.contains(k)) {
										continue;
									} else {
										hasbd.add(k);
									}
									setExcleMsg(k, ws, wc, son, 1, "新增", "",
											exclebase);
								}
							} else if (pt.getBelongLayer() < (son
									.getBelongLayer() - 1)) {// 下下阶层
							} else {
								needReturn = true;
								break;
							}
						}
					}
					for (ProcardTemplate oldSon : oldsonSet) {
						if (oldSon.getDataStatus() == null
								|| !oldSon.getDataStatus().equals("删除")) {
							if (oldSon.getProcardStyle().equals("外购")
									&& oldSon.getWgType() != null
									&& passwgTypeList.contains(oldSon
											.getWgType())) {
								// 不比对
								continue;
							}
							if (!sonMarkIdList.contains(oldSon.getMarkId())) {
								changeCount++;
								if (changetypesb.indexOf("删除子件") < 0) {
									if (changetypesb.length() == 0) {
										changetypesb.append("删除子件");
									} else {
										changetypesb.append("、" + "删除子件");
									}
								}
								if (changeCount > 1) {
									changeDetailsb.append("\n");
								}
								changeDetailsb.append(changeCount
										+ "、删除子件:"
										+ oldSon.getMarkId()
										// + ",名称："+ oldSon.getProName()
										+ ",版本：" + oldSon.getBanBenNumber()
										+ ",零件类型:" + oldSon.getProductStyle()
										+ "。");
							}
						}
					}
					if (changeCount > 0) {
						setExcleMsg(nowj, ws, wc, pt, 1, changetypesb
								.toString(), changeDetailsb.toString(),
								exclebase);
					} else {
						setExcleMsg(nowj, ws, wc, pt, 0, "", "", exclebase);
					}
					if (needReturn) {
						return;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	public String setExcleMsg(int index, WritableSheet ws,
			WritableCellFormat wc, ProcardTemplate pt, int type,
			String changetype, String changeDetail, int exclebase)
			throws WriteException {
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
				WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
				Colour.BLACK);
		WritableCellFormat wc2 = null;
		if (type == 1) {
			wc2 = new WritableCellFormat(wf);
			wc2.setBackground(jxl.format.Colour.YELLOW);
		} else {
			wc2 = wc;
		}
		ws.addCell(new jxl.write.Label(0, exclebase + index, (index + 1) + "",
				wc2));
		ws.addCell(new jxl.write.Label(1, exclebase + index, pt.getT1(), wc2));
		ws.addCell(new jxl.write.Label(2, exclebase + index, pt.getT2(), wc2));
		ws.addCell(new jxl.write.Label(3, exclebase + index, pt.getT3(), wc2));
		ws.addCell(new jxl.write.Label(4, exclebase + index, pt
				.getBanBenNumber(), wc2));
		ws.addCell(new jxl.write.Label(5, exclebase + index, pt.getProName(),
				wc2));
		for (int jc = 6; jc < 16; jc++) {
			ws.addCell(new jxl.write.Label(jc, exclebase + index, "", wc2));
		}
		ws.addCell(new jxl.write.Label(5 + pt.getBelongLayer(), exclebase
				+ index, pt.getCorrCount() + "", wc2));
		ws.addCell(new jxl.write.Label(16, exclebase + index, pt
				.getProcardStyle(), wc2));
		ws.addCell(new jxl.write.Label(17, exclebase + index, pt.getCorrCount()
				+ "", wc2));
		ws
				.addCell(new jxl.write.Label(18, exclebase + index, pt
						.getUnit(), wc2));
		ws.addCell(new jxl.write.Label(19, exclebase + index, pt.getBiaochu(),
				wc2));
		ws.addCell(new jxl.write.Label(20, exclebase + index, pt.getCaizhi(),
				wc2));
		ws.addCell(new jxl.write.Label(21, exclebase + index, pt
				.getThisLength()
				+ "", wc2));
		ws.addCell(new jxl.write.Label(22, exclebase + index, pt.getThisWidth()
				+ "", wc2));
		ws.addCell(new jxl.write.Label(23, exclebase + index, pt.getThisHight()
				+ "", wc2));
		ws.addCell(new jxl.write.Label(24, exclebase + index, pt.getRemark(),
				wc2));
		ws.addCell(new jxl.write.Label(25, exclebase + index, pt.getWgType(),
				wc2));
		ws.addCell(new jxl.write.Label(26, exclebase + index, pt
				.getSpecification(), wc2));
		ws.addCell(new jxl.write.Label(27, exclebase + index, "", wc2));
		ws.addCell(new jxl.write.Label(28, exclebase + index, changetype, wc2));
		ws
				.addCell(new jxl.write.Label(29, exclebase + index,
						changeDetail, wc2));
		return "true";
	}

	@Override
	public String addtclog(TechnicalchangeLog technicalchangeLog,
			int[] checkboxs) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String nowTime = Util.getDateTime();
		Users fqr = (Users) totalDao.getObjectById(Users.class,
				technicalchangeLog.getApplyUserId());
		technicalchangeLog.setAddTime(nowTime);
		technicalchangeLog.setAddUserId(user.getId());// 添加人Id
		technicalchangeLog.setAddUserName(user.getName());//
		technicalchangeLog.setAddUserCode(user.getCode());//
		technicalchangeLog.setApplyUserId(fqr.getId());// 发起人ID
		technicalchangeLog.setApplyUserName(fqr.getName());//
		technicalchangeLog.setApplyUserCode(fqr.getCode());//
		technicalchangeLog.setGcb("no");
		technicalchangeLog.setScb("no");
		technicalchangeLog.setPzb("no");
		technicalchangeLog.setPmcwk("no");
		technicalchangeLog.setPmcsg("no");
		technicalchangeLog.setCgwx("no");
		technicalchangeLog.setCgwg("no");
		technicalchangeLog.setCk("no");
		if (checkboxs != null && checkboxs.length > 0) {
			for (int vaule : checkboxs) {
				if (vaule == 1) {
					technicalchangeLog.setGcb("yes");
				}
				if (vaule == 2) {
					technicalchangeLog.setScb("yes");
				}
				if (vaule == 3) {
					technicalchangeLog.setPzb("yes");
				}
				if (vaule == 4) {
					technicalchangeLog.setPmcwk("yes");
				}
				if (vaule == 5) {
					technicalchangeLog.setPmcsg("yes");
				}
				if (vaule == 6) {
					technicalchangeLog.setCgwx("yes");
				}
				if (vaule == 7) {
					technicalchangeLog.setCgwg("yes");
				}
				if (vaule == 8) {
					technicalchangeLog.setCk("yes");
				}
			}
		}
		if (technicalchangeLog.getTclDetailList() != null
				&& technicalchangeLog.getTclDetailList().size() > 0) {
			Set<TechnicalchangeLogDetail> tcdlSet = new HashSet<TechnicalchangeLogDetail>();
			for (TechnicalchangeLogDetail tcld : technicalchangeLog
					.getTclDetailList()) {
				if (tcld != null && tcld.getMarkId() != null
						&& tcld.getMarkId().length() > 0) {
					tcld.setTechnicalchangeLog(technicalchangeLog);
					tcdlSet.add(tcld);
				}
			}
			technicalchangeLog.setTclDetailSet(tcdlSet);
		}

		return totalDao.save(technicalchangeLog) + "";
	}

	@Override
	public Map<Integer, Object> findTechnicalchangeLogByCondition(
			TechnicalchangeLog technicalchangeLog, int pageNo, int pageSize,
			String start, String end, String tag) {
		// TODO Auto-generated method stub
		if (technicalchangeLog == null) {
			technicalchangeLog = new TechnicalchangeLog();
		}
		String hql = totalDao.criteriaQueries(technicalchangeLog, null, null);
		if (start != null && start.length() > 0) {
			hql += " and recriveDate >='" + start + "'";
		}
		if (end != null && end.length() > 0) {
			hql += " and recriveDate <='" + end + "'";
		}
		if (tag.equals("in")) {
			hql += " and (outbsNumber is null or outbsNumber='' or outbsNumber='/')";
		} else if (tag.equals("out")) {
			hql += " and outbsNumber is not null and outbsNumber!='' and outbsNumber!='/'";
		}
		int count = totalDao.getCount(hql);
		List<TechnicalchangeLog> tclList = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		if (tclList != null && tclList.size() > 0) {
			for (TechnicalchangeLog tcl : tclList) {
				List<TechnicalchangeLogDetail> tcldList = new ArrayList<TechnicalchangeLogDetail>();
				Set<TechnicalchangeLogDetail> taclSet = tcl.getTclDetailSet();
				if (taclSet != null && taclSet.size() > 0) {
					for (TechnicalchangeLogDetail tcld : taclSet) {
						tcldList.add(tcld);
					}
				}
				tcl.setTclDetailList(tcldList);
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, tclList);
		map.put(2, count);
		return map;
	}

	@Override
	public TechnicalchangeLog gettclById(Integer id) {
		// TODO Auto-generated method stub
		return (TechnicalchangeLog) totalDao.getObjectById(
				TechnicalchangeLog.class, id);
	}

	@Override
	public TechnicalchangeLog gettclById2(Integer id) {
		// TODO Auto-generated method stub
		TechnicalchangeLog tcl = (TechnicalchangeLog) totalDao.getObjectById(
				TechnicalchangeLog.class, id);
		if (tcl != null) {
			List<TechnicalchangeLogDetail> tcldList = new ArrayList<TechnicalchangeLogDetail>();
			Set<TechnicalchangeLogDetail> taclSet = tcl.getTclDetailSet();
			if (taclSet != null && taclSet.size() > 0) {
				for (TechnicalchangeLogDetail tcld : taclSet) {
					tcldList.add(tcld);
				}
			}
			tcl.setTclDetailList(tcldList);
		}
		return tcl;
	}

	@Override
	public String updatetclog(TechnicalchangeLog technicalchangeLog,
			int[] checkboxs) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		TechnicalchangeLog old = (TechnicalchangeLog) totalDao.getObjectById(
				TechnicalchangeLog.class, technicalchangeLog.getId());
		if (old == null) {
			return "没有找到目标";
		}
		String nowTime = Util.getDateTime();
		technicalchangeLog.setAddTime(nowTime);
		technicalchangeLog.setAddUserId(user.getId());// 添加人Id
		technicalchangeLog.setAddUserName(user.getName());//
		technicalchangeLog.setAddUserCode(user.getCode());//
		technicalchangeLog.setGcb("no");
		technicalchangeLog.setScb("no");
		technicalchangeLog.setPzb("no");
		technicalchangeLog.setPmcwk("no");
		technicalchangeLog.setPmcsg("no");
		technicalchangeLog.setCgwx("no");
		technicalchangeLog.setCgwg("no");
		technicalchangeLog.setCk("no");
		if (checkboxs != null && checkboxs.length > 0) {
			for (int vaule : checkboxs) {
				if (vaule == 1) {
					technicalchangeLog.setGcb("yes");
				}
				if (vaule == 2) {
					technicalchangeLog.setScb("yes");
				}
				if (vaule == 3) {
					technicalchangeLog.setPzb("yes");
				}
				if (vaule == 4) {
					technicalchangeLog.setPmcwk("yes");
				}
				if (vaule == 5) {
					technicalchangeLog.setPmcsg("yes");
				}
				if (vaule == 6) {
					technicalchangeLog.setCgwx("yes");
				}
				if (vaule == 7) {
					technicalchangeLog.setCgwg("yes");
				}
				if (vaule == 8) {
					technicalchangeLog.setCk("yes");
				}
			}
		}
		BeanUtils.copyProperties(technicalchangeLog, old, new String[] { "id",
				"applyUserId", "applyUserName", "applyUserCode",
				"tclDetailSet", "addUserId", "addUserName", "addUserCode" });
		List<TechnicalchangeLogDetail> tcldList = technicalchangeLog
				.getTclDetailList();
		Set<TechnicalchangeLogDetail> oldtcldSet = old.getTclDetailSet();
		if (tcldList == null || tcldList.size() == 0) {
			if (oldtcldSet != null && oldtcldSet.size() > 0) {
				for (TechnicalchangeLogDetail oldtcld : oldtcldSet) {
					oldtcld.setTechnicalchangeLog(null);
					totalDao.delete(oldtcld);
				}
				old.setTclDetailSet(null);
			}
		} else {
			if (oldtcldSet == null) {
				oldtcldSet = new HashSet<TechnicalchangeLogDetail>();
			}
			Set<TechnicalchangeLogDetail> deleteSet = new HashSet<TechnicalchangeLogDetail>();
			for (TechnicalchangeLogDetail oldtcld : oldtcldSet) {
				boolean had = false;
				for (TechnicalchangeLogDetail tcld : tcldList) {
					if (tcld != null && tcld.getMarkId() != null
							&& tcld.getMarkId().length() > 0) {
						if (tcld.getId() != null
								&& oldtcld.getId().equals(tcld.getId())) {
							had = true;
							break;
						}
					}
				}
				if (!had) {
					oldtcld.setTechnicalchangeLog(null);
					totalDao.delete(oldtcld);
					deleteSet.add(oldtcld);
				}
			}
			oldtcldSet.removeAll(deleteSet);

			for (TechnicalchangeLogDetail tcld : tcldList) {
				if (tcld != null && tcld.getMarkId() != null
						&& tcld.getMarkId().length() > 0) {
					if (tcld.getId() != null) {
						for (TechnicalchangeLogDetail oldtcld : oldtcldSet) {
							if (oldtcld.getId().equals(tcld.getId())) {
								oldtcld.setMarkId(tcld.getMarkId());
								oldtcld.setChangeLog(tcld.getChangeLog());
								totalDao.update(oldtcld);
							}
						}
					} else {
						tcld.setTechnicalchangeLog(old);
						oldtcldSet.add(tcld);
					}
				}
			}
			old.setTclDetailSet(oldtcldSet);
		}
		if (!Util.isEquals(old.getApplyUserId(), technicalchangeLog
				.getApplyUserId())) {
			Users fqr = (Users) totalDao.getObjectById(Users.class,
					technicalchangeLog.getApplyUserId());
			old.setApplyUserId(fqr.getId());// 发起人ID
			old.setApplyUserName(fqr.getName());//
			old.setApplyUserCode(fqr.getCode());//
		}
		return totalDao.update(old) + "";
	}

	@Override
	public String detetetcl(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		TechnicalchangeLog old = (TechnicalchangeLog) totalDao.getObjectById(
				TechnicalchangeLog.class, id);
		if (old == null) {
			return "没有找到目标";
		}
		return totalDao.delete(old) + "";
	}

	public void exprotBbAply_new(ProcardTemplateBanBenApply bbAply,
			String startDate, String endDate) {
		if (bbAply == null) {
			bbAply = new ProcardTemplateBanBenApply();
		}
		String hql = totalDao.criteriaQueries(bbAply, null);
		if (startDate != null && !"".equals(startDate)) {
			hql += " and applyTime >= '" + startDate + " 00:00:00'";
		}
		if (endDate != null && !"".equals(endDate)) {
			hql += " and applyTime <= '" + endDate + " 23:59:59'";
		}
		List<ProcardTemplateBanBenApply> bbAplyList = totalDao.query(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			String filename = "设变信息";
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(filename.getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet(filename, 0);
			ws.setColumnView(16, 30);
			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "作者"));
			ws.addCell(new Label(2, 0, "部门"));
			ws.addCell(new Label(3, 0, "产品编码"));
			ws.addCell(new Label(4, 0, "类别"));
			ws.addCell(new Label(5, 0, "设变单号"));
			ws.addCell(new Label(6, 0, "组别"));
			ws.addCell(new Label(7, 0, "产品类别"));
			ws.addCell(new Label(8, 0, "属性"));
			ws.addCell(new Label(9, 0, "图纸状态"));
			ws.addCell(new Label(10, 0, "ECO编号"));
			ws.addCell(new Label(11, 0, "流程状态"));
			ws.addCell(new Label(12, 0, "当前处理人"));
			ws.addCell(new Label(13, 0, "当前步骤停留"));
			ws.addCell(new Label(14, 0, "流程单号"));
			ws.addCell(new Label(15, 0, "备注"));
			ws.addCell(new Label(16, 0, "发起时间"));
			ws.addCell(new Label(17, 0, "设变来源"));
			ws.addCell(new Label(18, 0, "设变原因"));
			ws.addCell(new Label(19, 0, "佐证人员"));
			DecimalFormat df = new DecimalFormat("#.####");
			for (int i = 0; i < bbAplyList.size(); i++) {
				ProcardTemplateBanBenApply bbAply0 = bbAplyList.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, bbAply0.getApplicantName()));
				ws.addCell(new Label(2, i + 1, bbAply0.getApplicantdept()));
				ws.addCell(new Label(3, i + 1, bbAply0.getYwMarkId()));
				ws.addCell(new Label(4, i + 1, bbAply0.getSbType()));
				ws.addCell(new Label(5, i + 1, bbAply0.getOutbsNumber()));
				ws.addCell(new Label(6, i + 1, bbAply0.getAboutPlace()));
				ws.addCell(new Label(7, i + 1, ""));
				ws.addCell(new Label(8, i + 1, ""));
				ws.addCell(new Label(9, i + 1, ""));
				ws.addCell(new Label(10, i + 1, ""));
				ws.addCell(new Label(11, i + 1, bbAply0.getProcessStatus()));
				String hql_person = "select userName from ProcardTemplateBanBenJudges where ptbbApply.id = ?";
				if ("项目主管初评".equals(bbAply0.getProcessStatus())) {
					hql_person += " and judgeType ='初评'";
				} else if ("各部门评审".equals(bbAply0.getProcessStatus())) {
					hql_person += " and judgeType ='内审'";
				} else {
					hql_person += " and judgeType ='工程师'";
				}
				String person = (String) totalDao.getObjectByCondition(
						hql_person, bbAply0.getId());
				if (person != null && !"".equals(hql_person)) {
					ws.addCell(new Label(12, i + 1, person));
				} else {
					ws.addCell(new Label(12, i + 1, ""));
				}
				if (!"".equals(bbAply0.getLastop())) {
					ws.addCell(new Label(13, i + 1, bbAply0.getLastop() + "-"
							+ bbAply0.getLastUserName() + "-"
							+ bbAply0.getLastTime()));
				} else {
					ws.addCell(new Label(13, i + 1, ""));
				}
				ws.addCell(new Label(14, i + 1, bbAply0.getSbNumber()));
				ws.addCell(new Label(15, i + 1, bbAply0.getRemark()));
				ws.addCell(new Label(16, i + 1, bbAply0.getApplyTime()));
				if (!"".equals(bbAply0.getSbSource())) {
					ws.addCell(new Label(17, i + 1, bbAply0.getSbSource()));
				} else {
					ws.addCell(new Label(17, i + 1, ""));
				}
				if (!"".equals(bbAply0.getSbreason())) {
					ws.addCell(new Label(18, i + 1, bbAply0.getSbreason()));
				} else {
					ws.addCell(new Label(18, i + 1, ""));
				}
				if ("上传佐证".equals(bbAply0.getProcessStatus())) {
					String hql_persondept = "from ProcardTemplateBanBenJudges where ptbbApply.id = ? and judgeType ='佐证'";
					List<ProcardTemplateBanBenJudges> pl = totalDao.query(
							hql_persondept, bbAply0.getId());
					String cont = "";
					if (pl != null) {
						for (ProcardTemplateBanBenJudges p : pl) {
							cont += p.getUserName() + "(" + p.getDept() + ");";
						}
						ws.addCell(new Label(19, i + 1, cont));
					}
				} else {
					ws.addCell(new Label(19, i + 1, ""));
				}
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exprotBbAply(ProcardTemplateBanBenApply bbAply) {
		if (bbAply == null) {
			bbAply = new ProcardTemplateBanBenApply();
		}
		String hql = totalDao.criteriaQueries(bbAply, null);
		List<ProcardTemplateBanBenApply> bbAplyList = totalDao.query(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(StrutsStatics.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			String filename = "设变信息";
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(filename.getBytes("GB2312"), "8859_1")
					+ ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet(filename, 0);
			ws.setColumnView(16, 30);
			ws.addCell(new Label(0, 0, "序号"));
			ws.addCell(new Label(1, 0, "设变单号"));
			ws.addCell(new Label(2, 0, "外部设变单号"));
			ws.addCell(new Label(3, 0, "总成件号"));
			ws.addCell(new Label(4, 0, "业务件号"));
			ws.addCell(new Label(5, 0, "总成名称"));
			ws.addCell(new Label(6, 0, "版本"));
			ws.addCell(new Label(7, 0, "发起人"));
			ws.addCell(new Label(8, 0, "发起时间"));
			ws.addCell(new Label(9, 0, "进程"));
			ws.addCell(new Label(10, 0, "备注"));

			DecimalFormat df = new DecimalFormat("#.####");
			for (int i = 0; i <= bbAplyList.size(); i++) {
				ProcardTemplateBanBenApply bbAply0 = bbAplyList.get(i);
				ws.addCell(new Label(0, i + 1, i + 1 + ""));
				ws.addCell(new Label(1, i + 1, bbAply0.getSbNumber()));
				ws.addCell(new Label(2, i + 1, bbAply0.getOutbsNumber()));
				ws.addCell(new Label(3, i + 1, bbAply0.getMarkId()));
				ws.addCell(new Label(4, i + 1, bbAply0.getYwMarkId()));
				ws.addCell(new Label(5, i + 1, bbAply0.getProName()));
				ws.addCell(new Label(6, i + 1, bbAply0.getBanbenNumber()));
				ws.addCell(new Label(7, i + 1, bbAply0.getApplicantName()));
				ws.addCell(new Label(8, i + 1, bbAply0.getApplyTime()));
				ws.addCell(new Label(9, i + 1, bbAply0.getProcessStatus()));
				ws.addCell(new Label(10, i + 1, bbAply0.getRemark()));
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<Integer, Object> findPcnByCondition(ProcessChangeNotice pcn,
			int pageNo, int pageSize, String start, String end) {
		// TODO Auto-generated method stub
		if (pcn == null) {
			pcn = new ProcessChangeNotice();
		}
		String hql = totalDao.criteriaQueries(pcn, null, null);
		if (start != null && start.length() > 0) {
			hql += " and applyDate >='" + start + "'";
		}
		if (end != null && end.length() > 0) {
			hql += " and applyDate <='" + end + "'";
		}
		int count = totalDao.getCount(hql);
		List<ProcessChangeNotice> pcnList = totalDao.findAllByPage(hql, pageNo,
				pageSize);
		if (pcnList != null && pcnList.size() > 0) {
			for (ProcessChangeNotice pcn2 : pcnList) {
				List<ProcessChangeNoticeFile> pcnFileList = totalDao.query(
						"from ProcessChangeNoticeFile where pcnId=? ", pcn2
								.getId());
				pcn2.setFiles(pcnFileList);
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, pcnList);
		map.put(2, count);
		return map;
	}

	@Override
	public String addpcn(ProcessChangeNotice pcn,
			List<ProcessChangeNoticeFile> pcnFileList) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		pcn.setAddTime(Util.getDateTime());
		pcn.setAddUserId(user.getId());// 添加人Id
		pcn.setAddUserName(user.getName());//
		pcn.setAddUserCode(user.getCode());//
		if (pcn.getApplyUserId() != null) {
			Users tjr = (Users) totalDao.getObjectById(Users.class, pcn
					.getApplyUserId());
			pcn.setApplyUserId(tjr.getId());// 发起人ID
			pcn.setApplyUserName(tjr.getName());//
			pcn.setApplyUserCode(tjr.getCode());//
		}
		totalDao.save(pcn);
		if (pcnFileList != null && pcnFileList.size() > 0) {
			for (ProcessChangeNoticeFile pcnFile : pcnFileList) {
				pcnFile.setPcnId(pcn.getId());
				totalDao.save(pcnFile);
			}
		}
		return "true";
	}

	@Override
	public String detetepcn(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcessChangeNotice pcn = (ProcessChangeNotice) totalDao.getObjectById(
				ProcessChangeNotice.class, id);
		if (pcn == null) {
			return "您要删除的数据不存在!";
		}
		return totalDao.delete(pcn) + "";
	}

	@Override
	public ProcessChangeNotice getpcnById(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessChangeNotice) totalDao.getObjectById(
				ProcessChangeNotice.class, id);
	}

	@Override
	public String updatepcn(ProcessChangeNotice pcn) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcessChangeNotice old = (ProcessChangeNotice) totalDao.getObjectById(
				ProcessChangeNotice.class, pcn.getId());
		if (old == null) {
			return "您要修改的数据不存在!";
		}
		BeanUtils.copyProperties(pcn, old, new String[] { "id", "addUserId",
				"addUserName", "addUserCode", "addTime", "applyUserId",
				"applyUserName", "applyUserCode" });
		if (!Util.isEquals(old.getApplyUserId(), pcn.getApplyUserId())) {
			Users fqr = (Users) totalDao.getObjectById(Users.class, pcn
					.getApplyUserId());
			old.setApplyUserId(fqr.getId());// 发起人ID
			old.setApplyUserName(fqr.getName());//
			old.setApplyUserCode(fqr.getCode());//
		}
		return totalDao.update(old) + "";
	}

	@Override
	public List<ProcessChangeNoticeFile> findPcnfilesBypcnId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao.query("from ProcessChangeNoticeFile where pcnId=?", id);
	}

	@Override
	public String addpcnFile(Integer id,
			List<ProcessChangeNoticeFile> pcnFileList) {
		// TODO Auto-generated method stub
		if (pcnFileList != null && pcnFileList.size() > 0) {
			for (ProcessChangeNoticeFile pcnFile : pcnFileList) {
				pcnFile.setPcnId(id);
				totalDao.save(pcnFile);
			}
		}
		return "true";
	}

	@Override
	public String detetepcnFile(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcessChangeNoticeFile pcnfile = (ProcessChangeNoticeFile) totalDao
				.getObjectById(ProcessChangeNoticeFile.class, id);
		if (pcnfile == null) {
			return "您要删除的数据不存在!";
		}
		return totalDao.delete(pcnfile) + "";
	}

	@Override
	public ProcessChangeNotice getpcnByfileId(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessChangeNotice) totalDao
				.getObjectByCondition(
						"from ProcessChangeNotice where id in(select pcnId from ProcessChangeNoticeFile where id=?)",
						id);
	}

	@Override
	public ProcessChangeNoticeFile getpcnfileByfileId(Integer id) {
		// TODO Auto-generated method stub
		return (ProcessChangeNoticeFile) totalDao.getObjectById(
				ProcessChangeNoticeFile.class, id);
	}

	@Override
	public String addecar(DesignfeedbackNotice ecar,
			List<DesignfeedbackNoticeFile> ecarFileList) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ecar.setAddTime(Util.getDateTime());
		ecar.setAddUserId(user.getId());// 添加人Id
		ecar.setAddUserName(user.getName());//
		ecar.setAddUserCode(user.getCode());//
		if (ecar.getTjUserId() != null) {
			Users tjr = (Users) totalDao.getObjectById(Users.class, ecar
					.getTjUserId());
			ecar.setTjUserId(tjr.getId());// 发起人ID
			ecar.setTjUserName(tjr.getName());//
			ecar.setTjUsercode(tjr.getCode());//
		}
		totalDao.save(ecar);
		if (ecarFileList != null && ecarFileList.size() > 0) {
			for (DesignfeedbackNoticeFile ecarFile : ecarFileList) {
				ecarFile.setEcarId(ecar.getId());
				totalDao.save(ecarFile);
			}
		}
		return "true";
	}

	@Override
	public Map<Integer, Object> findEcarByCondition(DesignfeedbackNotice ecar,
			int pageNo, int pageSize, String start, String end) {
		// TODO Auto-generated method stub
		if (ecar == null) {
			ecar = new DesignfeedbackNotice();
		}
		String hql = totalDao.criteriaQueries(ecar, null, null);
		if (start != null && start.length() > 0) {
			hql += " and applyDate >='" + start + "'";
		}
		if (end != null && end.length() > 0) {
			hql += " and applyDate <='" + end + "'";
		}
		int count = totalDao.getCount(hql);
		List<DesignfeedbackNotice> ecarList = totalDao.findAllByPage(hql,
				pageNo, pageSize);
		if (ecarList != null && ecarList.size() > 0) {
			for (DesignfeedbackNotice ecar2 : ecarList) {
				List<DesignfeedbackNoticeFile> pcnFileList = totalDao.query(
						"from DesignfeedbackNoticeFile where ecarId=? ", ecar2
								.getId());
				ecar2.setFiles(pcnFileList);
			}
		}
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, ecarList);
		map.put(2, count);
		return map;
	}

	@Override
	public String addecarFile(Integer id,
			List<DesignfeedbackNoticeFile> ecarFileList) {
		// TODO Auto-generated method stub
		if (ecarFileList != null && ecarFileList.size() > 0) {
			for (DesignfeedbackNoticeFile ecarFile : ecarFileList) {
				ecarFile.setEcarId(id);
				totalDao.save(ecarFile);
			}
		}
		return "true";
	}

	@Override
	public String deteteecar(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		DesignfeedbackNotice ecar = (DesignfeedbackNotice) totalDao
				.getObjectById(DesignfeedbackNotice.class, id);
		if (ecar == null) {
			return "您要删除的数据不存在!";
		}
		return totalDao.delete(ecar) + "";
	}

	@Override
	public String deteteecarFile(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		DesignfeedbackNoticeFile ecarfile = (DesignfeedbackNoticeFile) totalDao
				.getObjectById(DesignfeedbackNoticeFile.class, id);
		if (ecarfile == null) {
			return "您要删除的数据不存在!";
		}
		return totalDao.delete(ecarfile) + "";
	}

	@Override
	public List<DesignfeedbackNoticeFile> findEcarfilesByecarId(Integer id) {
		// TODO Auto-generated method stub
		return totalDao.query("from DesignfeedbackNoticeFile where ecarId=?",
				id);
	}

	@Override
	public DesignfeedbackNotice getecarById(Integer id) {
		// TODO Auto-generated method stub
		return (DesignfeedbackNotice) totalDao.getObjectById(
				DesignfeedbackNotice.class, id);
	}

	@Override
	public DesignfeedbackNotice getecarByfileId(Integer id) {
		// TODO Auto-generated method stub
		return (DesignfeedbackNotice) totalDao
				.getObjectByCondition(
						" from DesignfeedbackNotice where id in(select ecarId from DesignfeedbackNoticeFile where id=?)",
						id);
	}

	@Override
	public DesignfeedbackNoticeFile getecarfileByfileId(Integer id) {
		// TODO Auto-generated method stub
		return (DesignfeedbackNoticeFile) totalDao.getObjectById(
				DesignfeedbackNoticeFile.class, id);
	}

	@Override
	public String updateecar(DesignfeedbackNotice ecar) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		DesignfeedbackNotice old = (DesignfeedbackNotice) totalDao
				.getObjectById(DesignfeedbackNotice.class, ecar.getId());
		if (old == null) {
			return "您要修改的数据不存在!";
		}
		BeanUtils.copyProperties(ecar, old, new String[] { "id", "tjUserId",
				"tjUsercode", "tjUserName", "addUserId", "addUserName",
				"addUserCode", "addTime" });
		if (!Util.isEquals(old.getTjUserId(), ecar.getTjUserId())) {
			Users tjr = (Users) totalDao.getObjectById(Users.class, ecar
					.getTjUserId());
			old.setTjUserId(tjr.getId());// 发起人ID
			old.setTjUserName(tjr.getName());//
			old.setTjUsercode(tjr.getCode());//
		}
		return totalDao.update(old) + "";
	}

	@Override
	public String updatepbbremark(ProcardTemplateBanBen ptbb) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBen old = (ProcardTemplateBanBen) totalDao
				.getObjectById(ProcardTemplateBanBen.class, ptbb.getId());
		if (old == null) {
			return "对不起,没有找到修改的目标!";
		}
		List<ProcardTemplateBanBenJudges> bbJudgesList = totalDao.query(
				"from ProcardTemplateBanBenJudges where ptbbApply.id=?", old
						.getProcardTemplateBanBenApply().getId());
		boolean canchange = false;
		if (bbJudgesList != null && bbJudgesList.size() > 0) {
			for (ProcardTemplateBanBenJudges bbj : bbJudgesList) {
				if (bbj.getJudgeType().equals("工程师")) {
					if (user.getId().equals(bbj.getUserId())) {
						canchange = true;
					}
					break;
				}
			}
		}
		if (!canchange) {
			return "对不起,您不是工程师没有修改权限!";
		}
		old.setRemark(ptbb.getRemark());
		if (totalDao.update(old)) {
			return "修改成功!";
		} else {
			return "修改失败!";
		}
	}

	@Override
	public List findwjaPcnByCondition(ProcessChangeNotice pcn, String start,
			String end) {
		// TODO Auto-generated method stub
		if (pcn == null) {
			pcn = new ProcessChangeNotice();
		}
		String hql = totalDao.criteriaQueries(pcn, null, null);
		if (start != null && start.length() > 0) {
			hql += " and applyDate >='" + start + "'";
		}
		if (end != null && end.length() > 0) {
			hql += " and applyDate <='" + end + "'";
		}
		int count = totalDao.getCount(hql);
		List<ProcessChangeNotice> pcnList = totalDao.query(hql);
		if (pcnList != null && pcnList.size() > 0) {
			for (ProcessChangeNotice pcn2 : pcnList) {
				List<ProcessChangeNoticeFile> pcnFileList = totalDao.query(
						"from ProcessChangeNoticeFile where pcnId=? ", pcn2
								.getId());
				pcn2.setFiles(pcnFileList);
			}
		}
		return pcnList;
	}

	@Override
	public List findEcarByCondition(DesignfeedbackNotice ecar, String start,
			String end) {
		// TODO Auto-generated method stub
		if (ecar == null) {
			ecar = new DesignfeedbackNotice();
		}
		String zxstatus = ecar.getZxstatus();
		if (zxstatus.equals("待处理")) {
			ecar.setZxstatus(null);
		}
		String hql = totalDao.criteriaQueries(ecar, null, null);
		if (start != null && start.length() > 0) {
			hql += " and applyDate >='" + start + "'";
		}
		if (end != null && end.length() > 0) {
			hql += " and applyDate <='" + end + "'";
		}
		if (zxstatus.equals("待处理")) {
			hql += " and zxstatus not in('已','已取消','已驳回')";
		}
		int count = totalDao.getCount(hql);
		List<DesignfeedbackNotice> ecarList = totalDao.query(hql);
		if (ecarList != null && ecarList.size() > 0) {
			for (DesignfeedbackNotice ecar2 : ecarList) {
				List<DesignfeedbackNoticeFile> pcnFileList = totalDao.query(
						"from DesignfeedbackNoticeFile where ecarId=? ", ecar2
								.getId());
				ecar2.setFiles(pcnFileList);
			}
		}
		return ecarList;
	}

	@Override
	public String ncAndleisheFile(String path, String path2) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		String month = Util.getDateTime("yyyy-MM");
		// 将文件放入特定位置
		File cfdz = new File(path2 + "/upload/file/processTz/" + month);// 存放图纸的文件夹
		if (!cfdz.exists()) {
			cfdz.mkdirs();// 如果不存在文件夹就创建
		}
		File file = null;
		file = new File(path);
		if (!file.exists()) {
			return "对不起没有找到对应的文件";
		}
		File[] sonList1 = file.listFiles();
		StringBuffer drmes = new StringBuffer();
		StringBuffer errMsgSb = new StringBuffer();
		if (sonList1 != null && sonList1.length > 0) {
			for (File son1 : sonList1) {
				if (son1.getName().equals("NC数冲")
						|| son1.getName().equals("镭射")) {
					sdnum = 0;// 上传成功文件数量
					File[] sonList2 = son1.listFiles();
					if (sonList2 != null && sonList2.length > 0) {
						for (File son2 : sonList2) {
							if (!son2.isDirectory() && son2.getName() != null) {
								String msg = null;
								try {
									msg = checkAndUpdateTz4(son2.getName(),
											son1.getName(), null, son2, cfdz,
											month, user);
									if (!msg.equals("true")) {
										errMsgSb.append("<br/>" + msg);
									}
								} catch (Exception e) {
									// TODO: handle exception
									errMsgSb.append("<br/>" + son2.getName()
											+ "文件上传时出现异常!");
								}

							}
						}
					}
					drmes.append("导入成功" + son1.getName() + "文件:" + sdnum
							+ "份<br/>");
					// return "导入成功！";
				}
			}
		}
		return drmes.toString() + errMsgSb.toString();
	}

	private String checkAndUpdateTz4(String fileName, String typeName,
			Object object, File son2, File cfdz, String month, Users user) {
		String markId = fileName.substring(0, fileName.lastIndexOf("."));
		if (typeName.equals("NC数冲") || typeName.equals("镭射")) {
			if (markId.startsWith("YT") || markId.startsWith("yt")) {
				markId = markId.substring(0, 3) + "." + markId.substring(3, 6)
						+ "." + markId.substring(6, markId.length());
			} else if (markId.startsWith("DKBA") || markId.startsWith("dkba")) {
				markId = markId.substring(0, 5) + "." + markId.substring(5, 8)
						+ "." + markId.substring(8, markId.length());
			}
		}
		ProcardTemplate pt = (ProcardTemplate) totalDao
				.getObjectByCondition(
						"from ProcardTemplate where productStyle='批产' and markId=? and (banbenStatus='默认' or banbenStatus is null) and (dataStatus is null or dataStatus!='删除')",
						markId);
		if (pt == null) {
			return typeName + "图纸:" + markId + "没有找到对应的零件!<br/>";
		} else {
			// if(pt.getBzStatus()!=null&&pt.getBzStatus().equals("已批准")){
			// return typeName + "图纸:" + fileName + "对应的零件为已批准状态,不能上传图纸!<br/>";
			// }
		}
		boolean b = true;
		String type = fileName.substring(fileName.lastIndexOf("."), fileName
				.length());
		// 覆盖之前存在的
		String sqlhadtz = "from ProcessTemplateFile where productStyle=? and type='"
				+ typeName + "'";
		if (pt.getBanci() == null || pt.getBanci() == 0) {
			sqlhadtz += " and (banci is null or banci =0)";
		} else {
			sqlhadtz += " and banci =" + pt.getBanci();
		}
		if (type.equalsIgnoreCase(".jpeg")) {
			String fileName1 = fileName.replaceAll(".jpeg", ".jpg");
			String fileName2 = fileName.replaceAll(".jpeg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".jpg")) {
			String fileName1 = fileName.replaceAll(".jpg", ".jpeg");
			String fileName2 = fileName.replaceAll(".jpg", ".PDF");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else if (type.equalsIgnoreCase(".PDF")) {
			fileName = fileName.replaceAll(".pdf", ".PDF");
			String fileName1 = fileName.replaceAll(".PDF", ".jpg");
			String fileName2 = fileName.replaceAll(".PDF", ".jpeg");
			sqlhadtz += " and (oldfileName='" + fileName1
					+ "' or oldfileName ='" + fileName + "' or oldfileName ='"
					+ fileName2 + "')";
		} else {
			sqlhadtz += " and oldfileName='" + fileName + "'";
		}
		List<ProcessTemplateFile> hadFileList = totalDao.query(sqlhadtz, pt
				.getProductStyle());
		if (hadFileList != null && hadFileList.size() > 0) {
			int logn = 0;
			for (ProcessTemplateFile hadfile : hadFileList) {
				if (logn == 0) {
					ProcardTemplateChangeLog.addchangeLog(totalDao, pt,
							hadfile, "图纸", "删除", user, Util.getDateTime());
				}
				logn++;
				totalDao.delete(hadfile);
			}
		}
		if (b) {
			Integer banci = pt.getBanci();
			if (banci == null) {
				banci = 0;
			}
			ProcessTemplateFile ptFile = new ProcessTemplateFile();
			ptFile.setProductStyle(pt.getProductStyle());
			ptFile.setBanBenNumber(pt.getBanBenNumber());
			ptFile.setBanci(banci);
			ptFile.setMarkId(markId);
			String realFileName = Util.getDateTime("yyyyMMddHHmmssSSS") + type;
			String realFileName2 = null;
			if (type.equalsIgnoreCase(".bmp") || type.equalsIgnoreCase(".dib")
					|| type.equalsIgnoreCase(".gif")
					|| type.equalsIgnoreCase(".jfif")
					|| type.equalsIgnoreCase(".jpe")
					|| type.equalsIgnoreCase(".jpeg")
					|| type.equalsIgnoreCase(".jpg")
					|| type.equalsIgnoreCase(".png")
					|| type.equalsIgnoreCase(".tif")
					|| type.equalsIgnoreCase(".tiff")
					|| type.equalsIgnoreCase(".ico")
					|| type.equalsIgnoreCase(".pdf")) {
				realFileName2 = "jz_" + realFileName;
			}

			ptFile.setFileName(realFileName);// 文件名
			ptFile.setFileName2(realFileName2);// 文件名
			ptFile.setOldfileName(fileName);// 文件原名称
			ptFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
			ptFile.setType(typeName);// 文件类型(视频文件，工艺规范)
			ptFile.setStatus("默认");// 默认,历史
			ptFile.setUserName(user.getName());// 上传人姓名
			ptFile.setUserCode(user.getCode());// 上传人工号
			ptFile.setAddTime(Util.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
			ptFile.setProcessName(pt.getProName());
			ptFile.setProcessNO(null);
			Upload upload = new Upload();
			String msg = upload.UploadFile(son2, fileName, realFileName,
					"/upload/file/processTz/" + month, null);
			if (realFileName2 != null) {
				if (type.equalsIgnoreCase(".bmp")
						|| type.equalsIgnoreCase(".dib")
						|| type.equalsIgnoreCase(".gif")
						|| type.equalsIgnoreCase(".jfif")
						|| type.equalsIgnoreCase(".jpe")
						|| type.equalsIgnoreCase(".jpeg")
						|| type.equalsIgnoreCase(".jpg")
						|| type.equalsIgnoreCase(".png")
						|| type.equalsIgnoreCase(".tif")
						|| type.equalsIgnoreCase(".tiff")
						|| type.equalsIgnoreCase(".ico")) {
					// 将图纸加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_ytwrq.png");
					// 生成加章文件
					Util.markImageByIcon(icon_fileRealPath, cfdz + "/"
							+ realFileName, cfdz + "/" + realFileName2);
				} else if (type.equalsIgnoreCase(".pdf")) {
					// 将PDF加盖印章
					String icon_fileRealPath = ServletActionContext
							.getServletContext().getRealPath(
									"/upload/file/yz/icon_sk.png");
					// 生成加章文件
					try {
						Util.markPDFByIcon(icon_fileRealPath, cfdz + "/"
								+ realFileName, cfdz + "/" + realFileName2);
					} catch (Exception e) {
						// TODO: handle exception
						throw new RuntimeException(fileName + "文件有问题!");
					}
				}
				if (pt.getBzStatus().equals("已批准")) {// 直接加章
					ptFile.setFileName(realFileName2);// 文件名
					ptFile.setFileName2(realFileName);// 文件名
				}
			}
			totalDao.save(ptFile);
			ProcardTemplateChangeLog.addchangeLog(totalDao, pt, ptFile, "图纸",
					"添加", user, Util.getDateTime());
			Set<ProcessTemplate> processSet = pt.getProcessTemplate();
			if (processSet != null && processSet.size() > 0) {
				for (ProcessTemplate process : processSet) {
					if (process.getDataStatus() != null
							&& process.getDataStatus().equals("删除")) {
						continue;
					}
					if ((typeName.equals("NC数冲") && process.getProcessName()
							.contains("NC"))
							|| (typeName.equals("镭射") && process
									.getProcessName().contains("镭射"))) {
						ProcessTemplateFile processFile = new ProcessTemplateFile();
						processFile.setBanBenNumber(pt.getBanBenNumber());
						processFile.setBanci(banci);
						processFile.setProductStyle(pt.getProductStyle());
						processFile.setMarkId(markId);
						processFile.setFileName(ptFile.getFileName());// 文件名
						processFile.setFileName2(ptFile.getFileName2());// 文件名
						processFile.setOldfileName(fileName);// 文件原名称
						processFile.setMonth(month);// 上传月份(上传文件夹以月份命名（yyyy-MM）)
						processFile.setType(typeName);// 文件类型(视频文件，工艺规范)
						processFile.setStatus("默认");// 默认,历史
						processFile.setUserName(user.getName());// 上传人姓名
						processFile.setUserCode(user.getCode());// 上传人工号
						processFile.setAddTime(Util
								.getDateTime("yyyy-MM-dd HH:mm:ss"));// 上传时间
						processFile.setProcessName(process.getProcessName());
						processFile.setProcessNO(process.getProcessNO());
						totalDao.save(processFile);
					}
				}
			}
			sdnum++;
		}
		return b + "";
	}

	@Override
	public String dahuisb(Integer id, String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply pbba = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (pbba == null) {
			return "打回失败,没有找到设变单!";
		} else {
			pbba.setRemark(remark);
			pbba.setProcessStatus("分发项目组");
			pbba.setLastop("打回");
			pbba.setLastUserId(user.getId());
			pbba.setLastUserName(user.getName());
			pbba.setLastTime(Util.getDateTime());
			// if(pbba.getProcessStatus().equals("项目主管初评")
			// ||pbba.getProcessStatus().equals("项目工程师评审")){
			// }else{
			// return "当前设变单进度为:"+pbba.getProcessStatus()+"不允许强行";
			// }
			return totalDao.update(pbba) + "";
		}
	}

	@Override
	public String closeclosenoremark(Integer id) {// 设变完成之后
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply pbba = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (pbba == null) {
			return "失败,没有找到设变单!";
		} else {
			if (pbba.getProcessStatus().equals("上传佐证")
					|| pbba.getProcessStatus().equals("生产后续")) {
				// 华为反馈接口
				String mes = "true";
				if (pbba.getWorkItemId() != null) {
					String hql = "from ProcardTemplateBanBenApply where workItemId=? and id<>? and processStatus<>'关闭'";
					int count = totalDao.getCount(hql, pbba.getWorkItemId(),
							pbba.getId());
					if (count <= 0) {
						mes = hwEcTask(pbba.getWorkItemId(), pbba.getEcType(),
								"已执行");
					}
				}
				if ("true".equals(mes)) {
					pbba.setExecute("已执行");
					pbba.setProcessStatus("关闭");
					pbba.setColseUserId(user.getId());
					pbba.setColseUserName(user.getName());
					pbba.setColseTime(Util.getDateTime());
					pbba.setLastop("关闭");
					pbba.setLastUserId(user.getId());
					pbba.setLastUserName(user.getName());
					pbba.setLastTime(Util.getDateTime());
					return totalDao.update(pbba) + "";
				} else {
					return "关闭失败,因反馈Ec系统失败，失败原因:" + mes;
				}
			} else {
				return "当前设变单进度为:" + pbba.getProcessStatus() + "不允许强行关闭";
			}
		}
	}

	@Override
	public String closeSb(Integer id, String execute) {// 提前关闭
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply pbba = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (pbba == null) {
			return "关闭失败,没有找到设变单!";
		} else {
			if (pbba.getProcessStatus().equals("分发项目组")) {
				// 华为反馈接口
				String mes = "true";
				String more = "";
				if (execute != null && execute.length() > 0) {
					int ind = execute.indexOf(",");
					if (ind > 0) {
						more = execute.substring(ind + 1, execute.length());
						execute = execute.substring(0, ind);
					}
					if (pbba.getWorkItemId() != null) {
						String hql = "from ProcardTemplateBanBenApply where workItemId=? and id<>? and processStatus<>'关闭'";
						int count = totalDao.getCount(hql,
								pbba.getWorkItemId(), pbba.getId());
						if (count <= 0) {
							mes = hwEcTask(pbba.getWorkItemId(), pbba
									.getEcType(), execute);
						}
					}
				}
				if ("true".equals(mes)) {
					pbba.setRemark(pbba.getRemark() + more);
					pbba.setExecute(execute);
					pbba.setProcessStatus("关闭");
					pbba.setColseUserId(user.getId());
					pbba.setColseUserName(user.getName());
					pbba.setColseTime(Util.getDateTime());
					pbba.setLastop("关闭");
					pbba.setLastUserId(user.getId());
					pbba.setLastUserName(user.getName());
					pbba.setLastTime(Util.getDateTime());
					return totalDao.update(pbba) + "";
				} else {
					return "关闭失败,因反馈Ec系统失败，失败原因:" + mes;
				}
			} else {
				return "当前设变单进度为:" + pbba.getProcessStatus() + "不允许强行关闭";
			}
		}
	}

	public String hwEcTask(Long workItemId, String ecType, String execute) {
		AutoFeedBackService service = new AutoFeedBackService("Z000TR_PM",
				"ZLL1234.");
		// String execute = "";// 执行情况(已执行/不执行/不涉及)
		String refuseReason = "不涉及我司编码";// 未执行原因
		if ("已执行".equals(execute)) {
			refuseReason = "已执行";
		}

		SubmitECFeedBack submit = new SubmitECFeedBack();
		// 设置反馈单内容,根据实际内容设置,这只是样例
		submit.setWorkItemId(workItemId);// 任务唯一标识符
		submit.setEcType(ecType);// EC申请单类型
		submit.setRefuseReason(refuseReason);// 不涉及的时候必须填写原因
		// |NotInvovled(不涉及)]
		if ("MDADocAutoChange".equals(submit.getEcType())) {
			/****
			 * 非结构
			 */
			String mdamakedoc = "是";
			if ("已执行".equals(execute)) {
				submit.setExecute("Executed");// 是否执行EC[Executed(已执行)/NotInvovled(不涉及)]
			} else {
				mdamakedoc = "不涉及";
				submit.setExecute("NOTINVOLVED");// 是否执行EC[Executed(已执行)/NotInvovled(不涉及)]
			}
			/*****
			 * 处理结果:1.是 2.否:原因:xxxx 3.不涉及
			 */
			submit.setMdamakedoc(mdamakedoc);// 工艺文档(含展开图,BOM,QC,SOP,SIP)是否优化完成
			submit.setMdaequip("不涉及");// 工装检具模具是否优化完成
			submit.setMdasupplier("不涉及");// 二级供应商是否同步完成
			submit.setMdarelationNumber("不涉及");// 关联编码是否已排查完成
			submit.setStackComments("不涉及");// 库存(半成品,成品库存)处理情况
		} else {
			String cutInTime = null;
			if ("已执行".equals(execute)) {
				submit.setExecute("Executed");// 是否执行EC[Executed(已执行)/NOExecuted(不执行)/NOTINVOLVED(不涉及)]
				cutInTime = Util.getDateTime("yyyy-MM-dd");
			} else if ("不执行".equals(execute)) {
				submit.setExecute("NOExecuted");// 是否执行EC[Executed(已执行)/NOExecuted(不执行)/NOTINVOLVED(不涉及)]
			} else {
				submit.setExecute("NOTINVOLVED");// 是否执行EC[Executed(已执行)/NOExecuted(不执行)/NOTINVOLVED(不涉及)]
			}
			/****
			 * 结构
			 */
			submit.setMaterialsPurchasedWO("");// 采购材料执行数量或者任务命令
			submit.setSemiProductsWO("");// 半成品执行数量或者任务命令
			submit.setSystemProductsWO("");// 整机执行数量或任务命令
			submit.setCutInTime(cutInTime);// 切入时间(年-月-日,2016-01-20)
			submit.setMdamakedoc(null);// 工艺文档(含展开图,BOM,QC,SOP,SIP)是否优化完成
			submit.setMdaequip(null);// 工装检具模具是否优化完成
			submit.setMdasupplier(null);// 二级供应商是否同步完成
			submit.setMdarelationNumber(null);// 关联编码是否已排查完成
			submit.setStackComments(null);// 库存(半成品,成品库存)处理情况
		}

		ResponseInfoBean<SubmitECFeedBack> resp;
		try {
			resp = service.submitECFeedBack(Arrays.asList(submit));
			if (!resp.getSuccess()) {// 只用处理不成功情况
				// String msg = resp.getException();// 获取异常信息
				// System.out.println(msg);
				List<SubmitECFeedBack> submitlist = resp.getRecords();// 如果异常了,会将检查结果写到SubmitECFeedBack的checkResult字段
				for (SubmitECFeedBack submit1 : submitlist) {
					// System.out.println(submit1.getCheckResult());//
					// 获取检查结果,具体异常字段
					return submit1.getCheckResult();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return true + "";

	}

	@Override
	public String closeSb2(Integer id, String remark) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply pbba = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (pbba == null) {
			return "关闭失败,没有找到设变单!";
		} else {
			if (pbba.getProcessStatus().equals("分发项目组")) {
				pbba.setRemark(remark);
				pbba.setOldProcessStatus(pbba.getProcessStatus());
				pbba.setProcessStatus("关闭");
				pbba.setColseUserId(user.getId());
				pbba.setColseUserName(user.getName());
				pbba.setColseTime(Util.getDateTime());
				pbba.setLastop("关闭");
				pbba.setLastUserId(user.getId());
				pbba.setLastUserName(user.getName());
				pbba.setLastTime(Util.getDateTime());
				return totalDao.update(pbba) + "";
			} else if (pbba.getProcessStatus().equals("上传佐证")
					|| pbba.getProcessStatus().equals("生产后续")) {
				pbba.setRemark(remark);
				// 华为反馈接口
				String mes = "true";
				if (pbba.getWorkItemId() != null) {
					String hql = "from ProcardTemplateBanBenApply where workItemId=? and id<>? and processStatus<>'关闭'";
					int count = totalDao.getCount(hql, pbba.getWorkItemId(),
							pbba.getId());
					if (count <= 0) {
						mes = hwEcTask(pbba.getWorkItemId(), pbba.getEcType(),
								"已执行");
					}
				}
				if ("true".equals(mes)) {
					pbba.setExecute("已执行");
					pbba.setProcessStatus("关闭");
					pbba.setColseUserId(user.getId());
					pbba.setColseUserName(user.getName());
					pbba.setColseTime(Util.getDateTime());
					pbba.setLastop("关闭");
					pbba.setLastUserId(user.getId());
					pbba.setLastUserName(user.getName());
					pbba.setLastTime(Util.getDateTime());
					return totalDao.update(pbba) + "";
				} else {
					return "关闭失败,因反馈Ec系统失败，失败原因:" + mes;
				}
			} else {
				return "当前设变单进度为:" + pbba.getProcessStatus() + "不允许强行关闭";
			}
		}
	}

	@Override
	public ProcardTemplateBanBenApply getProcardTemplateBanbenApplyById(
			Integer id) {
		// TODO Auto-generated method stub
		return (ProcardTemplateBanBenApply) totalDao.getObjectById(
				ProcardTemplateBanBenApply.class, id);
	}

	@Override
	public void exportProcessExcel(Integer id) {
		if (id != null) {
			List<ProcessTemplate> processList = totalDao
					.query(
							" from ProcessTemplate where fk_pricessTId in (select id from  ProcardTemplate where"
									+ " rootId = ? and (dataStatus is null or dataStatus <> '删除') and (banbenStatus is null or banbenStatus !='历史'))  "
									+ " and (dataStatus is null or dataStatus <> '删除')",
							id);
			if (processList != null && processList.size() > 0) {
				try {
					HttpServletResponse response = (HttpServletResponse) ActionContext
							.getContext().get(
									ServletActionContext.HTTP_RESPONSE);
					ProcardTemplate totalCard = (ProcardTemplate) totalDao
							.getObjectById(ProcardTemplate.class, id);
					OutputStream os = response.getOutputStream();
					response.reset();
					String name = totalCard.getYwMarkId();
					if (name == null || name.length() == 0) {
						name = totalCard.getMarkId();
					}
					response.setHeader("Content-disposition",
							"attachment; filename="
									+ new String(name.getBytes("GB2312"),
											"8859_1") + ".xls");
					response.setContentType("application/msexcel");
					WritableWorkbook wwb = Workbook.createWorkbook(os);
					WritableSheet ws = wwb.createSheet("Bom明细", 0);
					ws.setColumnView(4, 20);
					ws.setColumnView(3, 10);
					ws.setColumnView(2, 20);
					ws.setColumnView(1, 12);
					WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
							WritableFont.NO_BOLD, false,
							UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
					WritableCellFormat wc = new WritableCellFormat(wf);
					wc.setAlignment(Alignment.LEFT);
					ws.addCell(new jxl.write.Label(0, 0, "序号", wc));
					ws.addCell(new jxl.write.Label(1, 0, "产品编码", wc));
					ws.addCell(new jxl.write.Label(2, 0, "件号", wc));
					ws.addCell(new jxl.write.Label(3, 0, "产品名称", wc));
					ws.addCell(new jxl.write.Label(4, 0, "工序号", wc));
					ws.addCell(new jxl.write.Label(5, 0, "工序名", wc));
					ws.addCell(new jxl.write.Label(6, 0, "整机用量", wc));
					ws.addCell(new jxl.write.Label(7, 0, "工序点数", wc));
					ws.addCell(new jxl.write.Label(8, 0, "计件单价", wc));
					for (int i = 0; i < processList.size(); i++) {
						ProcessTemplate processT = processList.get(i);
						ProcardTemplate procardT = processT
								.getProcardTemplate();
						Float yongliang = getcgNum(procardT, 1f, procardT
								.getBelongLayer());
						ws.addCell(new jxl.write.Label(0, i + 1, (i + 1) + "",
								wc));
						ws.addCell(new jxl.write.Label(1, i + 1, procardT
								.getYwMarkId(), wc));
						ws.addCell(new jxl.write.Label(2, i + 1, procardT
								.getMarkId(), wc));
						ws.addCell(new jxl.write.Label(3, i + 1, procardT
								.getProName(), wc));
						ws.addCell(new jxl.write.Label(4, i + 1, processT
								.getProcessNO()
								+ "", wc));
						ws.addCell(new jxl.write.Label(5, i + 1, processT
								.getProcessName(), wc));
						ws
								.addCell(new jxl.write.Number(6, i + 1,
										yongliang, wc));
						ws.addCell(new jxl.write.Number(7, i + 1, processT
								.getProcesdianshu() == null ? 0 : processT
								.getProcesdianshu(), wc));
						ws.addCell(new jxl.write.Number(8, i + 1, processT
								.getProcessjjMoney() == null ? 0 : processT
								.getProcessjjMoney(), wc));
					}
					wwb.write();
					wwb.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private Float getcgNum(ProcardTemplate sonProcardT, Float num,
			Integer belongLayer) {
		Float cgNum = 0f;
		if ("外购".equals(sonProcardT.getProcardStyle())) {
			cgNum = (sonProcardT.getQuanzi2() / sonProcardT.getQuanzi1()) * num;
		} else {
			Float corrCount = sonProcardT.getCorrCount();
			if (corrCount == null) {
				corrCount = 1f;
			}
			cgNum = corrCount * num;
		}
		if (sonProcardT.getFatherId() != null && sonProcardT.getFatherId() != 0) {
			ProcardTemplate fatherProcardT = (ProcardTemplate) totalDao.get(
					ProcardTemplate.class, sonProcardT.getFatherId());
			cgNum = getcgNum(fatherProcardT, cgNum, belongLayer);
		}
		return cgNum;
	}

	/***
	 * 获取华为设变待反馈清单--下发
	 * 
	 * @param ecaFeedBackBean
	 * @return
	 */
	@Override
	public List updateHWEcList(ECAFeedBackBean ecaFeedBackBean) {
		AutoFeedBackService service = new AutoFeedBackService(
				StationResource.gdpName, StationResource.gdpPassword);
		ResponseInfoBean<ECAFeedBackBean> resultBean;
		try {
			if (ecaFeedBackBean != null) {
				if (ecaFeedBackBean.getEcnName() != null
						&& ecaFeedBackBean.getEcnName().length() > 0) {
					resultBean = service.getECFeedBackByECN(ecaFeedBackBean
							.getEcnName());
				} else if (ecaFeedBackBean.getWorkItemCreateTime() != null
						&& ecaFeedBackBean.getWorkItemCreateTime().length() > 0) {
					resultBean = service.getECFeedBackByTime(ecaFeedBackBean
							.getWorkItemCreateTime());
				} else {
					resultBean = service.getECFeedBack();
				}
			} else {
				resultBean = service.getECFeedBack();
			}
			List<ECAFeedBackBean> efbbList = resultBean.getRecords();
			List<ECAFeedBackBean> efbbList1 = new ArrayList<ECAFeedBackBean>();
			List<ECAFeedBackBean> efbbList2 = new ArrayList<ECAFeedBackBean>();
			StringBuffer sb = new StringBuffer();
			String ecnNumber = null;
			if (ecaFeedBackBean != null) {
				ecnNumber = ecaFeedBackBean.getEcnNumber();// 多条件查询
			}
			for (ECAFeedBackBean ecaFeedBackBean2 : efbbList) {
				if (ecnNumber != null && ecnNumber.length() > 0) {
					if ((ecaFeedBackBean2.getEcnNumber().contains(ecnNumber)
							|| ecaFeedBackBean2.getEcnName()
									.contains(ecnNumber)
							|| ecaFeedBackBean2.getPartDocName().contains(
									ecnNumber) || ecaFeedBackBean2.getEcType()
							.contains(ecnNumber))) {
						efbbList1.add(ecaFeedBackBean2);
						sb.append(ecaFeedBackBean2.getWorkItemId() + ",");
					}
				} else {
					sb.append(ecaFeedBackBean2.getWorkItemId() + ",");
				}
			}
			if (efbbList1.size() > 0
					|| (ecnNumber != null && ecnNumber.length() > 0)) {
				efbbList = efbbList1;
			}

			/***
			 * 修复异常设变数据
			 */
			// for (ECAFeedBackBean ecaFeedBackBean2 : efbbList) {
			// boolean bool = false;
			// String hql =
			// "from ProcardTemplateBanBenApply where outbsNumber=?";
			// ProcardTemplateBanBenApply ptb=(ProcardTemplateBanBenApply)
			// totalDao.getObjectByCondition(hql,
			// ecaFeedBackBean2.getEcnNumber());
			// if(ptb!=null){
			// ptb.setWorkItemId(ecaFeedBackBean2.getWorkItemId());
			// ptb.setEcType(ecaFeedBackBean2.getEcType());
			// totalDao.update(ptb);
			// }
			// }

			String hql = "select workItemId from ProcardTemplateBanBenApply where  workItemId in ("
					+ sb.toString() + "0)";
			List<Long> workItemIdList = totalDao.query(hql);
			if (workItemIdList != null && workItemIdList.size() > 0) {
				for (ECAFeedBackBean ecaFeedBackBean2 : efbbList) {
					boolean bool = false;
					for (Long workItemIdLong : workItemIdList) {
						if (ecaFeedBackBean2.getWorkItemId() != workItemIdLong) {
							bool = true;
						} else {
							bool = false;
							break;
						}
					}
					if (bool) {
						efbbList2.add(ecaFeedBackBean2);
					}
				}
			} else {
				return efbbList;
			}
			return efbbList2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<Integer, Object> uploadzzfile(Integer id,
			List<ProcardTemplateBanBenJudgesFile> ptbbjFileList) {
		// TODO Auto-generated method stub
		Map<Integer, Object> obj = new HashMap<Integer, Object>();
		Users user = Util.getLoginUser();
		if (user == null) {
			obj.put(1, "请先登录!");
			return obj;

		}
		ProcardTemplateBanBenJudges ptbbj = (ProcardTemplateBanBenJudges) totalDao
				.getObjectById(ProcardTemplateBanBenJudges.class, id);
		if (ptbbj == null) {
			obj.put(1, "没有找到目标佐证人!");
			return obj;
		}
		for (ProcardTemplateBanBenJudgesFile file : ptbbjFileList) {
			file.setPtbbapplyId(ptbbj.getPtbbApply().getId());
			totalDao.save(file);
		}
		ProcardTemplateBanBenApply ptbbApply = ptbbj.getPtbbApply();
		ptbbApply.setLastop("上传佐证");
		ptbbApply.setLastUserId(user.getId());
		ptbbApply.setLastUserName(user.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		totalDao.update(ptbbApply);
		obj.put(1, "true");
		obj.put(2, ptbbj.getPtbbApply().getId());
		return obj;
	}

	@Override
	public String plzz(ProcardTemplateBanBenJudgesFile ptbbjfile) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenJudgesFile old = (ProcardTemplateBanBenJudgesFile) totalDao
				.getObjectById(ProcardTemplateBanBenJudgesFile.class, ptbbjfile
						.getId());
		if (old == null) {
			return "没有找到目标佐证!";
		}
		old.setSpStatus(ptbbjfile.getSpStatus());// 审批状态
		old.setSpRemark(ptbbjfile.getSpRemark());// 评语

		// 发送通知
		Integer[] userId = new Integer[] { old.getAddUserId() };
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, old
						.getPtbbapplyId());
		ptbbApply.setLastop("佐证评审");
		ptbbApply.setLastUserId(user.getId());
		ptbbApply.setLastUserName(user.getName());
		ptbbApply.setLastTime(Util.getDateTime());
		totalDao.update(ptbbApply);
		totalDao.update(old);
		AlertMessagesServerImpl.addAlertMessages("设变佐证评审通知", user.getName()
				+ ptbbjfile.getSpStatus() + "了你对总成为:" + ptbbApply.getMarkId()
				+ ",业务件号为：" + ptbbApply.getYwMarkId() + "的BOM设变,上传的佐证!。设变单号："
				+ ptbbApply.getSbNumber(), userId,
				"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
						+ ptbbApply.getId(), true, "no");
		return "true";
	}

	@Override
	public String deletezz(Integer id) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenJudgesFile old = (ProcardTemplateBanBenJudgesFile) totalDao
				.getObjectById(ProcardTemplateBanBenJudgesFile.class, id);
		if (old == null) {
			return "没有找到目标佐证!";
		}
		old.setDataStatus("删除");
		return totalDao.update(old) + "";
	}

	@Override
	public ProcardTemplateBanBenJudgesFile getPtbbjFileByfileId(Integer id) {
		// TODO Auto-generated method stub
		return (ProcardTemplateBanBenJudgesFile) totalDao.getObjectById(
				ProcardTemplateBanBenJudgesFile.class, id);
	}

	@Override
	public List<String> getRyzbList(String type) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return null;
		}
		return totalDao
				.query(
						"select groupName from UsersGroup where id in(select usersGroup.id from AssessPersonnel where userId=?) and status=? ",
						user.getId(), type);
	}

	@Override
	public String addzzry(Integer id, ProcardTemplateBanBenJudges ptbbJudges) {
		// TODO Auto-generated method stub
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录!";
		}
		ProcardTemplateBanBenApply ptbbApply = (ProcardTemplateBanBenApply) totalDao
				.getObjectById(ProcardTemplateBanBenApply.class, id);
		if (ptbbApply == null) {
			return "没有找到目标设变单!";
		}
		if (ptbbJudges.getSelectUserId3() != null
				&& ptbbJudges.getSelectUserId3().length() > 0) {
			Integer[] userId = null;
			String[] checkboxs = ptbbJudges.getSelectUserId3().split(";");
			userId = new Integer[checkboxs.length];
			Set<ProcardTemplateBanBenJudges> ptbbjSet = ptbbApply.getPtbbjset();
			int j = 0;
			// for (ProcardTemplateBanBenJudges had : ptbbjSet) {
			// if (had.getId().equals(ptbbJudges.getId())) {
			// had.setSelectUserId(ptbbJudges.getSelectUserId());
			// had.setSelectUsers(ptbbJudges.getSelectUsers());
			// }
			// }
			List<Integer> userIdList = new ArrayList<Integer>();
			for (String userIdStr : checkboxs) {
				Integer uId = Integer.parseInt(userIdStr);
				userIdList.add(uId);
				Users jUser = (Users) totalDao.getObjectById(Users.class, uId);
				if (jUser != null) {
					boolean hadj = false;
					for (ProcardTemplateBanBenJudges had : ptbbjSet) {
						if (had.getJudgeType().equals("佐证")
								&& had.getUserId().equals(uId)) {
							hadj = true;
							break;
						}
					}
					if (hadj) {
						continue;
					}
					ProcardTemplateBanBenJudges ptbbj = new ProcardTemplateBanBenJudges();
					ptbbj.setDept(jUser.getDept());// 部门
					ptbbj.setUserId(jUser.getId());// 评审人员id
					userId[j] = jUser.getId();
					ptbbj.setUserName(jUser.getName());// 评审名称
					ptbbj.setUserCode(jUser.getCode());// 用户
					// ptbbj.setremark;//评论
					// ptbbj.setsblv;//影响级别
					ptbbj.setAddTime(Util.getDateTime());// 添加时间
					// ptbbj.setplTime;//评论时间
					ptbbj.setPtbbApply(ptbbApply);// 版本升级主表
					// ptbbj.setjudgelv;//评审人员级别（主管，执行员）
					ptbbj.setJudgeType("佐证");// 评审类别（初评,内审）项目主管初步评审选出设变相关,内审人员评审设变对生产和物流的影响
					ptbbj.setJudgedId(ptbbJudges.getId());
					ptbbjSet.add(ptbbj);
					totalDao.save(ptbbj);
					// 发送消息
					j++;
				}
			}
			// 删除此次不要的人员
			Set<ProcardTemplateBanBenJudges> deleteSet = new HashSet<ProcardTemplateBanBenJudges>();
			for (ProcardTemplateBanBenJudges had : ptbbjSet) {
				if (had.getJudgeType().equals("佐证")
						&& !userIdList.contains(had.getUserId())) {
					deleteSet.add(had);
				}
			}
			ptbbjSet.removeAll(deleteSet);
			for (ProcardTemplateBanBenJudges had : deleteSet) {
				had.setPtbbApply(null);
				totalDao.delete(had);
			}
			AlertMessagesServerImpl.addAlertMessages("BOM设变佐证通知", user
					.getName()
					+ "对总成为:"
					+ ptbbApply.getMarkId()
					+ ",业务件号为："
					+ ptbbApply.getYwMarkId()
					+ "的BOM设变发起佐证上传指派,请您及时前往上传设变佐证 !设变单号:"
					+ ptbbApply.getSbNumber(), userId,
					"procardTemplateGyAction_showSbApplyJd2.action?bbAply.id="
							+ ptbbApply.getId(), true, "no");
			ptbbApply.setPtbbjset(ptbbjSet);
			ptbbApply.setLastop("指派佐证人员");
			ptbbApply.setLastUserId(user.getId());
			ptbbApply.setLastUserName(user.getName());
			ptbbApply.setLastTime(Util.getDateTime());
			totalDao.update(ptbbApply);
		}

		return "true";
	}

	@Override
	public Object[] findProcessAndWgProcard(Integer pageSize, Integer pageNo,
			String status, ProcessAndWgProcardTem pawp) {
		if (pawp == null) {
			pawp = new ProcessAndWgProcardTem();
		}
		String hql = totalDao.criteriaQueries(pawp, null, "ywMarkId", "wgType");
		List<ProcessAndWgProcardTem> pawpList = totalDao.findAllByPage(hql,
				pageNo, pageSize);
		int count = totalDao.getCount(hql);
		return new Object[] { pawpList, count };
	}

	@Override
	public void exprotPwap(ProcessAndWgProcardTem pawp) {
		if (pawp == null) {
			pawp = new ProcessAndWgProcardTem();
		}
		long startTime = System.currentTimeMillis();
		String hql = " from ProcardTemplate where 1=1 and procardStyle = '外购'"
				+ " and (dataStatus is null or dataStatus <> '删除')";
		if (pawp.getYwMarkId() != null && pawp.getYwMarkId().length() > 0) {
			hql += " and ywMarkId like '%" + pawp.getYwMarkId() + "%'";
		}
		if (pawp.getProcardMarkId() != null
				&& pawp.getProcardMarkId().length() > 0) {
			hql += " and procardTemplate.id in (select id from ProcardTemplate where "
					+ " markId like '%"
					+ pawp.getProcardMarkId()
					+ "%' and (dataStatus is null or dataStatus <> '删除') )";
		}
		// 拼接工序名
		String hql_process = "";
		String[] processNames = null;
		String process = "";
		if (pawp.getProcessName() != null && !"".equals(pawp.getProcessName())) {
			processNames = pawp.getProcessName().split(",");
			if (processNames != null && processNames.length > 0) {
				for (int i = 0; i < processNames.length; i++) {
					process += "," + "'" + processNames[i] + "'";
				}
				if (process.length() > 1) {
					process = process.substring(1);
				}
				hql_process = "  and markId in(select wgprocardMardkId from ProcessAndWgProcardTem where  processName in("
						+ process + "))";
			}
		}
		hql += hql_process;

		if (pawp.getWgType() != null && pawp.getWgType().length() > 0) {
			Category category = (Category) totalDao.getObjectByCondition(
					" from Category where name=? ", pawp.getWgType());
			getWgtype(category);
			hql += " and wgType in (" + strbu.toString().substring(1) + ")";
			strbu.setLength(0);
		}

		if (pawp.getWgprocardMardkId() != null
				&& pawp.getWgprocardMardkId().length() > 0) {
			hql += " and markId like '%" + pawp.getWgprocardMardkId() + "%'";
		}
		List<ProcardTemplate> procardList = totalDao.query(hql);
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			OutputStream os = response.getOutputStream();
			response.reset();
			String name = pawp.getProcessName();
			if (name == null || name.length() == 0) {
				name = "工序外购件用量";
			}
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(name.getBytes("GB2312"), "8859_1") + ".xls");
			response.setContentType("application/msexcel");
			WritableWorkbook wwb = Workbook.createWorkbook(os);
			WritableSheet ws = wwb.createSheet("工序外购件用量", 0);
			ws.setColumnView(4, 20);
			ws.setColumnView(3, 10);
			ws.setColumnView(2, 20);
			ws.setColumnView(1, 12);
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wc = new WritableCellFormat(wf);
			wc.setAlignment(Alignment.LEFT);
			ws.addCell(new jxl.write.Label(0, 0, "序号", wc));
			ws.addCell(new jxl.write.Label(1, 0, "业务件号", wc));
			ws.addCell(new jxl.write.Label(2, 0, "自制件号", wc));
			ws.addCell(new jxl.write.Label(3, 0, "外购件号", wc));
			ws.addCell(new jxl.write.Label(4, 0, "外购名称", wc));
			ws.addCell(new jxl.write.Label(5, 0, "规格", wc));
			ws.addCell(new jxl.write.Label(6, 0, "物料类别", wc));
			ws.addCell(new jxl.write.Label(7, 0, "单机用量", wc));
			ws.addCell(new jxl.write.Label(8, 0, "单台用量", wc));
			ws.addCell(new jxl.write.Label(9, 0, "工序名", wc));
			ws.addCell(new jxl.write.Label(10, 0, "单机面积", wc));
			ws.addCell(new jxl.write.Label(11, 0, "整机面积", wc));
			if (procardList != null && procardList.size() > 0) {
				Map<String, Float> map = new HashMap<String, Float>();
				Map<String, String> processNameMap = new HashMap<String, String>();
				for (int i = 0, length = procardList.size(); i < length; i++) {
					ProcardTemplate procardT = procardList.get(i);
					if (procardT.getProcardTemplate() == null) {
						continue;
					}
					String zzMarkId = procardT.getProcardTemplate().getMarkId();
					String processName = "";
					if (processNames != null && processNames.length == 1) {
						processName = processNames[0];
					} else if (processNames != null) {
						if (processNameMap.get(zzMarkId + ";"
								+ procardT.getMarkId()) == null) {
							processName = (String) totalDao
									.getObjectByCondition(
											" select  processName from ProcessAndWgProcardTem "
													+ " where procardMarkId=? and wgprocardMardkId =?  and processName in("
													+ process + ")", zzMarkId,
											procardT.getMarkId());
							processNameMap.put(zzMarkId + ";"
									+ procardT.getMarkId(), processName);
						} else {
							processName = processNameMap.get(zzMarkId + ";"
									+ procardT.getMarkId());
						}
						if (processName == null || processName == "") {
							continue;
						}
					}
					Float yongliang = getcgNum(procardT, 1f, procardT
							.getBelongLayer());
					ws.addCell(new jxl.write.Label(0, i + 1, (i + 1) + "", wc));
					ws.addCell(new jxl.write.Label(1, i + 1, procardT
							.getYwMarkId(), wc));
					ws.addCell(new jxl.write.Label(2, i + 1, zzMarkId, wc));
					ws.addCell(new jxl.write.Label(3, i + 1, procardT
							.getMarkId(), wc));
					ws.addCell(new jxl.write.Label(4, i + 1, procardT
							.getProName(), wc));
					ws.addCell(new jxl.write.Label(5, i + 1, procardT
							.getSpecification(), wc));
					ws.addCell(new jxl.write.Label(6, i + 1, procardT
							.getWgType(), wc));
					ws.addCell(new jxl.write.Number(7, i + 1, procardT
							.getQuanzi2(), wc));
					ws.addCell(new jxl.write.Number(8, i + 1, yongliang, wc));
					ws.addCell(new jxl.write.Label(9, i + 1, processName, wc));
					float areakg = 0f;
					if ("粉末".equals(procardT.getWgType())) {
						if (map.get(procardT.getMarkId()) != null
								&& map.get(procardT.getMarkId()) > 0) {
							areakg = map.get(procardT.getMarkId());
						} else if (procardT.getSpecification() != null
								&& procardT.getSpecification().length() > 0) {
							String specification = procardT.getSpecification();
							String array[] = specification.split("[㎡平方米]+");
							String str = array[0];
							try {
								areakg = Float.parseFloat(str.charAt(str
										.length() - 1)
										+ "");
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}
					if (areakg > 0) {
						map.put(procardT.getMarkId(), areakg);
						ws.addCell(new jxl.write.Number(10, i + 1, Util
								.MacthRound(procardT.getQuanzi2() * areakg, 4),
								wc));
						ws.addCell(new jxl.write.Number(11, i + 1, Util
								.MacthRound(yongliang * areakg, 4), wc));
					} else {
						ws.addCell(new jxl.write.Label(10, i + 1, "", wc));
						ws.addCell(new jxl.write.Label(11, i + 1, "", wc));
					}

				}
			}
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	private void getWgtype(Category category) {
		List<Category> list = totalDao.query(
				" from  Category where fatherId = ?", category.getId());
		if (list != null && list.size() > 0) {
			for (Category category2 : list) {
				getWgtype(category2);
			}
		} else {
			strbu.append(",'" + category.getName() + "'");
		}
	}

	private String isSameProcardStyle(ProcardTemplate procardT) {
		// 判断同件号，同业务件号，同产品类型，只能有同一卡片类型。
		int count = totalDao
				.getCount(
						" from ProcardTemplate where markId =? and ywMarkId =?  "
								+ " and productStyle =? and procardStyle <> ? and "
								+ " (dataStatus is null or dataStatus <> '删除' ) and (banbenStatus is null or banbenStatus <> '删除')",
						procardT.getMarkId(), procardT.getYwMarkId(), procardT
								.getProductStyle(), procardT.getProcardStyle());
		if (count > 0) {
			return "同业务件号下，件号:[" + procardT.getMarkId() + "]，有不属于:["
					+ procardT.getProcardStyle() + "]的卡片类型，不符合工艺规范，请检查!~";
		}
		return "";
	}

	/***
	 * 设变时间分析
	 * 
	 * @param userId
	 *            人员id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	@Override
	public Map<String, Object> findSbTime(Integer userId, String startTime,
			String endTime) {
		if (userId != null) {
			// 加载审批人员
			List userList = null;
			if (userId == 0) {
				String hql = "select id,name from Users where id in ("
						+ "select applicantId from ProcardTemplateBanBenApply where colseTime is not null and applyTime is not null group by applicantId)";
				userList = totalDao.query(hql);
			}

			String hql = "select applyTime,colseTime "
					+ "from ProcardTemplateBanBenApply  where colseTime is not null and applyTime is not null";
			if (userId != null && userId > 0) {
				hql += " and applicantId= '" + userId + "'";
			}
			if (startTime != null && startTime.length() > 0) {
				hql += " and applyTime>'" + startTime + "'";
			}
			if (endTime != null && endTime.length() > 0) {
				hql += " and applyTime<'" + endTime + "'";
			}
			List<Object[]> list = totalDao.query(hql);
			if (list != null && list.size() > 0) {
				List datelist = new ArrayList();
				List timelonglist = new ArrayList();
				List zwslist = new ArrayList();
				float[] nums = new float[list.size()];
				int i = 0;
				for (Object[] objects : list) {
					String atime = (String) objects[0];
					String btime = (String) objects[1];
					datelist.add(atime.substring(0, 10));
					try {
						Long times = Util.getDateDiff(atime,
								"yyyy-MM-dd HH:mm:ss", btime,
								"yyyy-MM-dd HH:mm:ss");
						Float hours = Float.parseFloat(String.format("%.2f",
								times / 3600F));
						timelonglist.add(hours);
						nums[i] = hours;
					} catch (Exception e) {
						e.printStackTrace();
						nums[i] = 0F;
					}
					i++;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				// 求中位数
				Float zws = Util.median(nums);
				for (float f : nums) {
					zwslist.add(zws);
				}
				map.put("timelong", timelonglist);
				map.put("yuefen", datelist);
				map.put("zws", zwslist);
				map.put("users", userList);
				return map;
			}
		}
		return null;
	}

	@Override
	public String copySonsPttoProcard(Integer id, Integer id2) {
		List<ProcardTemplate> hadSonList = totalDao
				.query(
						" from ProcardTemplate where fatherId=? and (dataStatus is null or dataStatus <> '删除' ) and (banbenStatus is null or banbenStatus <> '历史')"
								+ " and  markId not in(select markId from Procard where fatherId=? and (dataStatus is null or dataStatus <> '删除' ))",
						id, id2);
		Procard procard = (Procard) totalDao.getObjectById(Procard.class, id2);
		if (hadSonList != null && hadSonList.size() > 0 && procard != null) {
			for (ProcardTemplate son : hadSonList) {
				addsonProcard(procard.getId(), son.getId(), procard, son);
			}
			return "true";
		}
		return "失败";
	}

	@Override
	public String addsonProcard(Integer id, Integer id2, Procard father,
			ProcardTemplate procardTemplate) {
		// TODO Auto-generated method stub

		String message = "";
		// 生成流水卡片
		if (father == null) {
			father = (Procard) totalDao.getObjectById(Procard.class, id);
		}
		if (procardTemplate == null) {
			procardTemplate = (ProcardTemplate) totalDao.getObjectById(
					ProcardTemplate.class, id2);
		}
		message += procardTemplate.getMarkId();
		Procard procard = new Procard();
		int youxiao = 0;// 之前生成的零件是否有效0无效,1有效
		// if(index>0
		// &&pro!=null&&pro.getMarkId().equals(procardTemplate.getMarkId())&&pro.getBanci().equals(procardTemplate.getBanci())){
		// if(pro.getProcard().getStatus().equals("初始")||pro.getProcard().getStatus().equals("已发卡")
		// ||pro.getProcard().getStatus().equals("已发料")){//上层零件未生产过
		// youxiao=1;
		// }else if(father.getStatus().equals("领工序")){
		// //查询是否有工序进行生产
		// Float count = (Float)
		// totalDao.getObjectByCondition("select count(*) from ProcessInfor where procard.id=? and applyCount>0",
		// pro.getProcard().getId());
		// if(count==null||count==0){
		// youxiao=1;
		// }
		// }
		// }
		if (youxiao == 0) {
			/**
			 * 将流水卡片模板转换为流水卡片
			 */
			BeanUtils.copyProperties(procardTemplate, procard, new String[] {
					"rootId", "fatherId", "id" });
			if (procard.getBanci() == null) {
				procard.setBanci(0);
			}
			Float needNumber = null;
			if ("外购".equals(procard.getProcardStyle())) {
				Map<String, String> markidMap = new HashMap();
				markidMap.put("1.01.30017", "1.01.30022");
				markidMap.put("1.01.30005", "1.01.30011");
				markidMap.put("1.01.30012", "1.01.30007");
				markidMap.put("1.01.10025", "1.01.10055");
				markidMap.put("1.01.10021", "1.01.10040");
				markidMap.put("1.01.10028", "1.01.10043");
				markidMap.put("1.01.10027", "1.01.10042");
				markidMap.put("1.01.10026", "1.01.10062");
				markidMap.put("1.01.10030", "1.01.12501");
				markidMap.put("1.01.10029", "1.01.12537");
				markidMap.put("1.01.30018", "1.01.30014");
				markidMap.put("1.01.30026", "1.01.30016");
				markidMap.put("1.01.30034", "1.01.30010");
				markidMap.put("2.09.00014", "30100005");
				markidMap.put("DKBA8.840.0069", "30040054");
				markidMap.put("QDKBA4.409.0670", "26020187");
				markidMap.put("1.04.00560", "28010212");
				markidMap.put("DKBA3360.1", "30060106");
				markidMap.put("1.01.30002", "1.01.30013");

				String zhmarkid = (String) markidMap.get(procard.getMarkId());
				if ((zhmarkid != null) && (zhmarkid.length() > 0)) {
					String sql1 = "from YuanclAndWaigj where  (banbenStatus is null or banbenStatus!='历史') and markId=? and kgliao =? ";
					YuanclAndWaigj yuanclAndWaigj = null;
					try {
						yuanclAndWaigj = (YuanclAndWaigj) totalDao
								.getObjectByCondition(sql1, new Object[] {
										zhmarkid, procard.getKgliao() });
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (yuanclAndWaigj != null) {
						BeanUtils.copyProperties(yuanclAndWaigj, procard,
								new String[] { "id" });

						procard.setProName(yuanclAndWaigj.getName());
					}
				}
				needNumber = Float.valueOf(father.getFilnalCount().floatValue()
						* procard.getQuanzi2().floatValue()
						/ procard.getQuanzi1().floatValue());
				if (procard.getUnit().equals("PCS")
						|| procard.getUnit().equals("套")
						|| procard.getUnit().equals("瓶")
						|| procard.getUnit().equals("个")) {
					needNumber = (float) Math.ceil(needNumber);
				} else {
					needNumber = Util.FomartFloat(needNumber, 4);
				}
				String result = String.format("%.4f",
						new Object[] { needNumber });
				needNumber = Float.valueOf(Float.parseFloat(result));
				if (!"否".equals(procard.getLingliaostatus())) {
					procard.setLingliaostatus("是");
				}
				// needNumber = father.getFilnalCount() * procard.getQuanzi2()
				// / procard.getQuanzi1();
			} else {
				needNumber = father.getFilnalCount() * procard.getCorrCount();
				needNumber = (float) Math.ceil(needNumber);
			}

			// Double d = Math.ceil(needNumber);
			// procard.setNeedCount(Float.parseFloat(d.toString()));//
			// 计算外购/自制的实际需求数量(自动进1取整)
			// procard.setFilnalCount(Float.parseFloat(d.toString()));
			// Double d = Math.ceil(needNumber);
			// procard.setNeedCount(needNumber);// 计算外购/自制的实际需求数量(自动进1取整)

			procard.setPlanOrderId(father.getPlanOrderId());// 内部计划单id
			procard.setPlanOrderNum(father.getPlanOrderNum());// 内部计划单号
			procard.setOrderNumber(father.getOrderNumber());// 订单编号
			procard.setOrderId(father.getOrderId());// 订单id
			procard.setProcardTemplateId(procardTemplate.getId());// bom模板id
			procard.setStatus("初始");
			procard.setProcardTime(Util.getDateTime());// 制卡时间
			procard.setSelfCard(findMaxSelfCard(procard.getMarkId()));// 批次号
			procard.setZhikaren(Util.getLoginUser().getName());// 制卡人(当前登录用户)
			procard.setBarcode(UUID.randomUUID().toString());// 条码
			procard.setFilnalCount(needNumber);// 生产数量

			// 设置调用关系
			procard.setFatherId(father.getId());// 父类id
			procard.setRootId(father.getRootId());// 更新rootId
			procard.setProcard(father);// 设置父类
			procard.setRootSelfCard(father.getRootSelfCard());
			procard.setRootMarkId(father.getRootMarkId());
			procard.setYwMarkId(father.getYwMarkId());
			procard.setProductStyle(father.getProductStyle());
			procard.setJioafuDate(Util.getDateTime());
			procard.setHasChange("是");
			// procard.setSbId(ptbbApplyId);
			// procard.setSbNumber(sbNumber);
			procard.setMinNumber(0f);
			if (procard.getProcardStyle().equals("外购")) {
				// 添加外购件欲增数量
				// addProcardSbWg(ptbbApplyId, sbNumber, procard, needNumber,
				// Util
				// .getDateTime());
				sbData++;
			}
			totalDao.save(procard);// 添加
			// if(!"待定".equals(totalWlStatus)&&"外购".equals(procard.getProcardStyle())){//此生成BOM已被激活
			//				
			// }
			// 遍历该流水卡片对应工序并生成工序
			Set<ProcessTemplate> setProCess = procardTemplate
					.getProcessTemplate();
			for (ProcessTemplate processTem : setProCess) {
				ProcessInfor process = new ProcessInfor();
				BeanUtils.copyProperties(processTem, process, new String[] {
						"id", "procardTemplate" });
				// process.setProcessNO(processTem.getProcessNO());// 工序号
				// process.setProcessName(processTem.getProcessName());// 工序名称
				// process.setProcessStatus(processTem.getProcessStatus());//
				// 状态(并行/单独)
				// process.setParallelId(processTem.getParallelId());// 并行开始id

				// 人工节拍和设备节拍处理
				if (process.getProductStyle() != null
						&& process.getProductStyle().equals("外委")) {// 外委工序节拍设为1
					process.setOpcaozuojiepai(1f);
					process.setOpshebeijiepai(1f);
					process.setGzzhunbeijiepai(1f);
					process.setGzzhunbeicishu(1f);
					process.setAllJiepai(1f);
				} else {
					if (process.getOpcaozuojiepai() == null) {
						process.setOpcaozuojiepai(0F);
					}
					if (process.getOpshebeijiepai() == null) {
						process.setOpshebeijiepai(0F);
					}
				}
				process.setTotalCount(procard.getFilnalCount());// 可领取量
				process.setStatus("初始");
				process.setProcessTemplateId(processTem.getId());
				process.setProcard(procard);
				// -----------------辅料------------------
				if (processTem.getIsNeedFuliao() != null
						&& processTem.getIsNeedFuliao().equals("yes")) {
					process.setIsNeedFuliao("yes");
					Set<ProcessFuLiaoTemplate> fltmpSet = processTem
							.getProcessFuLiaoTemplate();
					if (fltmpSet.size() > 0) {
						Set<ProcessinforFuLiao> pinfoFlSet = new HashSet<ProcessinforFuLiao>();
						for (ProcessFuLiaoTemplate fltmp : fltmpSet) {
							ProcessinforFuLiao pinfoFl = new ProcessinforFuLiao();
							BeanUtils.copyProperties(fltmp, pinfoFl,
									new String[] { "id" });
							if (pinfoFl.getQuanzhi1() == null) {
								pinfoFl.setQuanzhi1(1f);
							}
							if (pinfoFl.getQuanzhi2() == null) {
								pinfoFl.setQuanzhi2(1f);
							}
							pinfoFl.setTotalCount(procard.getFilnalCount()
									* pinfoFl.getQuanzhi2()
									/ pinfoFl.getQuanzhi1());
							pinfoFl.setProcessInfor(process);
							pinfoFl.setOutCount(0f);
							pinfoFlSet.add(pinfoFl);
						}
						process.setProcessinforFuLiao(pinfoFlSet);
					}
				}
				try {
					totalDao.save(process);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			// 遍历查询子类流水卡片
			// Set<ProcardTemplate> setProCard =
			// procardTemplate.getProcardTSet();
			List<ProcardTemplate> setProCard = totalDao
					.query(
							"  from ProcardTemplate where fatherId =?"
									+ " and  (dataStatus <> '删除' or dataStatus is null ) and (banbenstatus <> '历史'"
									+ "or banbenstatus is null )",
							procardTemplate.getId());
			// Set<Procard> sonSet = pro.getProcardSet();
			if (setProCard != null && setProCard.size() > 0) {
				for (ProcardTemplate procardTem2 : setProCard) {
					if (procardTem2.getBzStatus() == null
							|| !procardTem2.getBzStatus().equals("已批准")) {
						throw new RuntimeException("零件:"
								+ procardTem2.getMarkId() + "的当前编制状态为:"
								+ procardTem2.getBzStatus() + "不能用于生成或更新生产件!");
					}
					Procard thisProcard = null;
					// if(sonSet!=null&&sonSet.size()>0){
					// for(Procard son:sonSet){
					// if(son.getProcardTemplateId().equals(procardTem2.getId())){
					// thisProcard = son;
					// }
					// }
					// }
					addsonProcard(procard.getId(), procardTem2.getId(),
							procard, procardTem2);
				}
			}
		} else {
			// copyProcard(procard, pro, procardTemplate.getQuanzi1(),
			// procardTemplate.getQuanzi2(), procardTemplate
			// .getCorrCount(), father);
		}
		return "true";

	}

	@Override
	public String copySonProcardTemplates(Integer id, Integer id2) {
		// TODO Auto-generated method stub
		List<ProcardTemplate> hadSonList = totalDao
				.query(
						" from ProcardTemplate where fatherId=? and (dataStatus is null or dataStatus <> '删除' ) and (banbenStatus is null or banbenStatus <> '历史')"
								+ " and  markId not in(select markId from ProcardTemplate where fatherId=? and (dataStatus is null or dataStatus <> '删除' ) and (banbenStatus is null or banbenStatus <> '历史'))",
						id, id2);
		ProcardTemplate father = (ProcardTemplate) totalDao.getObjectById(
				ProcardTemplate.class, id2);
		if (hadSonList != null && hadSonList.size() > 0) {
			for (ProcardTemplate son : hadSonList) {
				saveCopyProcard(father, son, father.getProductStyle());
			}
		}
		return null;
	}

	@Override
	public String daoruLyJxBOM(File bomTree, String bomTreeFileName,
			String productStyle) {

		// TODO Auto-generated mSethod stub
		String jymsg = "";
		Users user = Util.getLoginUser();
		if (user == null) {
			return "请先登录！";
		}
		String nowTime = Util.getDateTime();
		String fileRealPath = ServletActionContext.getServletContext()
				.getRealPath("/upload/file/bomxls");

		File file = new File(fileRealPath);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(fileRealPath + "/" + bomTreeFileName);
		try {
			FileCopyUtils.copy(bomTree, file2);

			// 开始读取excel表格
			InputStream is = new FileInputStream(file2);// 创建文件流
			jxl.Workbook rwb = Workbook.getWorkbook(is);// 创建workBook
			Sheet rs = null;
			try {
				rs = rwb.getSheet(0);
			} catch (IndexOutOfBoundsException e2) {
				rs = rwb.getSheet(0);
				e2.printStackTrace();
			}
			if (rs == null) {
				return "未找到需要处理的Sheet文件!";
			}
			int excelcolumns = rs.getRows();// 获得总行数
			if (excelcolumns > 2) {
				List<ProcardTemplate> ptList = new ArrayList<ProcardTemplate>();
				Map<String, ProcardTemplate> map = new HashMap<String, ProcardTemplate>();
				for (int i = 2; i < excelcolumns; i++) {
					ProcardTemplate pt = new ProcardTemplate();
					Cell[] cells = rs.getRow(i);// 获得每i行的所有单元格（返回的是数组）
					String regx = "[\\r\\n\\t\\b ]+";
					String shiyongdanwei = cells[7].getContents();
					String zhuzidanwei = cells[5].getContents();
					if ("机匣".equals(shiyongdanwei) || "机匣".equals(zhuzidanwei)) {
						String markId = cells[0].getContents();
						String proName = cells[1].getContents();
						String fatherMarkId = cells[3].getContents();
						String xiaohaoCountStr = cells[4].getContents();
						Float xiaohaoCount = null;
						if (xiaohaoCountStr != null
								&& !"".equals(xiaohaoCountStr)) {
							xiaohaoCount = Float.parseFloat(xiaohaoCountStr);
						}
						String fuzidanwei = cells[6].getContents();
						// String dantaiStr = cells[14].getContents();
						// Float zhdt = null;
						// if(dantaiStr!=null && !"".equals(dantaiStr)){
						// zhdt = Float.parseFloat(dantaiStr);
						// }
						// String remark =
						// cells[15].getContents().replaceAll(regx, "");
						String leixing = "";
						if ("外购".equals(zhuzidanwei)) {
							leixing = "外购";
						} else {
							leixing = "自制";
						}
						if (null == fatherMarkId || "".equals(fatherMarkId)) {
							leixing = "总成";
						}
						pt.setMarkId(markId);
						pt.setProName(proName);
						pt.setFatherMarkId(fatherMarkId);
						pt.setXiaohaoCount(xiaohaoCount);
						pt.setProcardStyle(leixing);
						pt.setProductStyle(productStyle);
						// pt.setRemark(remark);
						if ("自制".equals(leixing)) {
							// pt.setCorrCount(zhdt);
						} else if ("外购".equals(leixing)) {
							// pt.setQuanzi1(1f);
							// pt.setQuanzi2(zhdt);
						}
						map.put(pt.getMarkId(), pt);
						ptList.add(pt);
					}
				}
				if (ptList.size() > 0) {
					// 获取同件号自制件
					ProcardTemplate totalPt = null;
					totalPt = ptList.get(0);
					// totalPt.setYwMarkId(ywMarkId);
					// totalPt.setAddDateTime(createTime);
					// totalPt.setCreateDate(createTime);
					// totalPt.setCreateUserName(createUser);
					// totalPt.setLastUser(lastUser);
					totalPt.setProcardStyle("总成");
					totalPt.setCorrCount(1f);
					totalPt.setBelongLayer(1);
					totalPt.setDaoruDate(new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date()));
					totalPt.setRootMarkId(totalPt.getMarkId());
					ProcardTemplate same = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=? and (dataStatus is null or dataStatus!='删除') and procardStyle='总成' and productStyle='批产'",
									totalPt.getMarkId());
					if (same != null) {
						throw new RuntimeException("该总成件号已存在,导入失败!</br>");
					}
					totalDao.save(totalPt);
					totalPt.setRootId(totalPt.getId());
					totalDao.update(totalPt);
					String banbenSql = null;
					if (totalPt.getBanBenNumber() == null
							|| totalPt.getBanBenNumber().length() == 0) {
						banbenSql = " and (banBenNumber is null or banBenNumber='')";
					} else {
						banbenSql = " and banBenNumber = '"
								+ totalPt.getBanBenNumber() + "'";
					}
					ProcardTemplate zzSame = (ProcardTemplate) totalDao
							.getObjectByCondition(
									"from ProcardTemplate where markId=?  and procardStyle='自制' and productStyle='批产' and (banbenStatus is null or banbenStatus !='历史') and (dataStatus is null or dataStatus!='删除')"
											+ banbenSql, totalPt.getMarkId());
					if (zzSame != null) {
						copyjcMsg(zzSame, totalPt, totalPt.getYwMarkId(), 0,
								user);
						totalPt.setProcardStyle("总成");
						List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
								.query("from ProcardTemplate where fatherId=?",
										zzSame.getId());
						if (historySonList != null && historySonList.size() > 0) {
							Set<ProcardTemplate> nowSonSet = totalPt
									.getProcardTSet();
							List<String> hadMarkIds = new ArrayList<String>();
							if (nowSonSet != null && nowSonSet.size() > 0) {
								for (ProcardTemplate nowSon : nowSonSet) {
									hadMarkIds.add(nowSon.getMarkId());
								}
							}
							for (ProcardTemplate historySon : historySonList) {
								try {
									if (!hadMarkIds.contains(historySon
											.getMarkId())) {
										saveCopyProcard(totalPt, historySon,
												"批产");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						return "导入成功,此BOM的总成在其他BOM中已存在,直接借用!";
					}
					Map<String, ProcardTemplate> map1 = new HashMap<String, ProcardTemplate>();
					map1.put(totalPt.getMarkId(), totalPt);
					if (ptList.size() >= 1) {
						addProcardTemLyJx(ptList, 1, null, user, map1,
								productStyle);
					}
					String nowtime = Util.getDateTime();
					for (ProcardTemplate hasHistory : ptList) {
						// if ( hasHistory.getId() != null
						// && hasHistory.getFatherId() != null
						// ) {
						// ProcardTemplateChangeLog.addchangeLog(totalDao,
						// totalPt, hasHistory, "子件", "增加", user,
						// nowtime);
						// }
						hasHistory.setAddcode(user.getCode());
						hasHistory.setAdduser(user.getName());
						hasHistory.setAddtime(nowTime);
						if (hasHistory.getHistoryId() != null) {
							if (!hasHistory.getProcardStyle().equals("外购")) {
								List<ProcardTemplate> historySonList = (List<ProcardTemplate>) totalDao
										.query(
												"from ProcardTemplate where fatherId=? and (dataStatus is null or dataStatus!='删除')",
												hasHistory.getHistoryId());
								if (historySonList != null
										&& historySonList.size() > 0) {
									Set<ProcardTemplate> nowSonSet = hasHistory
											.getProcardTSet();
									List<String> hadMarkIds = new ArrayList<String>();
									if (nowSonSet != null
											&& nowSonSet.size() > 0) {
										for (ProcardTemplate nowSon : nowSonSet) {
											hadMarkIds.add(nowSon.getMarkId());
										}
									}
									for (ProcardTemplate historySon : historySonList) {
										try {
											if (!hadMarkIds.contains(historySon
													.getMarkId())) {
												System.out
														.println(hasHistory
																.getMarkId()
																+ "======="
																+ historySon
																		.getMarkId()
																+ " ======================");
												saveCopyProcard(hasHistory,
														historySon, "批产");
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						} else {
							if (hasHistory.getId() != null) {
								if (hasHistory.getBanci() == null) {
									hasHistory.setBanci(0);
								}
								if (hasHistory.getBanbenStatus() == null) {
									hasHistory.setBanbenStatus("默认");
								}
								if (hasHistory.getBzStatus() == null
										|| hasHistory.getBzStatus()
												.equals("初始")
										|| hasHistory.getBzStatus().equals(
												"待编制")) {
									hasHistory.setBzStatus("待编制");
									hasHistory.setBianzhiId(user.getId());
									hasHistory.setBianzhiName(user.getName());
									totalDao.update(hasHistory);
								}
							}
						}
					}
					return "true";
					// if (id != null) {
					// Set<ProcardTemplate> sons = totalPt
					// .getProcardTSet();
					// if (sons != null) {
					// for (ProcardTemplate son : sons) {
					// if (son.getId() >= thisminId) {
					// ProcardTemplateChangeLog.addchangeLog(
					// totalDao, totalPt, son, user,
					// Util.getDateTime());
					// }
					// }
					// }
					// }

				}
			} else {
				return "没有数据!";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "文件异常,导入失败!";
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "文件异常,导入失败!";
		}
		return "导入失败,未读取到有效数据,请核实模板格式是否有误!";
	}

	@Override
	public String addProcardTemLyJx(List<ProcardTemplate> ptList,
			Integer belongLayer, String zcMakrId, Users user,
			Map<String, ProcardTemplate> map1, String productStyle) {
		String msg = "";
		Integer biaoji = 0;
		for (int j = 1; j < ptList.size(); j++) {
			ProcardTemplate pt = ptList.get(j);
			pt.setProductStyle(productStyle);
			if (pt.getFatherMarkId() != null
					&& !"".equals(pt.getFatherMarkId())) {
				ProcardTemplate fatherPt = map1.get(pt.getFatherMarkId());
				if ("外购".equals(pt.getProcardStyle())) {
					YuanclAndWaigj wgj = (YuanclAndWaigj) totalDao
							.getObjectByCondition(
									"from  YuanclAndWaigj where markId = ? ",
									pt.getMarkId());
					if (wgj == null) {
						addWgj(pt, user, j, msg);
					} else {

					}
				}
				if (fatherPt != null) {
					pt.setFatherId(fatherPt.getId());
					pt.setRootId(fatherPt.getRootId());
					pt.setRootMarkId(fatherPt.getRootMarkId());
					pt.setProcardTemplate(fatherPt);
					pt.setBelongLayer(fatherPt.getBelongLayer() + 1);
					if (fatherPt.getHistoryId() != null) {
						map1.put(pt.getMarkId(), pt);
						biaoji = pt.getBelongLayer();
						continue;
					}
					if (biaoji != 0 && pt.getBelongLayer() > biaoji) {
						map1.put(pt.getMarkId(), pt);
						continue;
					}
				}
			}

			ProcardTemplate historypt = (ProcardTemplate) totalDao
					.getObjectByCondition(
							"from ProcardTemplate where markId = ? "
									+ " and (dataStatus is null or dataStatus <> '删除') and (banbenStatus is null or banbenStatus <> '历史')",
							pt.getMarkId());
			if (historypt != null) {
				pt.setHistoryId(historypt.getId());
			}
			totalDao.save(pt);
			if ("总成".equals(pt.getProcardStyle())) {
				pt.setBelongLayer(1);
				pt.setRootId(pt.getId());
				pt.setRootMarkId(pt.getMarkId());
				totalDao.update(pt);
			}
			map1.put(pt.getMarkId(), pt);
		}
		return null;
	}

	private void addWgj(ProcardTemplate sonPt, Users user, Integer i, String msg) {
		YuanclAndWaigj wgjTem = new YuanclAndWaigj();
		wgjTem.setMarkId(sonPt.getMarkId());// 件号
		wgjTem.setName(sonPt.getProName());// 名称
		wgjTem.setSpecification(sonPt.getSpecification());// 规格
		wgjTem.setUnit(sonPt.getUnit());// BOM用的单位
		wgjTem.setBanbenhao(sonPt.getBanBenNumber());// 版本号
		wgjTem.setClClass("外购件");// 物料类别（外购件,原材料,辅料）
		wgjTem.setCaizhi(sonPt.getCaizhi());// 材质类型
		wgjTem.setTuhao(sonPt.getTuhao());// 图号
		wgjTem.setWgType(sonPt.getWgType());// 物料类别
		wgjTem.setKgliao(sonPt.getKgliao());
		wgjTem.setProductStyle(sonPt.getProductStyle());
		wgjTem.setPricestatus("新增");
		wgjTem.setBanbenStatus("默认");
		wgjTem.setBili(sonPt.getBili());
		wgjTem.setAddTime(Util.getDateTime());
		wgjTem.setAddUserCode(user.getCode());
		wgjTem.setAddUserName(user.getName());
		wgjTem.setImportance(sonPt.getImportance());
		// wgjTem.setAreakg(sonPt
		// .getAreakg());// 每公斤使用面积
		// wgjTem.setDensity(sonPt
		// .getDensity());// 密度
		// wgjTem.setMinkc(wgjTemerror.getMinkc());//
		// 最小库存量；低于此库存开始自动采购（为null时不考虑）
		// wgjTem.setCgcount(wgjTemerror
		// .getCgcount());// 安全库存采购的采购量
		// wgjTem.setCgperiod(wgjTemerror
		// .getCgperiod());
		wgjTem.setAvgProductionTakt(sonPt.getAvgProductionTakt());// 生产节拍(s)
		wgjTem.setAvgDeliveryTime(sonPt.getAvgDeliveryTime());// 配送时长(d)
		if (wgjTem.getWgType() == null) {
			msg += "第" + (i + 1) + "行" + sonPt.getMarkId()
					+ "--此外购件没有物料类别，请先处理!<br/>";
		} else {
			totalDao.save(wgjTem);
		}
	}

}
