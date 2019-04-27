package com.task.ServerImpl.pro;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.task.Dao.TotalDao;
import com.task.Server.InsuranceGoldServer;
import com.task.Server.WageStandardServer;
import com.task.Server.pro.QuotedPriceServer;
import com.task.ServerImpl.AlertMessagesServerImpl;
import com.task.ServerImpl.AttendanceTowServerImpl;
import com.task.ServerImpl.sys.CircuitRunServerImpl;
import com.task.entity.*;
import com.task.entity.codetranslation.CodeTranslation;
import com.task.entity.gzbj.Gzstore;
import com.task.entity.project.*;
import com.task.entity.sop.OutSourcingApp;
import com.task.entity.sop.ProcardTemplate;
import com.task.entity.sop.ProcessTemplate;
import com.task.entity.sop.ycl.YuanclAndWaigj;
import com.task.entity.system.CircuitRun;
import com.task.util.DateUtil;
import com.task.util.RtxUtil;
import com.task.util.Upload;
import com.task.util.Util;
import jxl.Cell;
import jxl.DateCell;
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
import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 核算Server层实现类
 *
 * @author 刘培
 *
 */
@SuppressWarnings("unchecked")
public class QuotedPriceServerImpl implements QuotedPriceServer {

    private TotalDao totalDao;
    private InsuranceGoldServer igs; // 五险一金Server层
    private WageStandardServer wss; // 工资模板
    private List<ProjectMaterial> pmList;
    private float sumprice = 0f;
    private static final Double SECONDS = 619200.0;


    public TotalDao getTotalDao() {
        return totalDao;
    }

    public void setTotalDao(TotalDao totalDao) {
        this.totalDao = totalDao;
    }

    public InsuranceGoldServer getIgs() {
        return igs;
    }

    public void setIgs(InsuranceGoldServer igs) {
        this.igs = igs;
    }

    /***
     * 添加根部核算信息
     *
     * @param projectManage
     */
    @Override
    public void addQuotedPrice(QuotedPrice quotedPrice, File[] attachment,
                               String[] attachmentFileName) {
        if (quotedPrice != null) {
            // 查询项目是否存在
            ProjectManage projectManage = (ProjectManage) totalDao
                    .getObjectById(ProjectManage.class, quotedPrice.getProId());
            if (projectManage != null) {
                quotedPrice.setFatherId(0);
                quotedPrice.setBelongLayer(1);
                quotedPrice.setProcardStyle("总成");
                quotedPrice.setStatus("初始");
                quotedPrice.setProcardTime(Util.getDateTime());
                quotedPrice.setZhikaren(Util.getLoginUser().getName());
                quotedPrice.setBarcode(Util.getDateTime("yyyyMMddHHmmss"));
                // 附件处理
                if (attachment != null && attachment.length > 0) {
                    for (int i = 0; i < attachment.length; i++) {
                        File file = attachment[i];
                        String fileName = attachmentFileName[i];
                        Upload upload = new Upload();
                        String newFileName = upload.UploadFile(file, fileName,
                                null, "/upload/file/project",
                                "D:/WorkSpace/HHTask/WebRoot/upload/file");
                        quotedPrice.setFileName(newFileName);
                    }
                }
                if (null == quotedPrice.getQuotedNumber() || "".equals(quotedPrice.getQuotedNumber())) {
                    // 核算编号处理
                    String num = "XMBJ-" + Util.getDateTime("yyyy");
                    String hql = "select max(quotedNumber) from QuotedPrice where procardStyle='总成' and quotedNumber like '%"
                            + num + "%'";
                    Object obj = totalDao.getObjectByCondition(hql);
                    if (obj == null) {
                        num += "-001";
                    } else {
                        String maxNum = (String) obj;
                        int number = Integer.parseInt(maxNum.substring(maxNum
                                .lastIndexOf("-") + 1, maxNum.length())) + 1;
                        if (number < 10) {
                            num += "-00" + number;
                        } else if (number < 100) {
                            num += "-0" + number;
                        } else {
                            num += "-" + number;
                        }
                    }
                    quotedPrice.setQuotedNumber(num);
                }
                totalDao.save(quotedPrice);
                Users loginUser = Util.getLoginUser();
                String datetime = Util.getDateTime();
                BaomiOperateLog log = new BaomiOperateLog();
                log.setOperateType("增加");//操作类型
                log.setOperateObject("项目零件");//操作对象
                log.setOperateRemark("在项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum()
                        + "的项目下添加零件号为:" + quotedPrice.getMarkId() + "的零件");//
                log.setOperateTime(datetime);//
                log.setOperateUserId(loginUser.getId());
                log.setOperateUsername(loginUser.getName());//
                log.setOperateCode(loginUser.getCode());//
                log.setOperateDept(loginUser.getDept());//
                totalDao.save2(log);

                // 更新rootID
                quotedPrice.setRootId(quotedPrice.getId());
                // 更新项目流程状态为核算
                // projectManage.setStatus("核算");
                totalDao.update(projectManage);

                /** *******************添加各个部分的时间表*********************** **/
                // 核算填报
                ProjectTime projectTime = new ProjectTime(null, "核算填报", "hstb",
                        loginUser.getDept(), loginUser.getId(), loginUser
                        .getName(), "no", datetime, datetime, "",
                        datetime, loginUser.getName(), 1,
                        projectManage.getId(), quotedPrice.getId(),
                        projectManage);
                totalDao.save(projectTime);
                // BOM录入
                List<ProjectTime> bomTimeList = totalDao
                        .query("from ProjectTime where classNumber='bom' order by addDateTime desc");
                ProjectTime bomprojectTime = new ProjectTime();
                if (bomTimeList.size() > 0) {
                    for (ProjectTime time : bomTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, bomprojectTime,
                            // new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            bomprojectTime.setUserId(time.getUserId());
                            bomprojectTime.setUserName(time.getUserName());
                            bomprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                bomprojectTime.setClassName("BOM");
                bomprojectTime.setClassNumber("bom");
                // bomprojectTime.setAddDateTime(datetime);
                bomprojectTime.setAddUserName(loginUser.getName());
                bomprojectTime.setLevel(2);
                bomprojectTime.setProId(projectManage.getId());
                bomprojectTime.setQuoId(quotedPrice.getId());
                bomprojectTime.setProjectManage(projectManage);
                totalDao.save(bomprojectTime);
                // 材料录入
                List<ProjectTime> clTimeList = totalDao
                        .query(" from ProjectTime where classNumber='cl' order by addDateTime desc");
                ProjectTime clprojectTime = new ProjectTime();
                if (clTimeList.size() > 0) {
                    for (ProjectTime time : clTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, clprojectTime, new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            clprojectTime.setUserId(time.getUserId());
                            clprojectTime.setUserName(time.getUserName());
                            clprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                clprojectTime.setClassName("原材料价格");
                clprojectTime.setClassNumber("cl");
                // clprojectTime.setAddDateTime(datetime);
                clprojectTime.setAddUserName(loginUser.getName());
                clprojectTime.setLevel(3);
                clprojectTime.setProId(projectManage.getId());
                clprojectTime.setQuoId(quotedPrice.getId());
                clprojectTime.setProjectManage(projectManage);
                totalDao.save(clprojectTime);
                // 设备录入
                ProjectTime sbprojectTime = new ProjectTime("设备折旧", "sb",
                        datetime, loginUser.getName(), 4,
                        projectManage.getId(), quotedPrice.getId(),
                        projectManage);
                totalDao.save(sbprojectTime);
                // 工装录入
                List<ProjectTime> gzTimeList = totalDao
                        .query("from ProjectTime where classNumber='gz' order by addDateTime desc");
                ProjectTime gzprojectTime = new ProjectTime();
                if (gzTimeList.size() > 0) {
                    for (ProjectTime time : gzTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, gzprojectTime, new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            gzprojectTime.setUserId(time.getUserId());
                            gzprojectTime.setUserName(time.getUserName());
                            gzprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                gzprojectTime.setClassName("自制工装");
                gzprojectTime.setClassNumber("gz");
                // gzprojectTime.setAddDateTime(datetime);
                gzprojectTime.setAddUserName(loginUser.getName());
                gzprojectTime.setLevel(5);
                gzprojectTime.setProId(projectManage.getId());
                gzprojectTime.setQuoId(quotedPrice.getId());
                gzprojectTime.setProjectManage(projectManage);
                totalDao.save(gzprojectTime);
                // 辅料录入
                List<ProjectTime> flTimeList = totalDao
                        .query("from ProjectTime where classNumber='fl' order by addDateTime desc");
                ProjectTime flprojectTime = new ProjectTime();
                if (flTimeList.size() > 0) {
                    for (ProjectTime time : flTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, flprojectTime, new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            flprojectTime.setUserId(time.getUserId());
                            flprojectTime.setUserName(time.getUserName());
                            flprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                flprojectTime.setClassName("辅料消耗");
                flprojectTime.setClassNumber("fl");
                // flprojectTime.setAddDateTime(datetime);
                flprojectTime.setAddUserName(loginUser.getName());
                flprojectTime.setLevel(6);
                flprojectTime.setProId(projectManage.getId());
                flprojectTime.setQuoId(quotedPrice.getId());
                flprojectTime.setProjectManage(projectManage);
                totalDao.save(flprojectTime);

                // 人工录入
                List<ProjectTime> rgTimeList = totalDao
                        .query("from ProjectTime where classNumber='rg' order by addDateTime desc");
                ProjectTime rgprojectTime = new ProjectTime();
                if (rgTimeList.size() > 0) {
                    for (ProjectTime time : rgTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, rgprojectTime, new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            rgprojectTime.setUserId(time.getUserId());
                            rgprojectTime.setUserName(time.getUserName());
                            rgprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                rgprojectTime.setClassName("人工成本");
                rgprojectTime.setClassNumber("rg");
                // rgprojectTime.setAddDateTime(datetime);
                rgprojectTime.setAddUserName(loginUser.getName());
                rgprojectTime.setLevel(7);
                rgprojectTime.setProId(projectManage.getId());
                rgprojectTime.setQuoId(quotedPrice.getId());
                rgprojectTime.setProjectManage(projectManage);
                totalDao.save(rgprojectTime);
                // 外购外委录入
                List<ProjectTime> wgwwTimeList = totalDao
                        .query("from ProjectTime where classNumber='wgww' order by addDateTime desc");
                ProjectTime wgwwprojectTime = new ProjectTime();
                if (wgwwTimeList.size() > 0) {
                    for (ProjectTime time : wgwwTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            wgwwprojectTime.setUserId(time.getUserId());
                            wgwwprojectTime.setUserName(time.getUserName());
                            wgwwprojectTime.setDept(time.getDept());
                            // BeanUtils.copyProperties(time, wgwwprojectTime,
                            // new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            break;
                        }
                    }
                }
                wgwwprojectTime.setClassName("外购外委");
                wgwwprojectTime.setClassNumber("wgww");
                // wgwwprojectTime.setAddDateTime(datetime);
                wgwwprojectTime.setAddUserName(loginUser.getName());
                wgwwprojectTime.setLevel(8);
                wgwwprojectTime.setProId(projectManage.getId());
                wgwwprojectTime.setQuoId(quotedPrice.getId());
                wgwwprojectTime.setProjectManage(projectManage);
                totalDao.save(wgwwprojectTime);
                // 能源消耗录入
                List<ProjectTime> nyxhTimeList = totalDao
                        .query("from ProjectTime where classNumber='nyxh' order by addDateTime desc");
                ProjectTime nyxhprojectTime = new ProjectTime();
                if (nyxhTimeList.size() > 0) {
                    for (ProjectTime time : nyxhTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, nyxhprojectTime,
                            // new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            nyxhprojectTime.setUserId(time.getUserId());
                            nyxhprojectTime.setUserName(time.getUserName());
                            nyxhprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                nyxhprojectTime.setClassName("能源消耗");
                nyxhprojectTime.setClassNumber("nyxh");
                // nyxhprojectTime.setAddDateTime(datetime);
                nyxhprojectTime.setAddUserName(loginUser.getName());
                nyxhprojectTime.setLevel(9);
                nyxhprojectTime.setProId(projectManage.getId());
                nyxhprojectTime.setQuoId(quotedPrice.getId());
                nyxhprojectTime.setProjectManage(projectManage);
                totalDao.save(nyxhprojectTime);
                // 财务、管理录入
                List<ProjectTime> cwglTimeList = totalDao
                        .query("from ProjectTime where classNumber='cwgl' order by addDateTime desc");
                ProjectTime cwglprojectTime = new ProjectTime();
                if (cwglTimeList.size() > 0) {
                    for (ProjectTime time : cwglTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, cwglprojectTime,
                            // new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            cwglprojectTime.setUserId(time.getUserId());
                            cwglprojectTime.setUserName(time.getUserName());
                            cwglprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                cwglprojectTime.setClassName("管理费、财务费");
                cwglprojectTime.setClassNumber("cwgl");
                cwglprojectTime.setAddDateTime(datetime);
                cwglprojectTime.setAddUserName(loginUser.getName());
                cwglprojectTime.setLevel(10);
                cwglprojectTime.setProId(projectManage.getId());
                cwglprojectTime.setQuoId(quotedPrice.getId());
                cwglprojectTime.setProjectManage(projectManage);
                totalDao.save(cwglprojectTime);
                // 包装运输录入
                List<ProjectTime> bzysTimeList = totalDao
                        .query("from ProjectTime where classNumber='bzys' order by addDateTime desc");
                ProjectTime bzysprojectTime = new ProjectTime();
                if (bzysTimeList.size() > 0) {
                    for (ProjectTime time : bzysTimeList) {
                        if (time.getProvisionTime() != null) {// 有指派时间表示已经被指派过
                            // BeanUtils.copyProperties(time, bzysprojectTime,
                            // new
                            // String[]{"id","addDateTime","addUserName","proId","quoId","projectManage"});
                            bzysprojectTime.setUserId(time.getUserId());
                            bzysprojectTime.setUserName(time.getUserName());
                            bzysprojectTime.setDept(time.getDept());
                            break;
                        }
                    }
                }
                bzysprojectTime.setClassName("包装运输");
                bzysprojectTime.setClassNumber("bzys");
                // bzysprojectTime.setAddDateTime(datetime);
                bzysprojectTime.setAddUserName(loginUser.getName());
                bzysprojectTime.setLevel(11);
                bzysprojectTime.setProId(projectManage.getId());
                bzysprojectTime.setQuoId(quotedPrice.getId());
                bzysprojectTime.setProjectManage(projectManage);
                totalDao.save(bzysprojectTime);
                // 工装费录入
                ProjectTime gzfprojectTime = new ProjectTime("直接工装", "gzf",
                        datetime, loginUser.getName(), 12, projectManage
                        .getId(), quotedPrice.getId(), projectManage);
                totalDao.save(gzfprojectTime);
            }
        }
    }

    /***
     * 添加工艺规范Tree
     *
     * @param projectManage
     */
    @Override
    public void add(QuotedPrice quotedPrice) {
        QuotedPrice fatherQuotedPrice = quotedPrice.getQuotedPrice();
        quotedPrice.setMarkId(quotedPrice.getMarkId().replaceAll(" ", ""));// 去除件号中的所有空格
        quotedPrice.setQuotedNumber(fatherQuotedPrice.getQuotedNumber());// 询价单号
        quotedPrice
                .setProcardLifeCycle(fatherQuotedPrice.getProcardLifeCycle());// 产品生命周期
        quotedPrice.setStatus("初始");// 状态
        quotedPrice.setProcardTime(Util.getDateTime());
        quotedPrice.setZhikaren(Util.getLoginUser().getName());
        quotedPrice.setBarcode(Util.getDateTime("yyyyMMddHHmmss"));
        quotedPrice.setProId(fatherQuotedPrice.getProId());// 项目id
        totalDao.save(quotedPrice);
        // 外购的计算方式
        if (quotedPrice.getProcardStyle().equals("外购")) {
            quotedPrice.setFilnalCount(fatherQuotedPrice.getFilnalCount()
                    * quotedPrice.getQuanzi1() / quotedPrice.getQuanzi2());// 根据权值计算数量
            //外购外委评审问题
            QuotedPrice totalqp = (QuotedPrice) totalDao.getObjectByCondition(" from QuotedPrice where id=?", quotedPrice.getRootId());
            if (totalqp == null || totalqp.getStatus() == null || totalqp.getStatus().equals("初始") || totalqp.getStatus().equals("BOM录入")) {

            } else {
                Users loginUser = Util.getLoginUser();
                OutSourcingApp osa = (OutSourcingApp) totalDao.getObjectByCondition("from OutSourcingApp where processNO='外购' and  markID=?", quotedPrice.getMarkId());
                if (quotedPrice.getWaigouPrice() == null) {//自动注入外购价格
                    String toDay = Util.getDateTime("yyyy-MM-dd");
                    Object wgPrice = totalDao.getObjectByCondition("select hsPrice from Price where partNumber = ? and pricePeriodStart<=? and pricePeriodEnd>=? order by hsPrice", quotedPrice.getMarkId(), toDay, toDay);
                    if (wgPrice != null) {
                        quotedPrice.setWaigouPrice((float) Double.parseDouble(wgPrice.toString()));
                    }
                }
                if (osa == null) {
                    osa = new OutSourcingApp();
                    osa.setDept(loginUser.getDept());
                    osa.setIsJiaji("否");
                    osa.setMarkID(quotedPrice.getMarkId());
                    osa.setProcessNO("外购");
                    osa.setDeliveryCount(quotedPrice.getFilnalCount());
                    osa.setQpId(quotedPrice.getId());
                    osa.setRootId(quotedPrice.getRootId());
                    osa.setExecuteStatus("周期录入");
                    osa.setStatus("申请");
                    osa.setDeliveryDate(DateUtil.formatDate(new Date(),
                            "yyyy-MM-dd"));
                    saveOSApp1(osa);
                    totalqp.setStatus("外购外委评审");
                    totalDao.update(totalqp);
                    return;
                } else {// 同件号已被评审过
                    if ((osa.getStatus() == null || !osa.getStatus().equals("评审完成")) || (osa.getAbilityLack() != null
                            && (osa.getAbilityLack().equals("工艺限制") || osa.getAbilityLack().equals("表面处理") || osa.getAbilityLack().equals("热处理") || osa
                            .getAbilityLack().equals("设备限制")))) {//没有评审完成或者产能不足的原因是上述之一
//								qp.setProcardStyle("外购");
//								b = b & totalDao.update(qp);
                    } else {
                        boolean bool = false;
                        if (osa.getAddChengMinBalanceCount() != null
                                && osa.getDeliveryCount() != null
                                && (osa.getDeliveryCount() - osa
                                .getAddChengMinBalanceCount()) > 0) {
                            bool = true;
                        } else if (osa.getAddChengMinBalanceCount() == null
                                && osa.getDeliveryCount() != null) {
                            bool = true;
                        }
                        if (bool) {// 如果外委交付数超过外委盈亏平衡数量，则外委外购变为自制
                            quotedPrice.setProcardStyle("自制");
                        }
                    }

                }
            }


        } else {
            quotedPrice.setFilnalCount(fatherQuotedPrice.getFilnalCount()
                    * quotedPrice.getCorrCount());// 根据权值计算数量
        }
        totalDao.update(quotedPrice);
    }

    /***
     * 删除核算信息
     *
     * @param QuotedPrice
     */
    @Override
    public void delQuotedPrice(QuotedPrice quotedPrice) {
        if (quotedPrice != null) {
            // 查询项目立项信息是否存在
            ProjectManage projectManage = (ProjectManage) totalDao
                    .getObjectById(ProjectManage.class, quotedPrice.getProId());
            if (projectManage != null) {
                // 删除核算信息
                totalDao.delete(quotedPrice);
                // 更新项目流程状态为 "立项"
                projectManage.setStatus("立项");
                totalDao.update(projectManage);
                // 清空立项以外的时间数据
                String hql = "delete ProjectTime where quoId=?";
                totalDao.createQueryUpdate(hql, null, quotedPrice.getId());
            }
        }
    }

    /***
     * 删除
     *
     * @param QuotedPrice
     */
    @Override
    public void del(QuotedPrice quotedPrice) {
        if (quotedPrice != null) {
            // 删除
            totalDao.delete(quotedPrice);
        }
    }

    /***
     * 查询核算信息(条件查询、分页)
     *
     * @param quotedPrice
     * @param pageNo
     * @param pageSize
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object[] findPMByCondition(QuotedPrice quotedPrice, int pageNo,
                                      int pageSize, String pageStatus) {
        Users user = Util.getLoginUser();
        if (quotedPrice == null) {
            quotedPrice = new QuotedPrice();
        }
        String hql = totalDao.criteriaQueries(quotedPrice, null, "proId");
        if (quotedPrice.getProId() != null && quotedPrice.getProId() > 0) {
            hql += " and proId=" + quotedPrice.getProId();
        }
        if (pageStatus == null || pageStatus.length() == 0 || ("all").equals(pageStatus)) {
            hql += " and (isbaomi is null or isbaomi!='是')";
        } else if ("baomi".equals(pageStatus)) {
            Integer uid = (Integer) totalDao.getObjectByCondition("select u.id from Users u join u.moduleFunction f  where u.id='" + user.getId() + "' and f.functionName in('项目核算填报(所有保密)','核算管理(所有保密)'	)");
            if (uid != null) {
                hql += " and isbaomi ='是'";
            } else {
                hql += " and isbaomi='是'  and ((id in (select quoId from ProjectTime where userId='" + user.getId() + "')) or proId in (select id from ProjectManage where userId=" + user.getId() + "))";
            }
        } else if ("baomiAll".equals(pageStatus)) {
            hql += " and isbaomi='是'";
        } else {
            if (pageStatus.indexOf("baomi") > 0) {
                String classNumber = pageStatus.replaceAll("baomi", "");
                Integer uid = (Integer) totalDao.getObjectByCondition("select u.id from Users u join u.moduleFunction f  where u.id='" + user.getId() + "' and f.functionName in('项目核算填报(所有保密)','核算管理(所有保密)'	)");
                if (uid != null) {
                    hql += " and isbaomi ='是' and id in (select quoId from ProjectTime where classNumber='"
                            + classNumber
                            + "' and (realTime is  null or realTime ='') and provisionTime is not null )  ";
                } else {
                    hql += " and isbaomi ='是' and id in (select quoId from ProjectTime where classNumber='"
                            + classNumber
                            + "' and (realTime is  null or realTime = '') and provisionTime is not null and userId='" + user.getId() + "')  ";
                }


            }else if("BOM".equals(pageStatus)){
            	hql+=" and (isbaomi is null or isbaomi !='是')";
            }  else {
                hql += " and (isbaomi is null or isbaomi !='是') and id in (select quoId from ProjectTime where classNumber='"
                        + pageStatus
                        + "' and (realTime is  null or realTime = '') and provisionTime is not null)  ";
            }
        }
        List list = totalDao.findAllByPage(hql + " and (dateStatus is null or (dateStatus !='历史' and dateStatus!='删除')) order by id desc", pageNo,
                pageSize);
        int count = totalDao.getCount(hql + " and (dateStatus is null or (dateStatus !='历史' and dateStatus!='删除')) ");
        Object[] o = {list, count};
        return o;
    }

    /***
     * 查询所有待录入的报价信息
     *
     * @param pageStatus
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List findNeedLuru(String pageStatus) {
        Users user = Util.getLoginUser();
        String hql = "from QuotedPrice where 1=1 ";
        if (pageStatus == null || pageStatus.length() == 0 || ("all").equals(pageStatus)) {
            hql += " and (isbaomi is null or isbaomi!='是') and id in (select quoId from ProjectTime where classNumber=? and (realTime is null or realTime='') and provisionTime is not null)";
        } else if ("baomi".equals(pageStatus)) {
            Integer uid = (Integer) totalDao.getObjectByCondition("select u.id from Users u join u.moduleFunction f  where u.id='" + user.getId() + "' and f.functionName in('项目核算填报(保密)','核算管理(保密)'	)");
            if (uid != null) {
                hql += " and isbaomi ='是' and id in (select quoId from ProjectTime where (realTime is null or realTime='') and provisionTime is not null)";
            } else {
                hql += " and isbaomi='是' and id in (select quoId from ProjectTime where  (realTime is null or realTime='') and provisionTime is not null)" +
                        "and ((id in (select quoId from ProjectTime where userId='" + user.getId() + "' )) " +//权限判定
                        "or (proId in (select id from ProjectManage where userId=" + user.getId() + ")))";
            }
        } else if ("baomiAll".equals(pageStatus)) {
            hql += " and isbaomi='是' and id in (select quoId from ProjectTime where  (realTime is null or realTime='') and provisionTime is not null)";
        } else {
            if (pageStatus.indexOf("baomi") > 0) {
                String classNumber = pageStatus.replaceAll("baomi", "");
                Integer uid = (Integer) totalDao.getObjectByCondition("select u.id from Users u join u.moduleFunction f  where u.id='" + user.getId() + "' and f.functionName in('项目核算填报(保密)','核算管理(保密)'	)");
                if (uid != null) {
                    hql += " and isbaomi ='是' and id in (select quoId from ProjectTime where (realTime is null or realTime='') and provisionTime is not null)";
                } else {
                    hql += " and isbaomi ='是' and id in (select quoId from ProjectTime where classNumber='"
                            + classNumber
                            + "' and (realTime is null or realTime='') and provisionTime is not null and userId='" + user.getId() + "')  ";
                }


            } else {
                hql += " and (isbaomi is null or isbaomi !='是') and id in (select quoId from ProjectTime where classNumber='"
                        + pageStatus
                        + "' and (realTime is null or realTime='') and provisionTime is not null)  ";
            }
        }


        return totalDao.query(hql);
    }

    /***
     * 查询核算信息
     *
     * @param id
     *            主键id
     */
    @Override
    public QuotedPrice afindQuotedPrice(Integer id) {
        if (id != null) {
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, id);
            if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                    Users loginUser = Util.getLoginUser();
                    BaomiOperateLog log = new BaomiOperateLog();
                    log.setOperateType("查看");//操作类型
                    log.setOperateObject("BOM");//操作对象
                    log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                            + qp.getMarkId() + "的BOM进行查看");//
                    log.setOperateTime(Util.getDateTime());//
                    log.setOperateUserId(loginUser.getId());
                    log.setOperateUsername(loginUser.getName());//
                    log.setOperateCode(loginUser.getCode());//
                    log.setOperateDept(loginUser.getDept());//
                    totalDao.save2(log);
                }
            }
            return qp;
        }
        return null;
    }

    /***
     * 修改
     *
     * @param QuotedPrice
     */
    @Override
    public void update(QuotedPrice quotedPrice) {
        if (quotedPrice != null) {
            // 根据父类以及权值更新数量
            if (quotedPrice.getFatherId() != null
                    && quotedPrice.getFatherId() > 0) {
                QuotedPrice qpFather = (QuotedPrice) totalDao.getObjectById(
                        QuotedPrice.class, quotedPrice.getFatherId());
                if (qpFather != null) {
                    if ("外购".equals(quotedPrice.getProcardStyle())) {
                        quotedPrice.setFilnalCount(qpFather.getFilnalCount()
                                * quotedPrice.getQuanzi2()
                                / quotedPrice.getQuanzi1());
                    } else {
                        quotedPrice.setFilnalCount(qpFather.getFilnalCount()
                                * quotedPrice.getCorrCount() / 1);
                    }
                }

            }
            totalDao.update1(quotedPrice);
            // 当零件为外购的组合是其下零件都外购
            if (quotedPrice.getProcardStyle() != null
                    && quotedPrice.getProcardStyle().equals("组合")
                    && quotedPrice.getYucailiaostatus() != null
                    && quotedPrice.getYucailiaostatus().equals("是")) {
                List<QuotedPrice> qpList = totalDao.query(
                        "from QuotedPrice where rootId=?", quotedPrice
                                .getRootId());
                if (qpList.size() > 0) {
                    Integer maxBelongLayer = 0;
                    for (QuotedPrice qp1 : qpList) {
                        if (qp1.getBelongLayer() != null
                                && qp1.getBelongLayer() > maxBelongLayer) {
                            maxBelongLayer = qp1.getBelongLayer();
                        }
                    }
                    List<Integer> updateIds = new ArrayList<Integer>();
                    updateIds.add(quotedPrice.getId());
                    for (int i = 0; i < maxBelongLayer; i++) {
                        for (QuotedPrice qp2 : qpList) {
                            if (qp2.getFatherId() != null
                                    && updateIds.contains(qp2.getFatherId())
                                    && !updateIds.contains(qp2.getId())) {
                                updateIds.add(qp2.getId());
                            }
                        }
                    }
                    for (QuotedPrice qp3 : qpList) {
                        if (updateIds.contains(qp3.getId())) {
                            qp3.setYucailiaostatus("是");
                            totalDao.update(qp3);
                        }
                    }
                }
            }
        }
    }

    /***
     * 修改核算信息
     *
     * @param QuotedPrice
     */
    @Override
    public void updateQuotedPrice(QuotedPrice quotedPrice) {
        if (quotedPrice != null) {
            totalDao.update(quotedPrice);
        }
    }

    /***
     * 根据rootId查询这个树形数据
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List findAllForTree(int rootId) {
        if (rootId > 0) {
            String hql = "from QuotedPrice where rootId=?";
            return totalDao.query(hql, rootId);
        }
        return null;
    }

    /***
     * 查询工艺规范单条明细
     *
     * @param id
     * @return
     */
    @Override
    public Object[] findQpDetailForShow(int id) {
        if ((Object) id != null && id > 0) {
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, id);
            if (qp != null) {
                // 下层流水卡片模板
                Set<QuotedPrice> pcSet = qp.getQuotedPriceSet();
                List<QuotedPrice> pclist = new ArrayList<QuotedPrice>();
                pclist.addAll(pcSet);
                // 对应工序信息
                Set<QuotedProcessInfor> pceSet = qp.getQpinfor();
                List<QuotedProcessInfor> pcelist = new ArrayList<QuotedProcessInfor>();
                pcelist.addAll(pceSet);

                return new Object[]{qp, pclist, pcelist, qp.getQuotedPrice()};
            }
        }
        return null;
    }

    /***
     * 添加工序
     *
     * @param qpInfor
     */
    @Override
    public void addQpInfor(QuotedProcessInfor qpInfor) {
        if (qpInfor != null) {
            QuotedPrice quotedPrice = afindQuotedPrice(qpInfor.getQuotedPrice().getId());
            if (quotedPrice != null) {
                qpInfor.setQuotedPrice(quotedPrice);
            } else {
                return;
            }
            // 处理设备报价信息
            if (qpInfor.getShebeiId() != null && qpInfor.getShebeiId() > 0) {
                Machine machine = (Machine) totalDao.getObjectById(
                        Machine.class, qpInfor.getShebeiId());
                if (machine != null) {
                    qpInfor.setShebeiNo(machine.getNo());
                    qpInfor.setShebeiName(machine.getName());
                    // 计算净值和剩余折旧年限
                    String buytime = machine.getBuytime();// 购买时间
                    if (buytime != null) {
                        SimpleDateFormat df = new SimpleDateFormat(
                                "yyyy-MM-dd 00:00:00");
                        Float Buyamount = machine.getBuyamount();// 购买金额
                        Long buydate;
                        Timestamp timestamp = Timestamp.valueOf(df
                                .format(new java.util.Date()));// 取出系统当前时间
                        Long date = Util.StringToDate(timestamp.toString(),
                                "yyyy-MM-dd").getTime();// 当前日期
                        buydate = Util.StringToDate(buytime, "yyyy-MM-dd")
                                .getTime();// 购买日期
                        if (Buyamount != null && !"".equals(buytime)) {
                            Integer depreciationYear = machine
                                    .getDepreciationYear();// 折旧年限
                            Long nYear = 1000 * 60 * 60 * 24 * 365L;// 转化为年
                            Long newYear = date - buydate;// 已用折旧时间
                            float year1 = newYear / nYear;// 转化为年
                            float oldYear = depreciationYear - year1;// 剩余折旧时间(转化为年)
                            float equipmentworth = (oldYear / depreciationYear)
                                    * Buyamount;// 设备净值
                            DecimalFormat myFormat = new DecimalFormat("0.00");//
                            // 设置float类型的小数只能为两位
                            String strFloat = myFormat.format(equipmentworth);
                            machine.setEquipmentworth(strFloat);

                            qpInfor.setShebeiJingzhi(equipmentworth);// 设备净值
                            qpInfor.setShebeiZjYear(oldYear);// 净值折旧剩余年限
                            // // 计算设备折旧
                            // Float shebeiZhejiu = qpInfor.getShebeiDateTime()
                            // / (3600 * 24 * 365 * qpInfor
                            // .getShebeiZjYear());
                            // // 去除E-10
                            // String a = shebeiZhejiu.toString();
                            // shebeiZhejiu = Float.parseFloat(a.substring(0, a
                            // .indexOf(".") + 4));
                            // shebeiZhejiu = Float.parseFloat(String.format(
                            // "%.2f", shebeiZhejiu));
                            //
                            // qpInfor.setShebeiZhejiu(shebeiZhejiu);// 设备折旧
                        }
                    }
                }
            }
            // -------------------设备处理完成
            // -------------------工装处理开始
            if (qpInfor.getGongzhuangId() != null
                    && qpInfor.getGongzhuangId() > 0) {
                Gzstore gzstore = (Gzstore) totalDao.getObjectById(
                        Gzstore.class, qpInfor.getGongzhuangId());
                if (gzstore != null) {
                    qpInfor.setOldgongzhuang(gzstore.getMatetag());// 工装名称
                    qpInfor.setOldgongzhuangNumber(gzstore.getNumber());// 工装编号
                    if (gzstore.getPrice() != null) {
                        qpInfor.setGongzhuangPrice(gzstore.getPrice());// 工装价格
                        // // 计算工装分摊
                        // QuotedPrice quotedPrice = qpInfor.getQuotedPrice();
                        // qpInfor
                        // .setGongzhuangFt(qpInfor.getGongzhuangPrice()
                        // / (quotedPrice.getProcardLifeCycle() * quotedPrice
                        // .getFilnalCount()));// 工装分摊=工装价格/(产品生命周期*数量)
                    }
                }
                qpInfor.setIsOld("yes");
            } else {
                qpInfor.setIsOld("no");
            }
            // -------------------工装处理结束
            //外购外委申请评审
            Users loginUser = Util.getLoginUser();
            QuotedPrice totalqp = (QuotedPrice) totalDao.getObjectByCondition(" from QuotedPrice where id=?", quotedPrice.getRootId());
            if (totalqp == null || totalqp.getStatus() == null || totalqp.getStatus().equals("初始") || totalqp.getStatus().equals("BOM录入")) {

            } else {
                OutSourcingApp osa = (OutSourcingApp) totalDao.getObjectByCondition("from OutSourcingApp where markID=? and processNO=?", quotedPrice.getMarkId(), qpInfor.getProcessNO() + "");
                if (osa == null) {//原来没有该外委评审
                    osa = new OutSourcingApp();
                    osa.setDept(loginUser.getDept());
                    osa.setIsJiaji("否");
                    osa.setMarkID(quotedPrice.getMarkId());
                    osa.setProcessNO(qpInfor.getProcessNO()
                            + "");
                    osa.setProcessName(qpInfor
                            .getProcessName());
                    osa.setDeliveryCount(quotedPrice.getFilnalCount());
                    osa.setRootId(quotedPrice.getRootId());
                    osa.setQpinfoId(quotedPrice.getId());
                    osa.setDeliveryDate(DateUtil.formatDate(
                            new Date(), "yyyy-MM-dd"));
                    Users user = (Users) Util.getLoginUser();
                    // 外委申请单编号（NO+部门编码+yyyy+“001”）
                    String deptNum = user.getPassword()
                            .getDeptNumber();
                    String no = "OSA" + deptNum
                            + Util.getDateTime("yyyy");
                    String hqlmaxNum = "select max(osaNO) from OutSourcingApp where osaNO like'"
                            + no + "%'";
                    List li = totalDao.find(hqlmaxNum);
                    String maxNum = no + "001";
                    if (null != li && li.size() > 0
                            && null != li.get(0)) {
                        String maxdata = (String) li.get(0);
                        maxNum = no
                                + haoAddOne(maxdata
                                .substring(maxdata
                                        .length() - 3));
                    }
                    osa.setOsaNO(maxNum);
                    osa.setCode(user.getCode());
                    osa.setUsername(user.getName());
                    osa.setOsaSubmitTime(totalDao
                            .getDateTime(null));
                    osa.setStatus("申请");
                    osa
                            .setNumber("osa"
                                    + deptNum
                                    + user.getCode()
                                    + Util
                                    .getDateTime("yyyyMMddHHmmss"));
                    osa.setExecuteStatus("周期录入");
                    totalDao.save(osa);
                    totalqp.setStatus("外购外委评审");
                    totalDao.update(totalqp);
                } else {
                    if ((osa.getStatus() == null || !osa.getStatus().equals("评审完成")) || (osa.getAbilityLack() != null
                            && (osa.getAbilityLack().equals("工艺限制") || osa.getAbilityLack().equals("表面处理") || osa.getAbilityLack().equals("热处理") || osa
                            .getAbilityLack().equals("设备限制")))) {//没有评审完成或者产能不足的原因是上述之一
//						qp.setProcardStyle("外购");
//						b = b & totalDao.update(qp);
                    } else {
                        boolean bool = false;
                        if (osa.getAddChengMinBalanceCount() != null
                                && osa.getDeliveryCount() != null
                                && (osa.getDeliveryCount() - osa
                                .getAddChengMinBalanceCount()) > 0) {
                            bool = true;
                        } else if (osa
                                .getAddChengMinBalanceCount() == null
                                && osa.getDeliveryCount() != null) {
                            bool = true;
                        }
                        if (bool) {// 如果外委交付数超过外委盈亏平衡数量，则外委外购变为自制
                            qpInfor.setProductStyle("自制");
                        }
                    }
                }
            }
            totalDao.save(qpInfor);
        }
    }

    /***
     * 删除工序
     *
     * @param qpInfor
     */
    @Override
    public void delQpInfor(QuotedProcessInfor qpInfor) {
        if (qpInfor != null) {
            totalDao.delete(qpInfor);
        }
    }

    /***
     *根据Id查询工序
     *
     * @param id
     */
    @Override
    public QuotedProcessInfor findQpInfor(Integer id) {
        if (id != null && id > 0) {
            return (QuotedProcessInfor) totalDao.getObjectById(
                    QuotedProcessInfor.class, id);
        }
        return null;
    }

    /***
     * 查询件号对应的所有工序信息
     *
     * @param fkId
     *            产品id
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List findQpInforByFkid(Integer fkId) {
        if (fkId != null && fkId > 0) {
            String hql = "from QuotedProcessInfor where quotedPrice.id=? order by processNO";
            return totalDao.query(hql, fkId);
        }
        return null;
    }

    /**
     * 材料费、外购、财务风水电气费 填报
     */
    @Override
    public void updateQuotedPriceForcl(QuotedPrice qp, String pageStatus) {
        if (qp != null && qp.getId() != null) {
            QuotedPrice oldQp = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, qp.getId());
            if (oldQp != null) {
                if (pageStatus != null && pageStatus.length() > 0) {
                    if ("cl".equals(pageStatus)) {// 材料费
                        oldQp.setLastMonthPrice(qp.getLastMonthPrice());// 上月招标价格
                        oldQp.setMaterialPrice(oldQp.getLastMonthPrice()
                                * oldQp.getMaterialXh());// 材料费（招标价格*材料消耗）

                        oldQp.setLastMonthPricesj(qp.getLastMonthPricesj());// 上月招标价格
                        oldQp.setMaterialPricesj(oldQp.getLastMonthPricesj()
                                * oldQp.getMaterialXhsj());// 材料费（招标价格*材料消耗）

                        oldQp.setLastMonthPricesz(qp.getLastMonthPricesz());// 上月招标价格
                        oldQp.setMaterialPricesz(oldQp.getLastMonthPricesz()
                                * oldQp.getMaterialXhsz());// 材料费（招标价格*材料消耗）


                    } else if ("wgww".equals(pageStatus)) {// 外购、工装
                        oldQp.setWaigouPrice(qp.getWaigouPrice());// 外购价格
                        oldQp.setWaigouPricesz(qp.getWaigouPricesz());// 外购价格
                        oldQp.setWaigouPricesj(qp.getWaigouPricesj());// 外购价格
                        oldQp.setGongzhuangFei(qp.getGongzhuangFei());// 工装费用
                    } else if ("nyxh".equals(pageStatus)) {// 财务风水电气费
                        oldQp.setShuiFei(qp.getShuiFei());
                        oldQp.setDianFei(qp.getDianFei());
                        oldQp.setFengFei(qp.getFengFei());
                        oldQp.setQitiFei(qp.getQitiFei());
                        oldQp.setShuiFeisz(qp.getShuiFeisz());
                        oldQp.setDianFeisz(qp.getDianFeisz());
                        oldQp.setFengFeisz(qp.getFengFeisz());
                        oldQp.setQitiFeisz(qp.getQitiFeisz());
                        oldQp.setShuiFeisj(qp.getShuiFeisj());
                        oldQp.setDianFeisj(qp.getDianFeisj());
                        oldQp.setFengFeisj(qp.getFengFeisj());
                        oldQp.setQitiFeisj(qp.getQitiFeisj());
                    } else if ("bzys".equals(pageStatus)) {// 包装运输
                        oldQp.setBaozhuangFei(qp.getBaozhuangFei());
                        oldQp.setYunshuFei(qp.getYunshuFei());
                        oldQp.setBaozhuangFeisz(qp.getBaozhuangFeisz());
                        oldQp.setYunshuFeisz(qp.getYunshuFeisz());
                        oldQp.setBaozhuangFeisj(qp.getBaozhuangFeisj());
                        oldQp.setYunshuFeisj(qp.getYunshuFeisj());
                    } else if ("cwgl".equals(pageStatus)) {// 财务、管理
                        oldQp.setManageFei(qp.getManageFei());
                        oldQp.setCaiwuFei(qp.getCaiwuFei());
                    }
                }
                totalDao.update(oldQp);
            }
        }
    }

    /**
     * 各部门录入各部分数据
     *
     * @param qpInfor
     * @param pageStatus (sb、gz、fl、rg)
     */
    @Override
    public void updateDeptLuru(QuotedProcessInfor qpInfor, String pageStatus) {
        if (qpInfor != null && qpInfor.getId() != null && qpInfor.getId() > 0) {
            QuotedProcessInfor oldQpInfor = (QuotedProcessInfor) totalDao
                    .getObjectById(QuotedProcessInfor.class, qpInfor.getId());
            if (oldQpInfor != null) {
                if (pageStatus != null && pageStatus.length() > 0) {
                    if ("sb".equals(pageStatus)) {
                        // 设备处理
                        // oldQpInfor.setShebeiNo(qpInfor.getShebeiNo());
                        // oldQpInfor.setShebeiName(qpInfor.getShebeiName());
                        oldQpInfor.setShebeiDateTime(qpInfor
                                .getShebeiDateTime());
                        oldQpInfor.setShebeiJingzhi(qpInfor.getShebeiJingzhi());// 设备净值
                        oldQpInfor.setShebeiZjYear(qpInfor.getShebeiZjYear());// 净值折旧剩余年限
                        // 计算设备折旧
                        Float shebeiZhejiu = qpInfor.getShebeiDateTime()
                                * oldQpInfor.getShebeiJingzhi()
                                / (3600 * 8 * 12 * 22.5f * qpInfor
                                .getShebeiZjYear());
                        // 去除E-10
                        // String a = shebeiZhejiu.toString();
                        // shebeiZhejiu = Float.parseFloat(a.substring(0, a
                        // .indexOf(".") + 4));
                        shebeiZhejiu = Float.parseFloat(String.format("%.4f",
                                shebeiZhejiu));

                        oldQpInfor.setShebeiZhejiu(shebeiZhejiu);// 设备折旧

                    } else if ("gz".equals(pageStatus)) {
                        // 工装处理
                        oldQpInfor.setGongzhuangNumber(qpInfor
                                .getGongzhuangNumber());// 工装编号
                        oldQpInfor.setGongzhuang(qpInfor.getGongzhuang());// 工装名称
                        if (qpInfor.getGongzhuangPrice() != null) {
                            oldQpInfor.setGongzhuangPrice(qpInfor
                                    .getGongzhuangPrice());// 工装价格
                            oldQpInfor.setIsFentan(qpInfor.getIsFentan());// 是否分摊
                            if ("yes".equals(oldQpInfor.getIsFentan())) {
                                // 计算工装分摊
                                QuotedPrice quotedPrice = oldQpInfor
                                        .getQuotedPrice();
                                oldQpInfor
                                        .setGongzhuangFt(oldQpInfor
                                                .getGongzhuangPrice()
                                                / (quotedPrice
                                                .getProcardLifeCycle() * quotedPrice
                                                .getFilnalCount()));// 工装分摊=工装价格/(产品生命周期*数量)
                                oldQpInfor.setGongzhuangFy(null);
                            } else {
                                oldQpInfor.setGongzhuangFy(oldQpInfor
                                        .getGongzhuangPrice());
                                oldQpInfor.setGongzhuangFt(null);
                            }
                        }
                    } else if ("fl".equals(pageStatus)) {
                        // 辅助材料处理
                        oldQpInfor.setFuliao(qpInfor.getFuliao());
                        oldQpInfor.setFuliaoxh(qpInfor.getFuliaoxh());
                        oldQpInfor.setFuliaoMoney(qpInfor.getFuliaoMoney());
                        oldQpInfor.setFuliaosz(qpInfor.getFuliaosz());
                        oldQpInfor.setFuliaoxhsz(qpInfor.getFuliaoxhsz());
                        oldQpInfor.setFuliaoMoneysz(qpInfor.getFuliaoMoneysz());
                        oldQpInfor.setFuliaosj(qpInfor.getFuliaosj());
                        oldQpInfor.setFuliaoxhsj(qpInfor.getFuliaoxhsj());
                        oldQpInfor.setFuliaoMoneysj(qpInfor.getFuliaoMoneysj());
                    } else if ("rg".equals(pageStatus)) {
                        // 人工处理
                        String code = qpInfor.getJiagongUserCode();
                        Users user = (Users) totalDao.getObjectByCondition(
                                "from Users where code=?", code);
                        if (user != null) {
                            // 开始计算人工费
                            oldQpInfor.setJiagongTime(qpInfor.getJiagongTime());
                            oldQpInfor.setJiagongUser(user.getName());
                            oldQpInfor.setJiagongUserCode(user.getCode());

                            Float workingHoursWages = 0F; // 工序工时工资
                            // 查询工资模版
                            String hql = "from WageStandard where code=? and standardStatus='默认' and processStatus='同意'";
                            List list = totalDao.query(hql, code);
                            WageStandard wageStandard = null;
                            if (list != null && list.size() > 0) {
                                wageStandard = (WageStandard) list.get(0);
                            }
                            // 计算员工工资
                            InsuranceGold insuranceGold = igs
                                    .findInsuranceGoldBylc(wageStandard
                                                    .getLocalOrField(), wageStandard
                                                    .getCityOrCountryside(),
                                            wageStandard.getPersonClass()); // 福利系数
                            workingHoursWages = workingHoursWages
                                    + (wageStandard.getGangweigongzi()
                                    + wageStandard.getSsBase()
                                    * (insuranceGold
                                    .getQYoldageInsurance()
                                    + insuranceGold
                                    .getQYmedicalInsurance()
                                    + insuranceGold
                                    .getQYunemploymentInsurance()
                                    + insuranceGold
                                    .getQYinjuryInsurance() + insuranceGold
                                    .getQYmaternityInsurance())
                                    / 100 + wageStandard.getGjjBase()
                                    * insuranceGold.getQYhousingFund()
                                    / 100);
                            Float basicWorkingHoursWages = workingHoursWages / 619200; // 工序中基本工时工资(619200=60*60*8*21.5)
                            Float shibei = qpInfor.getShebeiDateTime() == null ? 0
                                    : qpInfor.getShebeiDateTime();
                            Float rengong = basicWorkingHoursWages
                                    * (oldQpInfor.getJiagongTime() + shibei); // 人工费=基本工时工资*(加工节拍+设备节拍)
                            // 人工费计算王成
                            oldQpInfor.setRengongfei(rengong);
                        }

                    } else if ("wgww".equals(pageStatus)) {
                        // 外委价格
                        oldQpInfor.setWwPrice(qpInfor.getWwPrice());
                        oldQpInfor.setWwPricesz(qpInfor.getWwPricesz());
                        oldQpInfor.setWwPricesj(qpInfor.getWwPricesj());
                    }
                    totalDao.update(oldQpInfor);// 更新数据
                }
            }
        }
    }

    /***
     * 报价汇总
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List findAllPrice(Integer id) {
        String hql = "from ProjectTime where quoId=? and classNumber not in ('lx','hstb','bom') order by level";
        return totalDao.query(hql, id);
    }

    /***
     * 报价总额
     *
     * @return
     */
    @Override
    public Float findAllMoney(Integer id) {
        String hql = "select sum(money) from ProjectTime where quoId=? and classNumber !='gzf' ";
        Object obj = totalDao.getObjectByCondition(hql, id);
        if (obj != null) {
            return Float.parseFloat(obj.toString());
        } else {
            return 0F;
        }
    }

    /***
     * 根据项目id和类别查询时间表
     *
     * @param proId
     *            项目id
     * @param pageStatus
     *            类别
     * @return
     */
    @Override
    public ProjectTime findProjectTime(Integer quoId, String pageStatus) {
        String hql = "from ProjectTime where quoId=? and classNumber=?";
        if (pageStatus != null && pageStatus.indexOf("baomi") > 0) {
            String classNumber = pageStatus.replaceAll("baomi", "");
            return (ProjectTime) totalDao.getObjectByCondition(hql, quoId,
                    classNumber);
        } else if (pageStatus != null && (("baomi").equals(pageStatus) || ("baomiAll").equals(pageStatus))) {
            String classNumber = pageStatus.replaceAll("baomi", "");
            return (ProjectTime) totalDao.getObjectByCondition(hql, quoId,
                    classNumber);
        } else {
            return (ProjectTime) totalDao.getObjectByCondition(hql, quoId,
                    pageStatus);
        }
    }

    @Override
    public List findQpInforAll() {
        // TODO Auto-generated method stub
        String hql = "from QuotedProcessInfor";
        return totalDao.query(hql);
    }

    /***
     * 根据首层父类id查询流水卡片模板(组装树形结构)
     *
     * @param QuotedPrice
     * @return
     */
    @SuppressWarnings("unchecked")
    public List findQuotedPriceByRootId(int rootId) {
        if ((Object) rootId != null && rootId > 0) {
            String hql = "from QuotedPrice where rootId=?";
            return totalDao.query(hql, rootId);
        }
        return null;
    }

    /***
     *显示计算试算成品页面
     */
    @SuppressWarnings("unchecked")
    @Override
    public String packageData(Integer id, Map map) {
        int count = 1;
        DataGrid dg = new DataGrid();
        int length = 1;
        // 查询所有零组件信息
        String hql = "from QuotedPrice where rootId=? and procardStyle<>'外购'";
        List<QuotedPrice> quoTemList = totalDao.query(hql, id);
        length += quoTemList.size();
        for (QuotedPrice quoted : quoTemList) {
            // 组装零件信息
            VOProductTree part = new VOProductTree(count++, quoted.getMarkId(),
                    quoted.getProName(), 0D, null, quoted.getId());
            // 获得零件对应工序信息
            Set<QuotedProcessInfor> quotedProInSet = quoted.getQpinfor();
            if (quotedProInSet != null && quotedProInSet.size() > 0) {
                length += quotedProInSet.size();
                for (QuotedProcessInfor quotedProin : quotedProInSet) {
                    // 数据有效性效验
                    Double OPLabourBeat = 0.0; // 人工节拍
                    Double OPEquipmentBeat = 0.0; // 设备节拍
                    Double PRLabourBeat = 0.0; // 人工节拍
                    Double PRPrepareIndex = 0.0; // 准备次数
                    if (quotedProin.getOpcaozuojiepai() != null)
                        OPLabourBeat = quotedProin.getOpcaozuojiepai()
                                .doubleValue();
                    if (quotedProin.getOpshebeijiepai() != null)
                        OPEquipmentBeat = quotedProin.getOpshebeijiepai()
                                .doubleValue();
                    if (quotedProin.getGzzhunbeijiepai() != null)
                        PRLabourBeat = quotedProin.getGzzhunbeijiepai()
                                .doubleValue();
                    if (quotedProin.getGzzhunbeicishu() != null)
                        PRPrepareIndex = quotedProin.getGzzhunbeicishu()
                                .doubleValue();

                    VOProductTree process = null;
                    // 人工费
                    float rengongfei = 0;
                    if (quotedProin.getRengongfei() != null) {
                        rengongfei = quotedProin.getRengongfei();
                    }
                    // 设备折旧费
                    float shebeiZhejiu = 0;
                    if (quotedProin.getShebeiZhejiu() != null) {
                        shebeiZhejiu = quotedProin.getShebeiZhejiu();
                    }
                    double rgfei = rengongfei;
                    double sbZhejiu = shebeiZhejiu;
                    if (map != null && map.size() > 0) {
                        DTOProcess dto = (DTOProcess) map.get(quotedProin
                                .getId());
                        process = new VOProductTree(count++, quotedProin
                                .getProcessName(), quotedProin.getProcessNO()
                                .toString(), quotedProin.getShebeiNo(), dto
                                .getOPLabourBeat(), dto.getOPEquipmentBeat(),
                                dto.getPRLabourBeat(), dto.getPRPrepareTIme(),
                                dto.getHandlers(), rgfei, part.getId(),
                                quotedProin.getId(), "PR", dto.getSumMoney(),
                                dto.getUnitPrice(), dto.getJobNum(), sbZhejiu);
                    } else {
                        process = new VOProductTree(count++, quotedProin
                                .getProcessName(), quotedProin.getShebeiName(),
                                quotedProin.getShebeiNo(), OPLabourBeat,
                                OPEquipmentBeat, PRLabourBeat, PRPrepareIndex,
                                quotedProin.getJiagongUser(), rgfei, part
                                .getId(), quotedProin.getId(), "PR",
                                null, null, quotedProin.getJiagongUserCode(),
                                sbZhejiu);
                    }
                    part.getChildren().add(process);
                }
            }
            dg.getRows().add(part);
        }
        dg.setTotal(new Long(length));
        String jsonStr = JSON.toJSON(dg).toString();
        return jsonStr;
    }

    /**
     * @throws
     * @Title: trial
     * @DescriqpInfoion: 人工数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public String trial(Integer id) {
        Map map = trialMentioningAwardPrice(id);
        String url = null;
        // if (map != null && map.size() > 0) {
        // try {
        // url = packageData(id, null);
        // } catch (ExceqpInfoion e) {
        // e.printStackTrace();
        // }
        // }
        return url;
    }

    /**
     * @param id
     * @return Map
     * @throws
     * @Title: trialMentioningAwardPrice
     * @DescriqpInfoion: 试算
     */
    @SuppressWarnings("unchecked")
    public Map trialMentioningAwardPrice(Integer id) {
        Map session = ActionContext.getContext().getSession();
        List<QuotedPrice> quotedPriceSet = findQuotedPriceByRootId(id); // 根据成品ID查询组件
        Map map = new HashMap(); // 存储数据
        try {
            /**
             * 遍历算 所有
             */
            for (QuotedPrice quotedp : quotedPriceSet) {
                Set<QuotedProcessInfor> quoProSet = quotedp.getQpinfor(); // 此组件需要的工序
                /**
                 * 遍历一个组件所需要的工序
                 */
                for (QuotedProcessInfor quotedProInfor : quoProSet) {
                    DTOProcess dto = (DTOProcess) session.get(quotedProInfor
                            .getId()); // 从Session中取出存储的前台填写数据
                    String jobNumStr = null;
                    // 保存数据
                    DTOProcess newDto = null;
                    if (dto != null) {
                        jobNumStr = dto.getJobNum(); // 获取完成此工序的人员工号
                        if (jobNumStr == null || jobNumStr.length() <= 0) {
                            // 保存页面添加人员信息
                            quotedProInfor.setJiagongUser("");
                            quotedProInfor.setJiagongUserCode("");
                            // 保存参数信息
                            quotedProInfor.setOpcaozuojiepai(Float.valueOf(dto
                                    .getOPLabourBeat().toString()));
                            quotedProInfor.setOpshebeijiepai(Float.valueOf(dto
                                    .getOPEquipmentBeat().toString()));
                            quotedProInfor.setGzzhunbeijiepai(Float.valueOf(dto
                                    .getPRLabourBeat().toString()));
                            quotedProInfor.setGzzhunbeicishu(Float.valueOf(dto
                                    .getPRPrepareTIme().toString()));
                            // 清空记录
                            quotedProInfor.setRengongfei(0F);

                        }
                    } else {
                        jobNumStr = quotedProInfor.getJiagongUserCode(); // 获取完成此工序的人员工号
                    }
                    if (jobNumStr == null || jobNumStr.equals("")) {
                        // newDto = new DTOProcess(
                        // quotedProInfor.getId(),
                        // quotedProInfor.getJiagongUserCode(),
                        // quotedProInfor.getOpcaozuojiepai() != null ?
                        // quotedProInfor
                        // .getOpcaozuojiepai().doubleValue()
                        // : 0.0,
                        // quotedProInfor.getOpshebeijiepai() != null ?
                        // quotedProInfor
                        // .getOpshebeijiepai().doubleValue()
                        // : 0,
                        // quotedProInfor.getGzzhunbeijiepai() != null ?
                        // quotedProInfor
                        // .getGzzhunbeijiepai().doubleValue()
                        // : 0.0,
                        // quotedProInfor.getGzzhunbeicishu() != null ?
                        // quotedProInfor
                        // .getGzzhunbeicishu().doubleValue()
                        // : 0.0, quotedProInfor.getJiagongUser(),
                        // 0.0, 0.0);
                        // map.put(quotedProInfor.getId(), newDto);
                        // if (dto != null)
                        // session.remove(quotedProInfor.getId());
                        continue;
                    }
                    /** 计算人工费开始 **/
                    String[] allJobNum = jobNumStr.split(";");
                    Double workingHoursWages = 0.0; // 工序工时工资
                    // 处理工号(一个工序多个人员)
                    for (String jobNum : allJobNum) { // 统计工序中基本工时工资
                        WageStandard wageStandard = wss.findWSByUser(jobNum); // 根据工号查询工资模板
                        if (wageStandard == null) {
                            continue;
                        }
                        if (dto != null) {
                            // 保存页面添加人员信息
                            quotedProInfor.setJiagongUserCode(wageStandard
                                    .getCode());
                            quotedProInfor.setJiagongUser(wageStandard
                                    .getUserName());
                            // 保存参数信息
                            quotedProInfor.setOpcaozuojiepai(Float.valueOf(dto
                                    .getOPLabourBeat().toString()));
                            quotedProInfor.setOpshebeijiepai(Float.valueOf(dto
                                    .getOPEquipmentBeat().toString()));
                            quotedProInfor.setGzzhunbeijiepai(Float.valueOf(dto
                                    .getPRLabourBeat().toString()));
                            quotedProInfor.setGzzhunbeicishu(Float.valueOf(dto
                                    .getPRPrepareTIme().toString()));
                            quotedProInfor.setJiagongTime(quotedProInfor
                                    .getOpcaozuojiepai()
                                    + quotedProInfor.getOpshebeijiepai()
                                    + quotedProInfor.getGzzhunbeicishu()
                                    * quotedProInfor.getGzzhunbeijiepai());// 加工时长
                        }

                        InsuranceGold insuranceGold = igs
                                .findInsuranceGoldBylc(wageStandard
                                        .getLocalOrField(), wageStandard
                                        .getCityOrCountryside(), wageStandard
                                        .getPersonClass()); // 福利系数
                        workingHoursWages = workingHoursWages
                                + (wageStandard.getGangweigongzi()
                                + wageStandard.getSsBase()
                                * (insuranceGold.getQYoldageInsurance()
                                + insuranceGold
                                .getQYmedicalInsurance()
                                + insuranceGold
                                .getQYunemploymentInsurance()
                                + insuranceGold
                                .getQYinjuryInsurance() + insuranceGold
                                .getQYmaternityInsurance())
                                / 100 + wageStandard.getGjjBase()
                                * insuranceGold.getQYhousingFund()
                                / 100);
                    }
                    Double basicWorkingHoursWages = workingHoursWages / SECONDS; // 工序中基本工时工资(每秒工资)
                    double processWages = 0.0;
                    if (dto != null) {
                        processWages = basicWorkingHoursWages
                                * ((dto.getOPLabourBeat() + dto
                                .getOPEquipmentBeat()) + (dto
                                .getPRLabourBeat() * dto
                                .getPRPrepareTIme())); // 人工费（加工时长*秒工资）
                    } else {
                        processWages = basicWorkingHoursWages
                                * ((quotedProInfor.getOpcaozuojiepai() + quotedProInfor
                                .getOpshebeijiepai()) + (quotedProInfor
                                .getGzzhunbeijiepai() * quotedProInfor
                                .getGzzhunbeicishu())); // 基本工时工资(单个工序工资)
                    }
                    float rgfei = (float) processWages;
                    quotedProInfor.setRengongfei(rgfei);// 人工费
                    /** 计算人工费结束 **/
                    /** 计算设备折旧费开始 **/
                    // 计算设备折旧(设备节拍/(年秒数*净值折旧剩余年限)*设备净值)
                    quotedProInfor.setShebeiZhejiu(0F);// 设备折旧
                    try {
                        if (quotedProInfor.getShebeiZjYear() != null
                                && quotedProInfor.getShebeiZjYear() > 0
                                && quotedProInfor.getShebeiJingzhi() != null) {
                            Float shebeiZhejiu = quotedProInfor
                                    .getOpshebeijiepai()
                                    * quotedProInfor.getShebeiJingzhi()
                                    / (3600 * 8 * 12 * 22.5f * quotedProInfor
                                    .getShebeiZjYear());

                            // Float shebeiZhejiu = quotedProInfor
                            // .getOpshebeijiepai()
                            // / (3600 * 24 * 365 * quotedProInfor
                            // .getShebeiZjYear())
                            // * quotedProInfor.getShebeiJingzhi();
                            if (shebeiZhejiu > 0) {
                                shebeiZhejiu = Float.parseFloat(String.format(
                                        "%.4f", shebeiZhejiu));
                                quotedProInfor.setShebeiZhejiu(shebeiZhejiu);// 设备折旧
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /** 计算设备折旧费结束 **/
                    if (dto != null)
                        session.remove(quotedProInfor.getId());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /****
     * 查询工序
     *
     * @param qpInforId
     * @return
     */
    @Override
    public QuotedProcessInfor showProcess(Integer qpInforId) {
        if (qpInforId != null) {
            return (QuotedProcessInfor) totalDao.getObjectById(
                    QuotedProcessInfor.class, qpInforId);
        }
        return null;
    }

    /****
     * 修改工序
     *
     * @param qpInforId
     * @return
     */
    @Override
    public void updateProcess(QuotedProcessInfor qpInfor) {
        if (qpInfor != null) {
            QuotedProcessInfor oldqpInfor = (QuotedProcessInfor) totalDao
                    .getObjectById(QuotedProcessInfor.class, qpInfor.getId());
            if (oldqpInfor != null) {
                // 处理设备报价信息
                if (qpInfor.getShebeiId() != null && qpInfor.getShebeiId() > 0) {
                    Machine machine = (Machine) totalDao.getObjectById(
                            Machine.class, qpInfor.getShebeiId());
                    if (machine != null) {
                        oldqpInfor.setShebeiNo(machine.getNo());
                        oldqpInfor.setShebeiName(machine.getName());
                        // 计算净值和剩余折旧年限
                        String buytime = machine.getBuytime();// 购买时间
                        if (buytime != null) {
                            SimpleDateFormat df = new SimpleDateFormat(
                                    "yyyy-MM-dd 00:00:00");
                            Float Buyamount = machine.getBuyamount();// 购买金额
                            Long buydate;
                            Timestamp timestamp = Timestamp.valueOf(df
                                    .format(new java.util.Date()));// 取出系统当前时间
                            Long date = Util.StringToDate(timestamp.toString(),
                                    "yyyy-MM-dd").getTime();// 当前日期
                            buydate = Util.StringToDate(buytime, "yyyy-MM-dd")
                                    .getTime();// 购买日期
                            if (Buyamount != null && !"".equals(buytime)) {
                                Integer depreciationYear = machine
                                        .getDepreciationYear();// 折旧年限
                                Long nYear = 1000 * 60 * 60 * 24 * 365L;// 转化为年
                                Long newYear = date - buydate;// 已用折旧时间
                                float year1 = newYear / nYear;// 转化为年
                                float oldYear = depreciationYear - year1;// 剩余折旧时间(转化为年)
                                float equipmentworth = (oldYear / depreciationYear)
                                        * Buyamount;// 设备净值
                                DecimalFormat myFormat = new DecimalFormat(
                                        "0.00");//
                                // 设置float类型的小数只能为两位
                                String strFloat = myFormat
                                        .format(equipmentworth);
                                machine.setEquipmentworth(strFloat);

                                oldqpInfor.setShebeiJingzhi(equipmentworth);// 设备净值
                                oldqpInfor.setShebeiZjYear(oldYear);// 净值折旧剩余年限
                            }
                        }
                    }
                }

                // -------------------工装处理开始
                if (qpInfor.getIsOld().equals("yes")) {
                    Gzstore gzstore = (Gzstore) totalDao.getObjectById(
                            Gzstore.class, qpInfor.getGongzhuangId());
                    if (gzstore != null) {
                        oldqpInfor.setGongzhuangNumber(gzstore.getNumber());// 工装编号
                        oldqpInfor.setGongzhuang(gzstore.getMatetag());// 工装名称
                        if (gzstore.getPrice() != null) {
                            oldqpInfor.setGongzhuangPrice(gzstore.getPrice());// 工装价格
                        }
                    }
                    oldqpInfor.setIsOld("yes");
                } else {
                    oldqpInfor.setGongzhuangId(null);
                    oldqpInfor.setGongzhuangNumber(qpInfor
                            .getGongzhuangNumber());// 工装编号
                    oldqpInfor.setGongzhuang(qpInfor.getGongzhuang());// 工装名称
                    oldqpInfor.setIsOld("no");
                }
                oldqpInfor.setProcessNO(qpInfor.getProcessNO());
                oldqpInfor.setProcessName(qpInfor.getProcessName());
                //处理外购外委评审问题
                if (oldqpInfor.getProductStyle() != null && oldqpInfor.getProductStyle().equals("自制")
                        && qpInfor.getProductStyle() != null && qpInfor.getProductStyle().equals("外委")) {
                    Users loginUser = Util.getLoginUser();
                    QuotedPrice quotedPrice = oldqpInfor.getQuotedPrice();
                    QuotedPrice totalqp = (QuotedPrice) totalDao.getObjectByCondition(" from QuotedPrice where id=?", quotedPrice.getRootId());
                    if (totalqp == null || totalqp.getStatus() == null || totalqp.getStatus().equals("初始") || totalqp.getStatus().equals("BOM录入")) {
                        oldqpInfor.setProductStyle(qpInfor.getProductStyle());
                    } else {
                        OutSourcingApp osa = (OutSourcingApp) totalDao.getObjectByCondition("from OutSourcingApp where markID=? and processNO=?", quotedPrice.getMarkId(), qpInfor.getProcessNO() + "");
                        if (osa == null) {//原来没有该外委评审
                            osa = new OutSourcingApp();
                            osa.setDept(loginUser.getDept());
                            osa.setIsJiaji("否");
                            osa.setMarkID(quotedPrice.getMarkId());
                            osa.setProcessNO(qpInfor.getProcessNO()
                                    + "");
                            osa.setProcessName(qpInfor
                                    .getProcessName());
                            osa.setDeliveryCount(quotedPrice.getFilnalCount());
                            osa.setRootId(quotedPrice.getRootId());
                            osa.setQpinfoId(quotedPrice.getId());
                            osa.setDeliveryDate(DateUtil.formatDate(
                                    new Date(), "yyyy-MM-dd"));
                            Users user = (Users) Util.getLoginUser();
                            // 外委申请单编号（NO+部门编码+yyyy+“001”）
                            String deptNum = user.getPassword()
                                    .getDeptNumber();
                            String no = "OSA" + deptNum
                                    + Util.getDateTime("yyyy");
                            String hqlmaxNum = "select max(osaNO) from OutSourcingApp where osaNO like'"
                                    + no + "%'";
                            List li = totalDao.find(hqlmaxNum);
                            String maxNum = no + "001";
                            if (null != li && li.size() > 0
                                    && null != li.get(0)) {
                                String maxdata = (String) li.get(0);
                                maxNum = no
                                        + haoAddOne(maxdata
                                        .substring(maxdata
                                                .length() - 3));
                            }
                            osa.setOsaNO(maxNum);
                            osa.setCode(user.getCode());
                            osa.setUsername(user.getName());
                            osa.setOsaSubmitTime(totalDao
                                    .getDateTime(null));
                            osa.setStatus("申请");
                            osa
                                    .setNumber("osa"
                                            + deptNum
                                            + user.getCode()
                                            + Util
                                            .getDateTime("yyyyMMddHHmmss"));
                            osa.setExecuteStatus("周期录入");
                            totalDao.save(osa);
                            totalqp.setStatus("外购外委评审");
                            totalDao.update(totalqp);
                            oldqpInfor.setProductStyle(qpInfor.getProductStyle());
                        } else {
                            if ((osa.getStatus() == null || !osa.getStatus().equals("评审完成")) || (osa.getAbilityLack() != null
                                    && (osa.getAbilityLack().equals("工艺限制") || osa.getAbilityLack().equals("表面处理") || osa.getAbilityLack().equals("热处理") || osa
                                    .getAbilityLack().equals("设备限制")))) {//没有评审完成或者产能不足的原因是上述之一
//								qp.setProcardStyle("外购");
//								b = b & totalDao.update(qp);
                                oldqpInfor.setProductStyle(qpInfor.getProductStyle());
                            } else {
                                boolean bool = false;
                                if (osa.getAddChengMinBalanceCount() != null
                                        && osa.getDeliveryCount() != null
                                        && (osa.getDeliveryCount() - osa
                                        .getAddChengMinBalanceCount()) > 0) {
                                    bool = true;
                                } else if (osa
                                        .getAddChengMinBalanceCount() == null
                                        && osa.getDeliveryCount() != null) {
                                    bool = true;
                                }
                                if (bool) {// 如果外委交付数超过外委盈亏平衡数量，则外委外购变为自制
                                    oldqpInfor.setProductStyle("自制");
                                }
                            }
                        }
                    }
                } else {
                    oldqpInfor.setProductStyle(qpInfor.getProductStyle());
                }
                totalDao.update(oldqpInfor);

            }

        }
    }

    public WageStandardServer getWss() {
        return wss;
    }

    public void setWss(WageStandardServer wss) {
        this.wss = wss;
    }

    @Override
    public boolean updateStartProject(int id) {
        // TODO Auto-generated method stub
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                QuotedPrice.class, id);
        if (qp != null) {
            qp.setStatus("项目启动中");
            return totalDao.update(qp);
        }
        return false;
    }

    @Override
    public boolean updateSubmitProject(int rootId) {
        // TODO Auto-generated method stub
        List<QuotedPrice> qpList = totalDao
                .query("from QuotedPrice where rootId=" + rootId);
        if (qpList.size() > 0) {
            for (QuotedPrice qp : qpList) {
                if (qp.getId().equals(rootId)
                        && (qp.getStatus() == null || !qp.getStatus().equals(
                        "项目启动中"))) {
                    return false;
                } else if (qp.getProductStyle() != null
                        && !qp.getProcardStyle().equals("外购")) {
                    Set<QuotedProcessInfor> qpInfoSet = qp.getQpinfor();
                    if (qpInfoSet.size() > 0) {
                        for (QuotedProcessInfor qpInfo : qpInfoSet) {
                            if (qpInfo.getGongzhuangId() == null
                                    && ((qpInfo.getGongzhuang() != null && !qpInfo.getGongzhuang().equals(""))
                                    || (qpInfo.getGongzhuangNumber() != null && !qpInfo.getGongzhuangNumber().equals("")))
                                    && (qpInfo.getOldgongzhuang() == null || qpInfo
                                    .getOldgongzhuangNumber() == null)) {
                                return false;
                            }
                        }
                    }

                }
            }
            QuotedPrice qp1 = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, rootId);
            qp1.setStatus("项目跟踪");
            return totalDao.update(qp1);
        }
        return false;
    }

    @Override
    public List<QuotedPriceLog> getQuotedPriceLog(int rootId) {
        // TODO Auto-generated method stub
        List<QuotedPriceLog> list = totalDao
                .query("from QuotedPriceLog where rootId=" + rootId);
        return list;
    }

    @Override
    public boolean addQuotedPriceLog(QuotedPriceLog quotedPriceLog) {
        // TODO Auto-generated method stub
        return totalDao.save(quotedPriceLog);
    }

    @Override
    public boolean deleteQPLogByid(Integer id) {
        // TODO Auto-generated method stub
        QuotedPriceLog qlog = getQpLogById(id);
        if (qlog != null) {
            return totalDao.delete(qlog);
        }
        return false;
    }

    @Override
    public QuotedPriceLog getQpLogById(Integer id) {
        // TODO Auto-generated method stub
        return (QuotedPriceLog) totalDao
                .getObjectById(QuotedPriceLog.class, id);
    }

    @Override
    public boolean updateQuotedPriceLog(QuotedPriceLog quotedPriceLog) {
        // TODO Auto-generated method stub
        if (quotedPriceLog != null) {
            QuotedPriceLog qpLog = getQpLogById(quotedPriceLog.getId());
            if (qpLog != null) {
                qpLog.setTitle(quotedPriceLog.getTitle());
                qpLog.setMsg(quotedPriceLog.getMsg());
                qpLog.setTime(qpLog.getTime() + "," + quotedPriceLog.getTime());
                if (quotedPriceLog.getAccessory() != null) {
                    qpLog.setAccessory(quotedPriceLog.getAccessory());
                }
                qpLog.setMoney(quotedPriceLog.getMoney());
                return totalDao.update(qpLog);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QuotedPrice> getAllNames(String markId) {
        // TODO Auto-generated method stub
        if (markId != null) {
            List<QuotedPrice> qlist1 = totalDao
                    .query("from QuotedPrice where markId like '%" + markId
                            + "%'");
            if (qlist1.size() > 0) {
                List<QuotedPrice> qlist2 = new ArrayList<QuotedPrice>();
                List<String> markIdList = new ArrayList<String>();
                int count = 0;
                for (QuotedPrice q1 : qlist1) {
                    if (q1.getMarkId() != null
                            && !markIdList.contains(q1.getMarkId())) {
                        markIdList.add(q1.getMarkId());
                        qlist2.add(q1);
                        count++;
                        if (count == 10) {// 只取前十条
                            break;
                        }
                    }
                }
                return qlist2;
            }
        }
        return null;
    }

    @Override
    public QuotedPrice findQuotedPriceByMarkId(String markId) {
        // TODO Auto-generated method stub
        if (markId != null) {
            List<QuotedPrice> qpList = totalDao
                    .query("from QuotedPrice where markId like '%" + markId
                            + "%'");
            if (qpList.size() > 0) {
                return qpList.get(0);
            }
        }
        return null;
    }

    @Override
    public boolean saveCopyProcess(QuotedPrice qp1, QuotedPrice qp2) {
        // TODO Auto-generated method stub
        QuotedPrice oldqp1 = (QuotedPrice) totalDao.getObjectById(
                QuotedPrice.class, qp1.getId());
        QuotedPrice oldqp2 = (QuotedPrice) totalDao.getObjectById(
                QuotedPrice.class, qp2.getId());
        if (oldqp1 != null && oldqp2 != null) {
            Set<QuotedProcessInfor> qpInfoSet1 = oldqp1.getQpinfor();
            Set<QuotedProcessInfor> qpInfoSet2 = oldqp2.getQpinfor();
            if (qpInfoSet2 == null) {
                return true;
            } else if (qpInfoSet1 == null) {
                for (QuotedProcessInfor qpInfo : qpInfoSet2) {
                    QuotedProcessInfor newqpInfo = new QuotedProcessInfor();
                    BeanUtils.copyProperties(qpInfo, newqpInfo, new String[]{
                            "id", "quotedPrice"});
                    newqpInfo.setQuotedPrice(oldqp1);
                    qpInfoSet1.add(newqpInfo);
                    oldqp1.setQpinfor(qpInfoSet1);
                    totalDao.save(newqpInfo);
                }

            } else {
                boolean b = true;
                Set<QuotedProcessInfor> qpInfoSet22 = new HashSet<QuotedProcessInfor>();
                for (QuotedProcessInfor qp22 : qpInfoSet2) {
                    for (QuotedProcessInfor qp11 : qpInfoSet1) {
                        if (qp11.getProcessNO() != null
                                && qp22.getProcessNO() != null
                                && qp11.getProcessNO().equals(
                                qp22.getProcessNO())) {

                        } else {
                            qpInfoSet22.add(qp22);
                        }
                    }
                }
                for (QuotedProcessInfor qp22 : qpInfoSet22) {
                    QuotedProcessInfor newqpInfo = new QuotedProcessInfor();
                    BeanUtils.copyProperties(qp22, newqpInfo, new String[]{
                            "id", "quotedPrice"});
                    newqpInfo.setQuotedPrice(oldqp1);
                    qpInfoSet1.add(newqpInfo);
                    oldqp1.setQpinfor(qpInfoSet1);
                    b = b & totalDao.save(newqpInfo);
                }
                return b;
            }

        }
        return false;
    }

    public boolean saveCopyProcess(QuotedPrice qp1, ProcardTemplate pt) {
        // TODO Auto-generated method stub
        QuotedPrice oldqp1 = qp1;
        if (qp1.getId() != null) {
            oldqp1 = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, qp1.getId());
        }
        Double rengongFei =0d;
        ProcardTemplate oldqp2 = (ProcardTemplate) totalDao.getObjectById(
                ProcardTemplate.class, pt.getId());
        if (oldqp1 != null && oldqp2 != null) {
        	Float corrcount =	getcgNum(oldqp2, 1f, oldqp2.getBelongLayer());
            Set<QuotedProcessInfor> qpInfoSet1 = oldqp1.getQpinfor();
            Set<ProcessTemplate> qpInfoSet2 = oldqp2.getProcessTemplate();
            if (qpInfoSet2 == null) {
                return true;
            } else if (qpInfoSet1 == null) {
            	qpInfoSet1 = new HashSet<QuotedProcessInfor>(); 
                for (ProcessTemplate qpInfo : qpInfoSet2) {
                    QuotedProcessInfor newqpInfo = new QuotedProcessInfor();
                    BeanUtils.copyProperties(qpInfo, newqpInfo, new String[]{
                            "id", "quotedPrice"});
                    if(newqpInfo.getProcessjjMoney()!=null
                    		&& newqpInfo.getProcessjjMoney()>0
                    		&& newqpInfo.getProcesdianshu()!=null
                    		&& newqpInfo.getProcesdianshu()>0){
                    	rengongFei +=(newqpInfo.getProcessjjMoney()*newqpInfo.getProcesdianshu()
                    			* corrcount);
                    }
                    newqpInfo.setQuotedPrice(oldqp1);
                    qpInfoSet1.add(newqpInfo);
                    oldqp1.setQpinfor(qpInfoSet1);
                    totalDao.save(newqpInfo);
                }
                oldqp1.setRengongFei(rengongFei.floatValue());
                totalDao.update(oldqp1);
            } else {
            	rengongFei = oldqp1.getRengongFei().doubleValue();
                boolean b = true;
                Set<ProcessTemplate> qpInfoSet22 = new HashSet<ProcessTemplate>();
                for (ProcessTemplate qp22 : qpInfoSet2) {
                    for (QuotedProcessInfor qp11 : qpInfoSet1) {
                        if (qp11.getProcessNO() != null
                                && qp22.getProcessNO() != null
                                && qp11.getProcessNO().equals(
                                qp22.getProcessNO())) {

                        } else {
                            qpInfoSet22.add(qp22);
                        }
                    }
                }
                for (ProcessTemplate qp22 : qpInfoSet22) {
                    QuotedProcessInfor newqpInfo = new QuotedProcessInfor();
                    BeanUtils.copyProperties(qp22, newqpInfo, new String[]{
                            "id", "quotedPrice"});
                    if(newqpInfo.getProcessjjMoney()!=null
                    		&& newqpInfo.getProcessjjMoney()>0
                    		&& newqpInfo.getProcesdianshu()!=null
                    		&& newqpInfo.getProcesdianshu()>0){
                    	rengongFei +=(newqpInfo.getProcessjjMoney()*newqpInfo.getProcesdianshu()
                    			* corrcount);
                    }
                    newqpInfo.setQuotedPrice(oldqp1);
                    qpInfoSet1.add(newqpInfo);
                    oldqp1.setQpinfor(qpInfoSet1);
                    b = b & totalDao.save(newqpInfo);
                    oldqp1.setRengongFei(rengongFei.floatValue());
                    totalDao.update(oldqp1);
                }
                return b;
            }

        }
        return false;
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
		if (sonProcardT.getFatherId() != null) {
			ProcardTemplate fatherProcardT = (ProcardTemplate) totalDao.get(
					ProcardTemplate.class, sonProcardT.getFatherId());
			cgNum = getcgNum(fatherProcardT, cgNum, belongLayer);
		}
		return cgNum;
	}
    @Override
    public boolean saveCopyQuotedPrice(QuotedPrice qp1, QuotedPrice qp2) {
        // TODO Auto-generated method stub
        if (qp1 != null && qp2 != null) {
            // 获取主副两个对象的持久层
            QuotedPrice oldqp1 = null;
            QuotedPrice oldqp2 = null;
            if (qp1.getQuotedPrice() != null) {// 说明qp1是在server层中产生的不是从action层中传过来的
                oldqp1 = qp1;
            } else {
                oldqp1 = (QuotedPrice) totalDao.getObjectById(
                        QuotedPrice.class, qp1.getId());
            }
            if (qp2.getQuotedPrice() != null) {// 说明qp2是在server层中产生的不是从action层中传过来的
                oldqp2 = qp2;
            } else {
                oldqp2 = (QuotedPrice) totalDao.getObjectById(
                        QuotedPrice.class, qp2.getId());
            }
            boolean b = true;
            if (oldqp1 != null && oldqp2 != null) {
                if (oldqp2.getProcardStyle() != null
                        && oldqp2.getProcardStyle().equals("总成")) {
                    // 如果是总成
                    Set<QuotedPrice> qpSet = oldqp2.getQuotedPriceSet();
                    if (qpSet != null && qpSet.size() > 0) {
                        Set<QuotedPrice> qpInfoSet2 = new HashSet<QuotedPrice>();
                        for (QuotedPrice qpInfo : qpSet) {
                            qpInfoSet2.add(qpInfo);
                        }
                        for (QuotedPrice qp : qpInfoSet2) {// 用qpInfoSet2循环是因为在下面的save操作会改变qpInfoSet的size
                            b = b & saveCopyQuotedPrice(qp1, qp);
                        }
                    }

                } else {
                    // 复制QuotedPrice
                    QuotedPrice newqp2 = new QuotedPrice();
                    // newqp2.setMarkId("123");
                    BeanUtils.copyProperties(qp2, newqp2, new String[]{"id",
                            "quotedPrice", "quotedPriceSet", "qpinfor"});
                    newqp2.setId(null);
                    newqp2.setRootId(oldqp1.getRootId());
                    newqp2.setFatherId(oldqp1.getId());
                    if (oldqp1.getBelongLayer() != null) {
                        newqp2.setBelongLayer(oldqp1.getBelongLayer() + 1);
                    }
                    Set<QuotedPrice> qpSet = oldqp2.getQuotedPriceSet();
                    Set<QuotedPrice> qpSet2 = new HashSet<QuotedPrice>();
                    if (qpSet.size() > 0) {
                        for (QuotedPrice qp : qpSet) {
                            qpSet2.add(qp);
                        }
                    }
                    // 复制QuotedProcessInfor
                    Set<QuotedProcessInfor> qpInfoSet = oldqp2.getQpinfor();
                    if (qpInfoSet != null && qpInfoSet.size() > 0) {
                        Set<QuotedProcessInfor> newqpInfoSet = new HashSet<QuotedProcessInfor>();
                        for (QuotedProcessInfor process : qpInfoSet) {
                            QuotedProcessInfor p = new QuotedProcessInfor();
                            BeanUtils.copyProperties(process, p, new String[]{
                                    "id", "quotedPrice"});
                            newqpInfoSet.add(p);
                        }
                        newqp2.setQpinfor(newqpInfoSet);
                    }
                    newqp2.setQuotedPrice(oldqp1);
                    Set<QuotedPrice> oldqp1qpSet = oldqp1.getQuotedPriceSet();
                    if (oldqp1qpSet != null && oldqp1qpSet.size() > 0) {
                        for (QuotedPrice p : oldqp1qpSet) {
                            if (p.getMarkId() != null
                                    && newqp2.getMarkId() != null
                                    && p.getMarkId().equals(newqp2.getMarkId())) {
                                // 零件下已含有被复制的零件模板
                                return true;
                            }
                        }
                        oldqp1qpSet.add(newqp2);
                    }
                    oldqp1.setQuotedPriceSet(oldqp1qpSet);
                    b = b & totalDao.save(newqp2);
                    if (b & qpSet.size() > 0) {
                        for (QuotedPrice qp : qpSet2) {
                            saveCopyQuotedPrice(newqp2, qp);
                        }
                    }
                }
                return b;
            }
        }
        return false;
    }

    @Override
    public List<String> getAllMarkId() {
        // TODO Auto-generated method stub
        List<String> all = totalDao.query("select markId from QuotedPrice");
        return all;
    }

    @Override
    public boolean updateMarkId(int id, String markId, Boolean isAll) {
        // TODO Auto-generated method stub
        if (id != 0 && markId != null && isAll != null) {
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, id);
            String oldMarkId = qp.getMarkId();
            if (qp == null) {
                return false;
            }
            boolean b = true;
            if (isAll) {
                List<QuotedPrice> qpList = totalDao
                        .query("from QuotedPrice where markId='" + oldMarkId
                                + "'");
                if (qpList.size() > 0) {
                    for (QuotedPrice qp1 : qpList) {
                        qp1.setMarkId(markId);
                        b = b & totalDao.update(qp1);
                    }
                }
                return b;
            } else {
                qp.setMarkId(markId);
                return totalDao.update(qp);
            }
        }
        return false;
    }

    @Override
    public boolean updateQuotedPrice(int id) {
        // TODO Auto-generated method stub
        if (id != 0) {
            boolean b = true;
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, id);
            if (qp != null && qp.getMarkId() != null) {
                Set<QuotedProcessInfor> qpInfoSet1 = qp.getQpinfor();
                List<Integer> processNOs1 = new ArrayList<Integer>();
                if (qpInfoSet1.size() > 0) {
                    for (QuotedProcessInfor process1 : qpInfoSet1) {
                        processNOs1.add(process1.getProcessNO());// 获取修改后的所有的工序号
                    }
                }
                List<QuotedPrice> qpList = totalDao
                        .query("from QuotedPrice where markId='"
                                + qp.getMarkId() + "' and id!=" + id
                                + " and procardStyle='" + qp.getProcardStyle()
                                + "'");
                if (qpList.size() > 0) {// 同步QuotedPrice
                    for (QuotedPrice qp2 : qpList) {
                        BeanUtils.copyProperties(qp, qp2, new String[]{"id",
                                "rootId", "fatherId", "belongLayer", "quanzi1",
                                "quanzi2", "quotedPrice", "quotedPriceSet",
                                "qpinfor"});
                        Set<QuotedProcessInfor> qpInfoSet2 = qp2.getQpinfor();// 循环获取其他零件的工序
                        List<Integer> processNOs2 = new ArrayList<Integer>();
                        if (qpInfoSet2.size() > 0) {
                            for (QuotedProcessInfor process21 : qpInfoSet2) {
                                processNOs2.add(process21.getProcessNO());// 循环获取其他零件的所有的工序号
                            }
                        }
                        List<Integer> toupdate = new ArrayList<Integer>();// 存储与修改有的工序相比都含有的工序
                        List<Integer> toadd = new ArrayList<Integer>();// 存储与修改有的工序相比少的工序
                        List<Integer> todelte = new ArrayList<Integer>();// 存储与修改有的工序相比多的工序
                        for (Integer no1 : processNOs1) {
                            if (processNOs2.contains(no1)) {
                                toupdate.add(no1);
                            } else {
                                toadd.add(no1);
                            }
                        }
                        for (Integer no2 : processNOs2) {
                            if (!processNOs1.contains(no2)) {
                                todelte.add(no2);
                            }
                        }
                        if (toupdate.size() > 0) {// 修改在修改列表中存在的工序号的工序
                            for (QuotedProcessInfor process25 : qpInfoSet1) {
                                if (process25.getProcessNO() != null
                                        && toupdate.contains(process25
                                        .getProcessNO())) {
                                    for (QuotedProcessInfor process26 : qpInfoSet2) {
                                        if (process26.getProcessNO() != null
                                                && process26
                                                .getProcessNO()
                                                .equals(
                                                        process25
                                                                .getProcessNO())) {
                                            BeanUtils.copyProperties(process25,
                                                    process26,
                                                    new String[]{"id",
                                                            "quotedPrice"});
                                            process26.setQuotedPrice(qp2);
                                            b = b & totalDao.update(process26);
                                        }
                                    }
                                }

                            }
                        }
                        if (todelte.size() > 0) {// 删除在删除列表中存在的工序号的工序
                            Set<QuotedProcessInfor> deletSet = new HashSet<QuotedProcessInfor>();
                            for (QuotedProcessInfor process22 : qpInfoSet2) {
                                if (process22.getProcessNO() != null
                                        && todelte.contains(process22
                                        .getProcessNO())) {
                                    deletSet.add(process22);
                                }
                            }
                            qpInfoSet2.removeAll(deletSet);
                            for (QuotedProcessInfor delete : deletSet) {
                                delete.setQuotedPrice(null);
                                b = b & totalDao.delete(delete);
                            }
                        }
                        if (toadd.size() > 0) {// 添加在添加列表中存在的工序号的工序
                            for (QuotedProcessInfor process23 : qpInfoSet1) {
                                if (process23.getProcessNO() != null
                                        && todelte.contains(process23
                                        .getProcessNO())) {
                                    QuotedProcessInfor process24 = new QuotedProcessInfor();
                                    BeanUtils.copyProperties(process23,
                                            process24, new String[]{"id",
                                                    "quotedPrice"});
                                    process24.setQuotedPrice(qp2);
                                    b = b & totalDao.save(process24);
                                    qpInfoSet2.add(process24);
                                }
                            }
                        }

                        if (b) {
                            qp2.setQpinfor(qpInfoSet2);
                            b = b & totalDao.update(qp2);
                        }
                    }
                }

            }
            return b;
        }
        return false;
    }

    @Override
    public boolean saveMoveQuotedPrice(Integer moveId, Integer targetId) {
        // TODO Auto-generated method stub
        if (moveId != null && targetId != null) {
            QuotedPrice move = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, moveId);
            QuotedPrice target = (QuotedPrice) totalDao.getObjectById(
                    QuotedPrice.class, targetId);
            if (move != null && target != null) {
                boolean b = true;
                move.setFatherId(target.getId());
                Integer belongLayer = 1;
                if (target.getBelongLayer() != null) {
                    belongLayer += target.getBelongLayer();
                }
                move.setBelongLayer(belongLayer);
                move.setQuotedPrice(target);
                b = b & totalDao.update(move);
                Set<QuotedPrice> qpSet = move.getQuotedPriceSet();
                if (qpSet != null && qpSet.size() > 0) {
                    for (QuotedPrice pt : qpSet) {
                        b = b & updateBelongLayer(pt, belongLayer);
                    }

                }
                return b;
            }
        }
        return false;
    }

    public boolean updateBelongLayer(QuotedPrice qp, Integer belongLayer) {
        boolean b = true;
        belongLayer++;
        qp.setBelongLayer(belongLayer);
        b = b & totalDao.update(qp);
        Set<QuotedPrice> ptSet = qp.getQuotedPriceSet();
        if (b & ptSet != null && ptSet.size() > 0) {
            for (QuotedPrice ptSon : ptSet) {
                b = b & updateBelongLayer(ptSon, belongLayer);
            }
            return b;
        } else {
            return b;
        }
    }

    @Override
    public List<OutSourcingApp> findOsa(int id) {
        // TODO Auto-generated method stub
        List<OutSourcingApp> all = new ArrayList<OutSourcingApp>();
        String sqlwg = "from OutSourcingApp osa where markID in(select markId from QuotedPrice where rootId =(select rootId from QuotedPrice where id=?) and procardStyle='外购')";
        List<OutSourcingApp> wg = totalDao.query(sqlwg, id);
        String sqlww = " from OutSourcingApp a where a.processNO is not null and a.processNO <>'外购' and a.markID in ("
                + "select markId from QuotedPrice where id in (select q.quotedPrice.id from QuotedProcessInfor q where q.quotedPrice.id in (select id from QuotedPrice where rootId=?)"
                + "and q.processNO = a.processNO and q.productStyle='外委'))";
        List<OutSourcingApp> ww = totalDao.query(sqlww, id);
        all.addAll(wg);
        all.addAll(ww);
        return all;
    }

    @Override
    public String sendOsaNotify(String ids) {
        // TODO Auto-generated method stub
        List<String> markIdList = totalDao
                .query("select markID from OutSourcingApp where id in (" + ids
                        + ")");
        if (markIdList.size() > 0) {
            List<String> userCodeList = totalDao
                    .query("select u.code from Users u join u.moduleFunction f  where f.functionName in ('产品基本信息','产品周期信息','申报原因','成本核算','自制新增成本') group by u.code");
            StringBuffer sb = new StringBuffer();
            int n = 0;
            for (String markId : markIdList) {
                if (n == 0) {
                    sb.append(markId);
                } else {
                    sb.append(",");
                    sb.append(markId);
                }
                n++;
            }
            String msg = "您有件号为：（" + sb.toString() + "）等外购外委没有评审完成请赶紧去评审";
            boolean b = RtxUtil.sendNotify(userCodeList, msg, "外购外委评审", "0",
                    "0");
            if (b) {
                return "发送提醒成功";
            } else {
                return "发送提醒失败";
            }
        }
        return "没有找到外购外委记录发送失败";
    }

    @Override
    public Map<Integer, Object> buildProcardTemp(Integer rootId, String productStyle) {
        // TODO Auto-generated method stub
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        boolean b = true;
        String msg = null;
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                QuotedPrice.class, rootId);
        if (qp != null) {
            List<Integer> sameIdList = totalDao.query(
                    "select id from ProcardTemplate where markId =? and procardStyle='总成'", qp
                            .getMarkId());
            if (sameIdList.size() > 0) {
                b = false;
                msg = "生成" + productStyle + "bom失败,已存在同样的bom树";
            } else {
                List<QuotedPrice> qpList = totalDao.query(
                        "from QuotedPrice where rootId=?", rootId);
                b = buildBomtosop2(qp, null, null, 1, productStyle);
                if (b) {
                    msg = "进入" + productStyle + "成功";
                }
            }
        }
        map.put(1, b);
        map.put(2, msg);
        return map;
    }

    public boolean buildBomtosop2(QuotedPrice qp, Integer rootId,
                                  Integer fatherId, Integer belongLayer, String productStyle) {
        boolean b = true;
        if (qp != null && qp.getMarkId() != null) {
            ProcardTemplate fpt = null;
            if (fatherId != null) {
                fpt = (ProcardTemplate) totalDao.getObjectById(
                        ProcardTemplate.class, fatherId);
            }
            Integer newrootId = rootId;
            Integer newbelongLayer = belongLayer;
            Integer newfatherId = null;
            ProcardTemplate pt = new ProcardTemplate();
            // 设置零件属性
            if (qp.getProcardStyle() != null
                    && qp.getProcardStyle().equals("总成")) {
                pt.setJisuanStatus("ok");
                pt.setDailyoutput(0f);
                pt.setOnePrice(0d);
            }
            pt.setMarkId(qp.getMarkId());
            pt.setProName(qp.getProName());
            pt.setProcardStyle(qp.getProcardStyle());
            pt.setUnit(qp.getUnit());
            pt.setCarStyle(qp.getCarStyle());
            pt.setStatus(qp.getYucailiaostatus());
            pt.setYuanUnit(qp.getUnit());
            pt.setQuanzi1(qp.getQuanzi1());
            pt.setQuanzi2(qp.getQuanzi2());
            pt.setTrademark(qp.getTrademark());
            pt.setSpecification(qp.getSpecification());
            pt.setCorrCount(qp.getCorrCount());
            pt.setBelongLayer(belongLayer);
            pt.setRootId(newrootId);
            pt.setFatherId(fatherId);
            pt.setProductStyle(productStyle);
            pt.setProcardTemplate(fpt);
            pt.setNeedProcess(qp.getNeedProcess());
            //设置工艺编制系统属性
            pt.setBzStatus("初始");
            pt.setCarStyle(qp.getXingbie());

            Set<QuotedProcessInfor> pdSet = qp.getQpinfor();
            if (pdSet != null && pdSet.size() > 0) {
                Set<ProcessTemplate> processtSet = new HashSet<ProcessTemplate>();
                for (QuotedProcessInfor processInFor : pdSet) {
                    ProcessTemplate processt = new ProcessTemplate();
                    // 设置节拍
                    if (processInFor.getOpcaozuojiepai() != null) {
                        processt.setOpcaozuojiepai(processInFor
                                .getOpcaozuojiepai());
                    } else {
                        processt.setOpcaozuojiepai(0f);
                    }
                    if (processInFor.getOpshebeijiepai() != null) {
                        processt.setOpshebeijiepai(processInFor
                                .getOpshebeijiepai());
                    } else {
                        processt.setOpshebeijiepai(0f);
                    }
                    if (processInFor.getGzzhunbeijiepai() != null) {
                        processt.setGzzhunbeijiepai(processInFor
                                .getGzzhunbeijiepai());
                    } else {
                        processt.setGzzhunbeijiepai(0f);
                    }
                    if (processInFor.getGzzhunbeicishu() != null) {
                        processt.setGzzhunbeicishu(processInFor
                                .getGzzhunbeicishu());
                    } else {
                        processt.setGzzhunbeicishu(0f);
                    }
                    // 设置操作人
                    if (processInFor.getJiagongUserCode() != null) {
                        Users user = (Users) totalDao.getObjectByCondition(
                                "from Users where code=?", processInFor
                                        .getJiagongUserCode());
                        if (user != null) {
                            processt.setOperatorDept(user.getDept());// 操作人部门
                            processt.setOperatorCode(user.getCode());// 操作人工号
                            processt.setOperatorCardId(user.getCardId());// 操作人卡号
                            processt.setOperatorName(user.getName());// 操作人姓名
                            processt.setOperatorUserId(user.getId());// 操作人id
                        }
                    }
                    // 设置在工艺规程中找的到的属性
                    processt.setProcessNO(processInFor.getProcessNO());
                    processt.setProcessName(processInFor.getProcessName());
                    if (processInFor.getShebeiId() != null
                            && processInFor.getShebeiNo() != null) {
                        processt.setShebeiId(processInFor.getShebeiId());
                        processt.setShebeiNo(processInFor.getShebeiNo());
                        processt.setShebeiName(processInFor.getShebeiName());
                        processt.setShebeistatus("是");// 设备验证

                        List<TaSopGongwei> shebList = totalDao.query(
                                "from TaSopGongwei where shebeiCode=?",
                                processInFor.getShebeiNo());
                        if (shebList.size() > 0) {
                            TaSopGongwei tsg = shebList.get(0);
                            if (tsg.getCaozJineng() != null) {
                                processt.setOptechnologyRate(tsg
                                        .getCaozJineng());
                            } else {
                                processt.setOptechnologyRate(0f);
                            }
                            if (tsg.getCaoztihuanrenshu() != null) {
                                processt.setOpCouldReplaceRate(tsg
                                        .getCaoztihuanrenshu());
                            } else {
                                processt.setOpCouldReplaceRate(0f);
                            }
                            if (tsg.getGongzhuangFuhe() != null) {
                                processt.setGzfuheRate(tsg.getGongzhuangFuhe());
                            } else {
                                processt.setGzfuheRate(0f);
                            }
                            if (tsg.getGongzhuangJineng() != null) {
                                processt.setGztechnologyRate(tsg
                                        .getGongzhuangJineng());
                            } else {
                                processt.setGztechnologyRate(0f);
                            }

                            if (tsg.getGongzhuangtihuanrenshu() != null) {
                                processt.setGzCouldReplaceRate(tsg
                                        .getGongzhuangtihuanrenshu());
                            } else {
                                processt.setGzCouldReplaceRate(0f);
                            }
                            processt.setGongwei(tsg.getGongweihao());
                            processt.setOpnoReplaceRate(0f);
                            processt.setOpzonghezhishu(0f);
                            processt.setOpzongheqiangdu(0f);
                            processt.setGzfuheRate(0f);
                            processt.setGznoReplaceRate(0f);
                            processt.setGzzonghezhishu(0f);
                        }
                    } else {
                        processt.setShebeistatus("否");// 设备验证
                    }
                    if (processInFor.getGongzhuangId() != null) {
                        processt.setGzstoreId(processInFor.getGongzhuangId());
                        processt.setNumber(processInFor
                                .getOldgongzhuangNumber());
                        processt.setMatetag(processInFor.getOldgongzhuang());
                        processt.setGongzhuangstatus("是");// 工装验证
                    } else {
                        processt.setGongzhuangstatus("否");// 工装验证

                    }
//					 if (pdata.getLiangjuId() != null) {
//					 processt.setMeasuringId(pdata.getLiangjuId());
//					 processt.setMeasuringNumber(pdata.getLiangjuNo());
//					 processt
//					 .setMeasuringMatetag(pdata.getLiangjuName());
//					 processt.setMeasuring_no(pdata
//					 .getLiangjuNoForCompany());
//					 processt.setLiangjustatus("是");// 量具验证
//					 } else {
//					 processt.setLiangjustatus("否");// 量具验证
//					 }
                    processt.setProductStyle("自制");// 默认为自制
                    processt.setZjStatus("yes");// 默认首检
                    processt.setProcessStatus("yes");// 默认并行
                    processt.setIsPrice("yes");// 默认参与
                    processt.setKaoqingstatus("是");// 默认考勤
                    processt.setGuifanstatus("是");// 默认规范
                    // 将工序放入集合中
                    processtSet.add(processt);
                }
                // 将集合放入零件中
                pt.setProcessTemplate(processtSet);
            }
            b = b & totalDao.save(pt);
            if (belongLayer == 1) {
                newrootId = pt.getId();
                pt.setRootId(newrootId);
                b = b & totalDao.update(pt);
            }
            newfatherId = pt.getId();
            Set<QuotedPrice> qpSonSet = qp.getQuotedPriceSet();

            if (qpSonSet.size() > 0) {
                newbelongLayer++;
                for (QuotedPrice qp2 : qpSonSet) {
                    b = b
                            & buildBomtosop2(qp2, newrootId, newfatherId,
                            newbelongLayer, productStyle);
                }
            }

        }
        return b;
    }

    @Override
    public void updateStatus(Integer rootId, String status) {
        // TODO Auto-generated method stub
        List<QuotedPrice> list = totalDao.query("from QuotedPrice where rootId=?", rootId);
        if (status == "核算完成") {
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, rootId);
            if (qp != null) {
                qp.setFbSatuts("未放标");
//				Float money1 =  (Float) totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=?", rootId);
//				Float money2 =  (Float) totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where qpId=? and applyStatus='同意'", rootId);
//				Float money3 = (Float) totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=? and classNumber='gzf'", rootId);
//				if(money1==null){
//					money1 = 0f;
//				}
//				if(money2==null){
//					money2 = 0f;
//				}
//				qp.setRealAllfy(money3);
//				qp.setAllFeiyong(money1+money2);
//				qp.setYingkui(0-money1-money2);
                totalDao.update(qp);
            }
        }
        if (list != null && list.size() > 0) {
            for (QuotedPrice qp : list) {
                qp.setStatus(status);
                totalDao.update(qp);
            }
        }
    }

    // 字符串拼接
    public String haoAddOne(String liuShuiHao) {
        Integer intHao = Integer.parseInt(liuShuiHao);
        intHao++;
        String strHao = intHao.toString();
        while (strHao.length() < 3)
            strHao = "0" + strHao;
        return strHao;
    }

    /**
     * 添加产品基本信息
     **/
    public boolean saveOSApp1(OutSourcingApp osa) {
        Users user = (Users) Util.getLoginUser();
        // 外委申请单编号（NO+部门编码+yyyy+“001”）
        String deptNum = user.getPassword().getDeptNumber();
        String no = "OSA" + deptNum + Util.getDateTime("yyyy");
        String hqlmaxNum = "select max(osaNO) from OutSourcingApp where osaNO like'"
                + no + "%'";
        List li = totalDao.find(hqlmaxNum);
        String maxNum = no + "001";
        if (null != li && li.size() > 0 && null != li.get(0)) {
            String maxdata = (String) li.get(0);
            maxNum = no + haoAddOne(maxdata.substring(maxdata.length() - 3));
        }
        osa.setOsaNO(maxNum);
        osa.setCode(user.getCode());
        osa.setUsername(user.getName());
        osa.setOsaSubmitTime(totalDao.getDateTime(null));
        osa.setStatus("申请");
        osa.setNumber("osa" + deptNum + user.getCode()
                + Util.getDateTime("yyyyMMddHHmmss"));
        osa.setExecuteStatus("产品录入");

        String hql = "from ModuleFunction where functionName='产品周期信息'";
        ModuleFunction moduleFunction = (ModuleFunction) this.totalDao
                .getObjectByCondition(hql);
        String sql = "select ta_userid  from ta_usersMF where ta_mfId=?";
        List list = this.totalDao.createQuerySelect(null, sql, moduleFunction
                .getId());
        List<String> codes = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            Users users = (Users) this.totalDao.getObjectByCondition(
                    "from Users where id=?", list.get(i));
            codes.add(users.getCode());
        }
        if (codes != null && codes.size() > 0) {
            RtxUtil.sendNotify(codes, "有未录入的产品周期信息，请前往录入!", "系统消息", "0", "0");
        }
        return totalDao.save(osa);
    }


    @Override
    public Object[] findQpForCost(QuotedPrice quotedPrice, int pageNo,
                                  int pageSize, String pageStatus) {
        // TODO Auto-generated method stub
        if (quotedPrice == null) {
            quotedPrice = new QuotedPrice();
            quotedPrice.setProcardStyle("总成");
        }
        String hql = totalDao.criteriaQueries(quotedPrice, null);
//		if(pageStatus!=null&&(pageStatus.equals("alltz")||pageStatus.equals("singletz"))){
//			hql+= " and fbSatuts is not null and fbSatuts ='放标'";
//		}else
        if (pageStatus != null && pageStatus.equals("setProTz")) {
            hql += " and fbSatuts is no null or fbSatuts ='未开发'";
        }
        hql += "and status in('核算完成','首件申请中','首件阶段','试制申请中','试制阶段','批产阶段','批产申请中')";
        List<QuotedPrice> list = totalDao.findAllByPage(hql + " order by id desc", pageNo,
                pageSize);
        if (pageStatus != null && (pageStatus.equals("alltz") || pageStatus.equals("singletz"))
                && list.size() > 0) {
            //查出自己产于过投资的项目设置页面属性
            Investor investor = getInvestorByLogin();
            if (investor != null) {
                for (QuotedPrice qp : list) {
                    Float count = (Float) totalDao.getObjectByCondition("select count(*) from QuotedPriceUserCost where investorId=? and qpId=? and tzStatus='投资'", investor.getId(), qp.getId());
                    if (count != null && count > 0) {
                        qp.setHasTz("yes");
                    }
                }
            }
        }
        int count = totalDao.getCount(hql);
        Object[] o = {list, count};
        return o;
    }

    @Override
    public List<QuotedPriceCost> getQpCostDetail(int id) {
        // TODO Auto-generated method stub
        List list = totalDao.query("from QuotedPriceCost where qpId=?", id);
        return list;
    }

    @Override
    public boolean addQpCost(QuotedPriceCost quotedPriceCost) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        quotedPriceCost.setTzMoney(0d);
        quotedPriceCost.setUserName(user.getName());
        quotedPriceCost.setUserCode(user.getCode());
        quotedPriceCost.setDept(user.getDept());
        quotedPriceCost.setApplyStatus("未审批");
        quotedPriceCost.setSource("预算申报");
        quotedPriceCost.setAddTime(Util.getDateTime());
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, quotedPriceCost.getQpId());
        if (qp != null) {
//			quotedPriceCost.setProStatus(qp.getStatus());
            quotedPriceCost.setMarkId(qp.getMarkId());
        } else {
            return false;
        }
        totalDao.save(quotedPriceCost);
        Integer epId = null;
        try {
//			epId = CircuitRunServerImpl.createProcess("项目阶段费用审批流程", QuotedPriceCost.class, quotedPriceCost.getId(), "applyStatus", "id","QuotedPrice_QpCostshow.action.action?id="+quotedPriceCost.getId(), "仓库物料解封申请审批", true);
            epId = CircuitRunServerImpl.createProcess("项目阶段费用审批流程", QuotedPriceCost.class, quotedPriceCost.getId(), "applyStatus", "id", user.getName() + "申请项目阶段费用", true);
            if (epId != null) {
                quotedPriceCost.setEpId(epId);
                CircuitRun cr = (CircuitRun) totalDao.getObjectById(CircuitRun.class, epId);
                if (cr.getAllStatus() != null && cr.getAllStatus().equals("同意") && cr.getAuditStatus() != null && cr.getAuditStatus().equals("审批完成")) {
                    //没有审批结点直接同意
                    //重新计算零件总费用
                    Float money1 = (Float) totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=?", quotedPriceCost.getQpId());
                    Float money2 = (Float) totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where qpId=? and applyStatus='同意'", quotedPriceCost.getQpId());
                    if (money1 == null) {
                        money1 = 0f;
                    }
                    if (money2 == null) {
                        money2 = 0f;
                    }
                    qp.setAllFeiyong(money1 + money2 + quotedPriceCost.getMoney());
                    if (qp.getYingkui() == null) {
                        qp.setYingkui(0 - qp.getAllFeiyong());
                    } else {
                        qp.setYingkui(qp.getYingkui() - quotedPriceCost.getMoney());
                    }
                    totalDao.update(qp);
                    quotedPriceCost.setApplyStatus("同意");
                }
                return totalDao.update(quotedPriceCost);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Object[] getQpUserCostMsg(int id, QuotedPriceUserCost qpUserCost, String pageStatus) {
        // TODO Auto-generated method stub
        String proStatus = null;
        if (qpUserCost == null || qpUserCost.getProStatus() == null || qpUserCost.getProStatus().length() == 0) {
            proStatus = "核算完成";
        } else {
            proStatus = qpUserCost.getProStatus();
        }
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, id);
        Double totalMoney = qp.getAllFeiyong();//项目求资总额
        Float dfMoney = qp.getDfMoney();//项目单份金额
        Double hastzMoney = qp.getTouziFeiyong();//已投资金额
        if (hastzMoney == null) {
            hastzMoney = 0d;
        }
        Double zongCountdb = null;
        Double shengyuCountdb = null;
        if (dfMoney != null && dfMoney > 0) {
            zongCountdb = Math.ceil(totalMoney / dfMoney);
            shengyuCountdb = Math.floor((qp.getTfzonge() - hastzMoney) / dfMoney);
        } else {
            zongCountdb = 0d;
            shengyuCountdb = 0d;
        }
        Float zongCount = Float.parseFloat(zongCountdb.toString());//总份数
        Float shengyuCount = Float.parseFloat(shengyuCountdb.toString());//剩余份数
        Float selfAllMoney = null;
        Float selfThisMoney = null;
        List list = null;
        ;
        if (pageStatus.equals("singletz")) {
            Users user = Util.getLoginUser();
            selfAllMoney = (Float) totalDao.getObjectByCondition("select sum(tzMoney) from QuotedPriceUserCost where " +
                    "applyStatus='同意' and tzStatus!='撤资' and userCode=? and qpId=?", user.getCode(), id);
            selfThisMoney = (Float) totalDao.getObjectByCondition("select sum(tzMoney) from QuotedPriceUserCost where " +
                    "applyStatus='同意' and tzStatus!='撤资' and userCode=? and qpId=? and proStatus=?", user.getCode(), id, proStatus);
            list = totalDao.query("from QuotedPriceUserCost where " +
                    "userCode=? and qpId=? ", user.getCode(), id);
        } else if (pageStatus.equals("alltz")) {
            String addSql = " and 1=1 ";
            if (qpUserCost != null) {
                if (qpUserCost.getUserName() != null
                        && qpUserCost.getUserName().length() > 0) {
                    addSql += "and userName like'%" + qpUserCost.getUserName()
                            + "%'";
                }
                if (qpUserCost.getUserCode() != null
                        && qpUserCost.getUserCode().length() > 0) {
                    addSql += "and userCode like'%" + qpUserCost.getUserCode()
                            + "%'";
                }
            }
            selfAllMoney = (Float) totalDao.getObjectByCondition(
                    "select sum(tzMoney) from QuotedPriceUserCost where "
                            + "applyStatus='同意' and qpId=?" + addSql, id);
            selfThisMoney = (Float) totalDao.getObjectByCondition(
                    "select sum(tzMoney) from QuotedPriceUserCost where "
                            + "applyStatus='同意' and qpId=? and proStatus=?"
                            + addSql, id, proStatus);
            list = totalDao.query("from QuotedPriceUserCost where "
                            + " qpId=?" + addSql,
                    id);

        }
        return new Object[]{zongCount, shengyuCount, selfAllMoney, selfThisMoney, list};
    }

    @Override
    public List showCostType(int id) {
        // TODO Auto-generated method stub
        List list = (List) totalDao.query("select new map(costType as costType,money as money,tzMoney as tzMoney)" +
                " from QuotedPriceCost where qpId=? and applyStatus='同意'", id);
        return list;
    }

    @Override
    public String addQpUserCost(QuotedPriceUserCost quotedPriceUserCost) {
        // TODO Auto-generated method stub
        if (quotedPriceUserCost == null) {
            return "填报异常";
        }
        if (quotedPriceUserCost.getTzFenshu() == null || quotedPriceUserCost.getTzFenshu() == 0) {
            return "填报异常,请填写投资份数!";
        }
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录";
        }
        Investor investor = (Investor) totalDao.getObjectByCondition("from Investor where userId=?", user.getId());
        if (investor.getKyMoney() == null || investor.getKyMoney() < quotedPriceUserCost.getTzMoney()) {
            return "对不起您拥有的可用资金不足，请先充值!";
        } else {
            if (investor.getDjMoney() == null) {
                investor.setDjMoney(quotedPriceUserCost.getTzMoney());
            } else {
                investor.setDjMoney(investor.getDjMoney() + quotedPriceUserCost.getTzMoney());
            }
            investor.setKyMoney(investor.getKyMoney() - quotedPriceUserCost.getTzMoney());
            quotedPriceUserCost.setInvestorId(investor.getId());
        }
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, quotedPriceUserCost.getQpId());
        if (qp == null) {
            return "没有找到投资目标";
        } else {
            quotedPriceUserCost.setTzMoney(quotedPriceUserCost.getTzFenshu() * qp.getDfMoney());
            if (qp.getTouziFeiyong() == null) {
                qp.setTouziFeiyong(0d);
            }
            quotedPriceUserCost.setTzMoney(quotedPriceUserCost.getTzFenshu() * qp.getDfMoney());
            if (quotedPriceUserCost.getTzMoney() > (qp.getTfzonge() - qp.getTouziFeiyong())) {
                return "您投资的金额超过可投资金额!";
            }

        }
        quotedPriceUserCost.setUserName(user.getName());
        quotedPriceUserCost.setUserCode(user.getCode());
        quotedPriceUserCost.setDept(user.getDept());
        quotedPriceUserCost.setApplyStatus("未审批");
        quotedPriceUserCost.setAddTime(Util.getDateTime());
        quotedPriceUserCost.setTzStatus("投资");
        if (qp != null) {
            quotedPriceUserCost.setProStatus(qp.getStatus());
            quotedPriceUserCost.setMarkId(qp.getMarkId());
            Float timeMoney = (Float) totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where qpId=? and proStatus=?", qp.getId(), qp.getStatus());
            Float money1 = (Float) totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=?", qp.getId());
            if (money1 == null) {
                money1 = 0f;
            }
            if (timeMoney == null) {
                timeMoney = 0f;
            }
            quotedPriceUserCost.setTimeMoney(timeMoney + money1);
        }
        totalDao.save(quotedPriceUserCost);
        //增加投资角色
        InvestorOfQuotedPrice iqp = (InvestorOfQuotedPrice) totalDao.getObjectByCondition("from InvestorOfQuotedPrice where investorId=? and qpId=?", investor.getId(), qp.getId());
        if (iqp == null) {
            iqp = new InvestorOfQuotedPrice();
            iqp.setInvestorId(investor.getId());
            iqp.setQpId(qp.getId());
            iqp.setRole("成员");
            iqp.setAddTime(Util.getDateTime());
            totalDao.save(iqp);
        }
        Integer epId = null;
        try {
//			epId = CircuitRunServerImpl.createProcess("项目投资审批流程", QuotedPriceUserCost.class, quotedPriceUserCost.getId(), "applyStatus", "id","QuotedPrice_quotedPriceUserCost.action.action?id="+quotedPriceUserCost.getId(), "仓库物料解封申请审批", true);
            epId = CircuitRunServerImpl.createProcess("项目投资审批流程", QuotedPriceUserCost.class, quotedPriceUserCost.getId(), "applyStatus", "id", user.getName() + "申请项目投资", true);
            if (epId != null) {
                quotedPriceUserCost.setEpId(epId);
                CircuitRun cr = (CircuitRun) totalDao.getObjectById(CircuitRun.class, epId);
                if (cr.getAllStatus() != null && cr.getAllStatus().equals("同意") && cr.getAuditStatus() != null && cr.getAuditStatus().equals("审批完成")) {
                    //没有审批结点直接同意
                    quotedPriceUserCost.setApplyStatus("同意");
                    Float touziFeiyong = (Float) totalDao.getObjectByCondition("select sum(tzMoney) from QuotedPriceUserCost where  applyStatus='同意' and tzStatus!='撤资' and qpId=?", quotedPriceUserCost.getQpId());
                    qp.setTouziFeiyong((double) touziFeiyong);
                    totalDao.update(qp);
                }
                totalDao.update(investor);
                return totalDao.update(quotedPriceUserCost) + "";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("审批流程有误!");
        }
        throw new RuntimeException("审批流程有误!");
    }

    @Override
    public String ApplyQP(int id) {
        // TODO Auto-generated method stub
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, id);
        String changeType = null;
        if (qp == null) {
            return "没有找到目标,申请失败!";
        } else if (qp.getStatus().equals("核算完成")) {
            //重新计算零件总费用和盈亏
            changeType = "首件申请中";
        } else if (qp.getStatus().equals("首件阶段")) {
            changeType = "试制申请中";
        } else if (qp.getStatus().equals("项目试制")) {
            changeType = "批产申请中";
        } else {
            return "项目阶段不合标准!申请失败!";
        }
        Integer epId = null;
        try {
            epId = CircuitRunServerImpl.createProcess("项目升级审批流程", QuotedPrice.class, qp.getId(), "applyStatus", "id", qp.getMarkId() + "项目阶段升级申请审批", true);
            if (epId != null) {
                List<QuotedPrice> qpList = totalDao.query("from QuotedPrice where rootId =?", id);
                CircuitRun cr = (CircuitRun) totalDao.getObjectById(CircuitRun.class, epId);
                if (cr.getAllStatus() != null && cr.getAllStatus().equals("同意") && cr.getAuditStatus() != null && cr.getAuditStatus().equals("审批完成")) {
                    //没有审批结点直接同意
                    for (QuotedPrice qp2 : qpList) {
                        if (changeType.equals("试制申请中")) {
                            qp2.setStatus("试制阶段");
                        }
                        if (changeType.equals("批产申请中")) {
                            qp2.setStatus("批产阶段");
                        }
                        qp2.setEpId(epId);
                        totalDao.update(qp2);
                    }
                } else {
                    for (QuotedPrice qp2 : qpList) {
                        qp2.setStatus(changeType);
                        qp2.setEpId(epId);
                        totalDao.update(qp2);
                    }
                }
                return "true";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "审批流程有误!";
        }

        return "true";
    }

    @Override
    public QuotedPrice changeQpAllFeiYong(int id) {
        // TODO Auto-generated method stub
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, id);
        //重新计算零件总费用
//		Float money1 =  (Float) totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=? and classNumber!='gzf'", id);
        Float money2 = (Float) totalDao.getObjectByCondition("select sum(money) from QuotedPriceCost where qpId=? and applyStatus='同意'", id);
        Float moneysj = findAllMoneysj(id);
        Float moneysz = findAllMoneysz(id);
        //直接工装记为实际工装费用
        Float moneygz = (Float) totalDao.getObjectByCondition("select sum(money) from ProjectTime where quoId=? and classNumber='gzf'", id);
//		if(money1==null){
//			money1 = 0f;
//		}
        if (money2 == null) {
            money2 = 0f;
        }
        if (moneysj == null) {
            moneysj = 0f;
        }
        if (moneysz == null) {
            moneysz = 0f;
        }
        if (moneygz == null) {
            moneygz = 0f;
        }
        if (qp.getGzcb() == null || qp.getGzcb().equals("是")) {
            qp.setAllFeiyong((double) money2 + moneysj + moneysz + moneygz);
        } else {
            qp.setAllFeiyong((double) money2 + moneysj + moneysz);
        }
        totalDao.update(qp);
        return qp;
    }

    @Override
    public List<QuotedPriceFenhong> getFhList(int id) {
        // TODO Auto-generated method stub
        return totalDao.query("from QuotedPriceFenhong where qpucId=?", id);
    }

    @Override
    public Map<Integer, Object> findInvestorsByCondition(Investor investor,
                                                         int pageNo, int pageSize) {
        // TODO Auto-generated method stub
        if (investor == null) {
            investor = new Investor();
        }
        String hql = totalDao.criteriaQueries(investor, null, null);
        int count = totalDao.getCount(hql);
        List objs = totalDao.findAllByPage(hql, pageNo, pageSize);
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, objs);
        map.put(2, count);
        return map;
    }

    @Override
    public List<Users> findAllBangUser() {
        // TODO Auto-generated method stub
        String hql = null;
        hql = "from Users where onWork not in ('离职','离职中','内退','退休','病休') and dept not in('內退','病休')" +
                " and id in (select userId from Investor)" + " order by dept";

        return totalDao.query(hql);
    }

    @Override
    public Object[] findAllUser(Users user, int pageNo, int pageSize) {
        // TODO Auto-generated method stub
        if (user == null) {
            user = new Users();
        }
        String hql = totalDao.criteriaQueries(user, null, null);
        hql += " and onWork not in ('离职','离职中','内退','退休','病休') and dept not in('內退','病休')" +
                " and id not in (select userId from Investor)" + " order by dept";

        List list = totalDao.findAllByPage(hql, pageNo, pageSize);
        int count = totalDao.getCount(hql);
        Object[] o = {list, count};
        return o;
    }

    @Override
    public String addTzr(Integer[] usersId) {
        // TODO Auto-generated method stub
        if (usersId != null && usersId.length > 0) {
            StringBuffer userIdsb = new StringBuffer();
            for (int i = 0; i < usersId.length; i++) {
                if (i == 0) {
                    userIdsb.append(usersId[i]);
                } else {
                    userIdsb.append("," + usersId[i]);
                }
            }
            List<Users> userList = totalDao.query("from Users where onWork not in ('离职','离职中','内退','退休','病休') and dept not in('內退','病休')" +
                    " and id not in (select userId from Investor) and id in (" + userIdsb.toString() + ") order by dept");
            if (userList.size() > 0) {
                for (Users uses : userList) {
                    Investor investor = new Investor();
                    investor.setUserId(uses.getId());
                    investor.setName(uses.getName());//名字
                    investor.setCode(uses.getCode());//工号
                    investor.setDept(uses.getDept());//部门
                    investor.setType("正常");//类型(正常,黑名单)
                    totalDao.save(investor);
                }
            }
            return "添加成功";
        }
        return "请选择人员!";
    }

    @Override
    public String updatezg(int id, String op) {
        // TODO Auto-generated method stub
        Investor investor = getInvestorById(id);
        if (investor != null) {
            if (op.equals("恢复")) {
                investor.setType("正常");
                return totalDao.update(investor) + "";
            } else if (op.equals("取消")) {
                investor.setType("黑名单");
                return totalDao.update(investor) + "";
            }
            return "异常操作!";
        }
        return "没有找到目标";
    }

    @Override
    public Investor getInvestorById(Integer id) {
        // TODO Auto-generated method stub
        return (Investor) totalDao.getObjectById(Investor.class, id);
    }

    @Override
    public String gettzzg() {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user != null) {
            String type = (String) totalDao.getObjectByCondition("select type from Investor where userId=?", user.getId());
            if (type != null && type.equals("正常")) {
                return "true";
            }
        }
        return "对不起,您没有投资资格!";
    }

    @Override
    public QuotedPrice getQPById(int id) {
        // TODO Auto-generated method stub
        return (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, id);
    }

    @Override
    public String setProTz(QuotedPrice quotedPrice) {
        // TODO Auto-generated method stub
        QuotedPrice old = getQPById(quotedPrice.getId());
        if (old != null) {
            if (old.getFbSatuts() != null && !old.getFbSatuts().equals("") && !old.getFbSatuts().equals("未放标")) {
                return "此项目的放标状态为：" + old.getFbSatuts() + "不能设置!";
            }
            Float money = findAllMoney(quotedPrice.getId());
            Float moneysj = findAllMoneysj(quotedPrice.getId());
            Float moneysz = findAllMoneysz(quotedPrice.getId());
            if (quotedPrice.getSzCount() != null) {
                moneysz = moneysz * quotedPrice.getSzCount();
            } else {
                return "请填写试制数量!";
            }
            Float moneyGz = 0f;
            old.setSjrefy(moneysj);//首件预算费用
            old.setSzrefy(moneysz);//试制预算费用（不含首件）
            old.setGzcb(quotedPrice.getGzcb());
            old.setSzCount(quotedPrice.getSzCount());
            old.setDfMoney(quotedPrice.getDfMoney());
            if (old.getGzcb() == null || old.getGzcb().equals("是")) {
                String hql = "select money from ProjectTime where quoId=? and classNumber=?";
                moneyGz = (Float) totalDao.getObjectByCondition(hql, quotedPrice.getId(),
                        "gzf");
                if (moneyGz == null) {
                    moneyGz = 0f;
                }
            }
            old.setAllFeiyong((double) moneysj + moneysz + moneyGz);
            if (quotedPrice.getFbBili() != null) {
                old.setFbBili(quotedPrice.getFbBili());
                old.setTfzonge(Float.parseFloat(old.getAllFeiyong() * old.getFbBili() + ""));
            }
            return "" + totalDao.update(old);
        }
        return "对不起没有找到目标";
    }

    @Override
    public String updatefbStatus(int id) {
        // TODO Auto-generated method stub
        QuotedPrice qp = getQPById(id);
        if (qp != null && qp.getProcardStyle().equals("总成")) {
            if (qp.getFbSatuts() == null || qp.getFbSatuts().equals("未放标")) {
                qp.setFbSatuts("放标");
            } else if (qp.getFbSatuts().equals("放标")) {
                qp.setFbSatuts("关闭");
                //项目放标结束后统计票数，如果票数=总数量就出组长
                Float f = (Float) totalDao.getObjectByCondition("select sum(selectedCount)-count(*)from InvestorOfQuotedPrice where qpId=?", id);
                if (f != null && f == 0) {
                    //确定组长
                    List<InvestorOfQuotedPrice> zuzhangList = totalDao.query("from InvestorOfQuotedPrice where selectedCount=(select max(selectedCount) from InvestorOfQuotedPrice where qpId=?)", id);
                    if (zuzhangList != null && zuzhangList.size() > 0) {
                        if (zuzhangList.size() > 1) {
                            //推送RTX消息
                            String proName = (String) totalDao.getObjectByCondition("select projectName from ProjectManage where id=?", qp.getProId());
                            List<String> codeList = totalDao.query("slect code from Investor  where id in(select investorId from  InvestorOfQuotedPrice where qpId=?)", id);
                            RtxUtil.sendNoLoginNotify(codeList, proName + "项目的" + qp.getMarkId() + "零件此轮组长推选失败!有" + zuzhangList.size() + "名投资人拥有相同的最大票数,连接地址:", "项目组长推选通知", "0", "0");
                            for (InvestorOfQuotedPrice ip : zuzhangList) {//刷新投票数量
                                ip.setSelectedCount(0);
                                totalDao.update(ip);
                            }
                            return "此轮组长推选失败!有" + zuzhangList.size() + "名投资人拥有相同的最大票数";
                        } else {
                            InvestorOfQuotedPrice zuzhang = zuzhangList.get(0);
                            zuzhang.setRole("组长");
                            String zuzhangName = (String) totalDao.getObjectByCondition("select name from Investor where id =?", zuzhang.getInvestorId());
                            qp.setZuzhang(zuzhangName);
                            totalDao.update(zuzhang);
                            totalDao.update(qp);
                            String proName = (String) totalDao.getObjectByCondition("select projectName from ProjectManage where id=?", qp.getProId());
                            List<String> codeList = totalDao.query("slect code from Investor  where id in(select investorId from  InvestorOfQuotedPrice where qpId=?)", id);
                            RtxUtil.sendNoLoginNotify(codeList, proName + "项目的" + qp.getMarkId() + "零件此轮组长推选成功!组长为:" + zuzhangName + "。", "项目组长推选通知", "0", "0");
                            return "此轮组长推选成功!组长为:" + zuzhangName + "。";
                        }
                    }
                }

                //给投资人发送推选组长信息
                String proName = (String) totalDao.getObjectByCondition("select projectName from ProjectManage where id=?", qp.getProId());
                List<String> codeList = totalDao.query("slect code from Investor  where id in(select investorId from  InvestorOfQuotedPrice where qpId=?)", id);
                RtxUtil.sendNoLoginNotify(codeList, proName + "项目的" + qp.getMarkId() + "零件已关闭投资请去推选组长,连接地址:" + Util.serverUrl + "/QuotedPrice_findQpPartner.action?pageStatus=singletz&id" + id, "项目组长推选通知", "0", "0");
            } else {
                return "目标项目已关闭,操作有误!";
            }
            return "" + totalDao.update(qp);
        }
        return "对不起没有找到目标";
    }

    @Override
    public Float findAllMoneysj(int id) {
        // TODO Auto-generated method stub
        String hql1 = "select sum(money) from ProjectTime where quoId=? and classNumber in('sb','gz','rg') ";
        Float money1 = (Float) totalDao.getObjectByCondition(hql1, id);
        if (money1 == null) {
            money1 = 0f;
        }
        String hql2 = "select sum(moneysj) from ProjectTime where quoId=? and classNumber not in('sb','gz','rg','gzf') ";
        Float money2 = (Float) totalDao.getObjectByCondition(hql2, id);
        if (money2 == null) {
            money2 = 0f;
        }
        return money1 + money2;
    }

    @Override
    public Float findAllMoneysz(int id) {
        // TODO Auto-generated method stub
        String hql1 = "select sum(money) from ProjectTime where quoId=? and classNumber in('sb','gz','rg') ";
        Float money1 = (Float) totalDao.getObjectByCondition(hql1, id);
        if (money1 == null) {
            money1 = 0f;
        }
        String hql2 = "select sum(moneysz) from ProjectTime where quoId=? and classNumber not in('sb','gz','rg','gzf') ";
        Float money2 = (Float) totalDao.getObjectByCondition(hql2, id);
        if (money2 == null) {
            money2 = 0f;
        }
        return money1 + money2;
    }

    @Override
    public Float findAllMoneyqt(int id) {
        // TODO Auto-generated method stub
        String hql1 = "select sum(money) from QuotedPriceCost where qpId=? and source='预算申报'";
        Float money1 = (Float) totalDao.getObjectByCondition(hql1, id);
        if (money1 == null) {
            money1 = 0f;
        }
        return money1;
    }

    @Override
    public List<QuotedPriceCost> findQpCostList(int id, String source) {
        // TODO Auto-generated method stub
        if (source == null || source.length() == 0) {
            return totalDao.query("from QuotedPriceCost where qpId=? order by source", id);
        } else {
            return totalDao.query("from QuotedPriceCost where qpId=? and source=?", id, source);
        }
    }

    @Override
    public Investor getInvestorByLogin() {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user != null) {
            return (Investor) totalDao.getObjectByCondition("from Investor where userId=? and type='正常'", user.getId());
        }
        return null;
    }

    @Override
    public Float getSelfJxgx(int id) {
        // TODO Auto-generated method stub
        return (Float) totalDao.getObjectByCondition("select jixiaokaohegongzi from WageStandard where code=(select code from Users where id=?)", id);
    }

    @Override
    public List<RechargeRecord> getRechargeRecords(Integer id) {
        // TODO Auto-generated method stub
        return totalDao.query("from RechargeRecord where investorId=?", id);
    }

    @Override
    public InvestorMonthJx saveOrGetInvestorMonthJxsById(Integer id) {
        // TODO Auto-generated method stub
        String month = Util.getDateTime("yyyy-MM");
        InvestorMonthJx monthjx = (InvestorMonthJx) totalDao.getObjectByCondition("from InvestorMonthJx where investorId =?  and month =?", id, month);
        if (monthjx == null) {
            Users user = Util.getLoginUser();
            if (user != null) {
                Float money = (Float) totalDao.getObjectByCondition("select jixiaokaohegongzi from WageStandard where code=?", user.getCode());
                if (money != null) {
                    monthjx = new InvestorMonthJx();
                    monthjx.setMonthMoney(money);//月份可充值金额（当月绩效工资）
                    monthjx.setSyMoney(money);//剩余金额
                    monthjx.setMonth(month);//月份
                    monthjx.setInvestorId(id);//投资人Id
                    totalDao.save(monthjx);
                    return monthjx;
                }
            }
        } else {
            return monthjx;
        }
        return null;
    }

    @Override
    public String inmoney(int id, Float money) {
        // TODO Auto-generated method stub
        String month = Util.getDateTime("yyyy-MM");
        Investor investor = getInvestorById(id);
        InvestorMonthJx monthjx = (InvestorMonthJx) totalDao.getObjectByCondition("from InvestorMonthJx where investorId =?  and month =?", id, month);
        if (monthjx == null || monthjx.getSyMoney() == null) {
            return "没有查到您的可充值金额!";
        }
        if (monthjx.getSyMoney() < money) {
            return "您此次充值金额超过了的可充值金额!";
        }
        monthjx.setSyMoney(monthjx.getSyMoney() - money);
        totalDao.update(monthjx);//修改该月剩余可充值金额
        RechargeRecord rr = new RechargeRecord();
        rr.setTime(Util.getDateTime());
        rr.setMoney(money);
        rr.setType("绩效充值");
        rr.setInvestorId(id);//投资人Id
        totalDao.save(rr);//添加充值记录
        if (investor.getHasMoney() == null) {
            investor.setHasMoney(money);
        } else {
            investor.setHasMoney(investor.getHasMoney() + money);
        }
        if (investor.getKyMoney() == null) {
            investor.setKyMoney(money);
        } else {
            investor.setKyMoney(investor.getKyMoney() + money);
        }
        return "" + totalDao.update(investor);//增加投资人的金额
    }

    @Override
    public Map<Integer, Object> findQPriceUserCostsByCondition(
            QuotedPriceUserCost quotedPriceUserCost, int investorId,
            int pageNo, int pageSize, String start, String end) {
        // TODO Auto-generated method stub
        if (quotedPriceUserCost == null) {
            quotedPriceUserCost = new QuotedPriceUserCost();
        }
        String hql = totalDao.criteriaQueries(quotedPriceUserCost, null, null);
        hql += " and addTime >=? and addTime<=? ";
        if (start == null || start.length() == 0) {
            start = "0000-00-00 00:00:00";
        } else {
            start += " 00:00:00";
        }
        if (end == null || end.length() == 0) {
            end = "9999-12-32 00:00:00";
        } else {
            end += " 24:00:00";
        }
        hql += " and investorId=?";
        int count = totalDao.getCount(hql, start, end, investorId);
        List<QuotedPriceUserCost> list = totalDao.findAllByPage(hql, pageNo, pageSize, start, end, investorId);
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, list);
        map.put(2, count);
        return map;
    }

    @Override
    public List<QuotedPrice> findSelectLeaderList() {
        // TODO Auto-generated method stub
        Investor investor = getInvestorByLogin();
        if (investor != null) {
            List<QuotedPrice> qpList = totalDao.query("from QuotedPrice where id in(select qpId from InvestorOfQuotedPrice where investorId=?)", investor.getId());
            if (qpList != null && qpList.size() > 0) {
                for (QuotedPrice qp : qpList) {
                    String zuzhang = (String) totalDao.getObjectByCondition("select name from Investor where id in" +
                            "(select investorId from InvestorOfQuotedPrice where qpId=? and role='组长')", qp.getId());
                    qp.setZuzhang(zuzhang);
                }
            }
            return qpList;

        }
        return null;
    }

    @Override
    public List<Investor> getQpInvestorList(int id) {
        // TODO Auto-generated method stub
        List<Investor> investorList = totalDao.query("from Investor where id in(select investorId from InvestorOfQuotedPrice where qpId =?)", id);
        if (investorList != null && investorList.size() > 0) {
            Investor self = getInvestorByLogin();
            if (self == null) {
                return null;
            }
            //查到本人之前选中的组长Id(投资人和零件中间表Id)
            Integer selectedId = (Integer) totalDao.getObjectByCondition("select zzId from InvestorOfQuotedPrice where qpId=? and investorId=?", id, self.getId());
            if (selectedId != null) {
                for (Investor i : investorList) {
                    if (selectedId.equals(i.getId())) {
                        i.setIsSelected("yes");
                    }
                }
            }
        }
        return investorList;
    }

    @Override
    public List<InvestorOfQuotedPrice> getInvestorOfQpList(int id) {
        // TODO Auto-generated method stub
        List<InvestorOfQuotedPrice> iOfQpList = totalDao.query("from InvestorOfQuotedPrice where qpId =? order by role", id);
        if (iOfQpList != null && iOfQpList.size() > 0) {
            for (InvestorOfQuotedPrice iOfQp : iOfQpList) {
                Investor i = (Investor) totalDao.getObjectById(Investor.class, iOfQp.getInvestorId());
                if (i != null) {
                    iOfQp.setZanCount(i.getZanCount());
                    iOfQp.setTocaoCount(i.getTocaoCount());
                    iOfQp.setName(i.getName());
                    iOfQp.setCode(i.getCode());
                    iOfQp.setDept(i.getDept());
                }
            }
        }
        return iOfQpList;
    }

    @Override
    public String saveLeader(Integer qpId, Integer investorId) {
        // TODO Auto-generated method stub
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, qpId);
        if (qp == null) {
            return "对不起没有找到目标项目";
        }
        Investor self = getInvestorByLogin();
        if (self == null) {
            return "请先登录或确认投资资格!";
        }
        //找到本人与次零件的对应关系设置本人选的组长
        InvestorOfQuotedPrice selfOfQp = (InvestorOfQuotedPrice) totalDao.getObjectByCondition("from InvestorOfQuotedPrice where qpId =? and investorId=?", qpId, self.getId());
        if (selfOfQp == null) {
            return "对不起没有找到您对此项目的投资!";
        }
        //找到被选中组长的对象
        InvestorOfQuotedPrice selected = (InvestorOfQuotedPrice) totalDao.getObjectByCondition("from InvestorOfQuotedPrice where qpId =? and investorId=?", qpId, investorId);
        if (selected == null) {
            return "对不起没有找到您选中的投资人对此项目的投资";
        }
        if (selfOfQp.getZzId() != null) {
            if (!selfOfQp.getZzId().equals(selected.getId())) {//选择了另一个
                //将原来的票数减1
                InvestorOfQuotedPrice beforeSelected = (InvestorOfQuotedPrice) totalDao.getObjectByCondition("from InvestorOfQuotedPrice where qpId =? and investorId=?", qpId, selfOfQp.getZzId());
                if (beforeSelected != null) {
                    beforeSelected.setSelectedCount(beforeSelected.getSelectedCount() - 1);
                    totalDao.update(beforeSelected);
                }
                selfOfQp.setZzId(selected.getId());
                if (selected.getSelectedCount() == null) {
                    selected.setSelectedCount(1);
                } else {
                    selected.setSelectedCount(selected.getSelectedCount() + 1);
                }
                totalDao.update(selfOfQp);
                totalDao.update(selected);
            } else {
                //不用改变
            }
        } else {
            selfOfQp.setZzId(selected.getId());
            if (selected.getSelectedCount() == null) {
                selected.setSelectedCount(1);
            } else {
                selected.setSelectedCount(selected.getSelectedCount() + 1);
            }
            totalDao.update(selfOfQp);
            totalDao.update(selected);
        }
        //项目放标结束后统计票数，如果票数=总数量就出组长
        Float f = (Float) totalDao.getObjectByCondition("select sum(selectedCount)-count(*)from InvestorOfQuotedPrice where qpId=?", qpId);
        if (qp.getFbSatuts() != null && qp.getFbSatuts().equals("关闭") && f != null && f == 0) {
            //确定组长
            List<InvestorOfQuotedPrice> zuzhangList = totalDao.query("from InvestorOfQuotedPrice where selectedCount=(select max(selectedCount) from InvestorOfQuotedPrice where qpId=?)", qpId);
            if (zuzhangList != null && zuzhangList.size() > 0) {
                if (zuzhangList.size() > 1) {
                    //推送RTX消息
                    String proName = (String) totalDao.getObjectByCondition("select projectName from ProjectManage where id=?", qp.getProId());
                    List<String> codeList = totalDao.query("slect code from Investor  where id in(select investorId from  InvestorOfQuotedPrice where qpId=?)", qpId);
                    RtxUtil.sendNoLoginNotify(codeList, proName + "项目的" + qp.getMarkId() + "零件此轮组长推选失败!有" + zuzhangList.size() + "名投资人拥有相同的最大票数,连接地址:", "项目组长推选通知", "0", "0");
                    for (InvestorOfQuotedPrice ip : zuzhangList) {//刷新投票数量
                        ip.setSelectedCount(0);
                        totalDao.update(ip);
                    }
                    return "投票成功,但此轮组长推选失败!有" + zuzhangList.size() + "名投资人拥有相同的最大票数";
                } else {
                    InvestorOfQuotedPrice zuzhang = zuzhangList.get(0);
                    zuzhang.setRole("组长");
                    String zuzhangName = (String) totalDao.getObjectByCondition("select name from Investor where id =?", zuzhang.getInvestorId());
                    qp.setZuzhang(zuzhangName);
                    totalDao.update(zuzhang);
                    totalDao.update(qp);
                    //推送RTX消息
                    String proName = (String) totalDao.getObjectByCondition("select projectName from ProjectManage where id=?", qp.getProId());
                    List<String> codeList = totalDao.query("slect code from Investor  where id in(select investorId from  InvestorOfQuotedPrice where qpId=?)", qpId);
                    RtxUtil.sendNoLoginNotify(codeList, proName + "项目的" + qp.getMarkId() + "零件此轮组长推选成功!组长为:" + zuzhangName + "。", "项目组长推选通知", "0", "0");
                    return "true";
                }
            }
        }
        return "true";

    }

    @Override
    public InvestorOfQuotedPrice getInvestorOfQpById(int id) {
        // TODO Auto-generated method stub
        return (InvestorOfQuotedPrice) totalDao.getObjectById(InvestorOfQuotedPrice.class, id);
    }

    @Override
    public List<InvestorEvaluation> findieList(int id) {
        // TODO Auto-generated method stub
        Investor i = getInvestorByLogin();
        if (i != null) {
            return totalDao.query("from InvestorEvaluation where iOfQpId=? and investorId=?", id, i.getId());
        }
        return null;
    }

    @Override
    public Map<Integer, Object> evaluationartner(InvestorEvaluation investorEvaluation) {
        // TODO Auto-generated method stub
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        Investor self = getInvestorByLogin();
        if (self == null) {
            map.put(0, "对不起,您未登陆或没有权限!");
            return map;
        }
        Float count = (Float) totalDao.getObjectByCondition("select count(*) from InvestorEvaluation where iOfQpId=? and iOfQpId2=?", investorEvaluation.getIofQpId(), self.getId());
        if (count != null && count >= 3) {
            map.put(0, "对不起您对此组长的评价已达到上限,最多三条!");
            return map;
        }
        InvestorOfQuotedPrice iOfQp = (InvestorOfQuotedPrice) totalDao.getObjectById(InvestorOfQuotedPrice.class, investorEvaluation.getIofQpId());
        if (iOfQp == null) {
            map.put(0, "对不起,没有找到被评价的投资者!");
            return map;
        }
        if (iOfQp.getRole() == null || !iOfQp.getRole().equals("组长")) {
            map.put(0, "对不起,没有找到被评价的投资者不是组长!");
            return map;
        }
        Investor zuzhang = (Investor) totalDao.getObjectById(Investor.class, investorEvaluation.getIofQpId());
        if (zuzhang == null) {
            map.put(0, "对不起,没有找到对应的投资者!");
            return map;
        }
        if (investorEvaluation.getType() == null) {
            map.put(0, "对不起,请先选评价类型!");
            return map;
        } else if (investorEvaluation.getType().equals("吐槽")) {
            if (zuzhang.getTocaoCount() == null) {
                zuzhang.setTocaoCount(1);
            } else {
                zuzhang.setTocaoCount(zuzhang.getTocaoCount() + 1);
            }
            totalDao.update(zuzhang);
        } else if (investorEvaluation.getType().equals("点赞")) {
            if (zuzhang.getZanCount() == null) {
                zuzhang.setZanCount(1);
            } else {
                zuzhang.setZanCount(zuzhang.getZanCount() + 1);
            }
            totalDao.update(zuzhang);
        } else {
            map.put(0, "对不起,选评价类型有误!");
            return map;
        }
        map.put(1, iOfQp.getQpId());
        investorEvaluation.setIofQpId2(self.getId());
        investorEvaluation.setQpId(iOfQp.getQpId());
        investorEvaluation.setInvestorId(iOfQp.getInvestorId());
        totalDao.save(investorEvaluation);
        map.put(0, "true");
        return map;
    }

    @Override
    public List<QuotedPriceCost> findQpTrueCostList(int id) {
        // TODO Auto-generated method stub
        List<QuotedPriceCost> qpCostList = totalDao.query("from QuotedPriceCost where qpId=? and money>0 and source!='预算申报'", id);
        if (qpCostList != null && qpCostList.size() > 0) {
            Double totalMoney = 0d;
            for (QuotedPriceCost qpCost : qpCostList) {
                if (qpCost.getMoney() != null) {
                    totalMoney = totalMoney + qpCost.getMoney();
                }
                qpCost.setTotalMoney(Float.parseFloat(totalMoney + ""));
            }
        }
        return qpCostList;
    }

    public String pladdQuotedPrice(File addquotedPrice, String fileName, QuotedPrice quotedPrice
    		,String productStyle) {
        // TODO Auto-generated mSethod stub
        String jymsg = "";
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录！";
        }
        String fileRealPath = ServletActionContext.getServletContext()
                .getRealPath("/upload/file/bomxls");

        File file = new File(fileRealPath);
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(fileRealPath + "/" + fileName);
        try {
            FileCopyUtils.copy(addquotedPrice, file2);

            // 开始读取excel表格
            InputStream is = new FileInputStream(file2);// 创建文件流
            jxl.Workbook rwb = Workbook.getWorkbook(is);// 创建workBook
            Sheet rs = rwb.getSheet(0);// 获得第二张Sheet表
            int excelcolumns = rs.getRows();// 获得总行数
            if (quotedPrice != null && quotedPrice.getId()!=null) {
                quotedPrice = (QuotedPrice) totalDao.get(QuotedPrice.class, quotedPrice.getId());
            }
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
                List<QuotedPrice> qpList = new ArrayList<QuotedPrice>();
                boolean isbegin = false;
                int base = 0;
             // key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
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
//                    if (number.equals("1")) {
//                        isbegin = true;
//                    } else if (!isbegin) {
//                        base++;
//                        continue;
//                    }
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
                    QuotedPrice qp = new QuotedPrice();
                    qp.setYwmrkId(ywMarkId);
                    String markId = cells[3].getContents();// 件号
                    if (markId != null) {
                        markId = markId.replaceAll(" ", "");
                        markId = markId.replaceAll("	", "");
                        markId = markId.replaceAll("_", "-");
                    }
                    String hwdrMarkId = cells[2].getContents();// 件号
                    if (hwdrMarkId != null) {
                        hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
                        hwdrMarkId = hwdrMarkId.replaceAll("	", "");
                        hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
                        hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
                    }
                    boolean zouzui = true;
                    if (markId == null || markId.length() == 0) {
                        jymsg += "第" + (i + 1) + "行,件号不能为空!</br>";
                    } else{
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
                            && hwdrMarkId != null && hwdrMarkId.length() > 0) {
                    		zouzui = false;
                    		markId = hwdrMarkId;
                    	}
                    }
                    if (markId.startsWith("DKBA") && markId.indexOf(".") < 0) {
                        markId = markId.substring(0, 5) + "."
                                + markId.substring(5, 8) + "."
                                + markId.substring(8, markId.length());
                    }
                    String banben = "";
                    try {
                        banben = cells[4].getContents();// 版本
                    } catch (Exception e1) {
                    }
                    String proName = cells[5].getContents();// 名称
                    proName = proName.replaceAll(" ", "");
                    proName = proName.replaceAll("	", "");
                    Integer belongLayer = null;// 层数
                    Float corrCount = null;// 消耗量
                    for (int jc = 6; jc <= 15; jc++) {
                        String xiaohao = cells[jc].getContents();
                        if (xiaohao != null && xiaohao.length() > 0) {
                            try {
                                corrCount = Float.parseFloat(xiaohao);
                                belongLayer = jc - 5;
                            } catch (Exception e) {
                                e.printStackTrace();
                                jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
                            }
                        }
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
                        loadMarkId = cells[24].getContents();// 初始总成(备注)
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loadMarkId = loadMarkId.replaceAll(" ", "");
                    loadMarkId = loadMarkId.replaceAll("	", "");
                    loadMarkId = loadMarkId.replaceAll("（", "(");
                    loadMarkId = loadMarkId.replaceAll("）", ")");
                    loadMarkId = loadMarkId.replaceAll("x", "X");
                    String wgType = null;// 外购件类型
                    try {
                        wgType = cells[25].getContents();// 外购件类型
                        wgType = wgType.replaceAll(" ", "");
                        wgType = wgType.replaceAll("	", "");
                    } catch (Exception e) {
                        e.printStackTrace();
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
                        banben = banben
                                .replaceAll(String.valueOf((char) n), "");
                        if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
                    }
                    qp.setSpecification(specification);
					qp.setProcardStyle("待定");
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
							qp.setSpecification(useguige);
							qp.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)));
							qp.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							qp.setSpecification(useguige);
							qp.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)));
							qp.setProcardStyle("外购");
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
								qp.setProcardStyle("外购");
								qp.setTuhao(markId + "-" + useguige);
								qp.setSpecification(useguige);
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
											qp.setProcardStyle("外购");
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
											qp.setProcardStyle("外购");
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
									qp.setSpecification(useguige);
									qp.setTuhao(markId + "-" + useguige);
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

									qp.setShowSize(loadMarkId);
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
                                        "select valueCode,valueName from CodeTranslation where keyCode=? and type='国标'",
                                        markId);
                        if (change != null && change.length == 2
                                && change[0] != null
                                && change[0].toString().length() > 0) {
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
                    if (markId != null && (markId.startsWith("DKBA0")|| markId
							.equals("DKBA3359"))) {
                        jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
                                + "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
                    }
                    qp.setBelongLayer(belongLayer);
                    qp.setProName(proName);
                    qp.setProcardStyle("待定");
                    if (source != null) {
                        if (source.equals("外购") || source.equals("半成品")) {
                            qp.setProcardStyle("外购");
                        } else if (source.equals("生产") || source.equals("自制")) {
                            qp.setProcardStyle("自制");
                        }
                    }
                    if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, qp
								.getProcardStyle());
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(qp.getProcardStyle())) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + qp.getProcardStyle()
									+ "]类型,:[" + procardStyle0
									+ "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
                    qp.setMarkId(markId);
                    qp.setBanBenNumber(banben);
                    qp.setUnit(unit);
                    qp.setCorrCount(corrCount);
                    qp.setClType(clType);
                    qp.setBiaochu(biaochu);
                    qp.setBzStatus("初始");
                    qp.setProductStyle(productStyle);
                    qp.setCardXiaoHao(1f);
                    qp.setLoadMarkId(loadMarkId);
                    qp.setXuhao(0f);
                    qp.setThisLength(thislength);
                    qp.setThisWidth(thiswidth);
                    qp.setThisHight(thishigth);
                    qp.setQuotedNumber(quotedPrice.getQuotedNumber());
                    if ("外购".equals(qp.getProcardStyle())) {
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
                    if (qp.getProcardStyle().equals("外购")
							|| qp.getProcardStyle().equals("半成品")) {
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
                    qp.setWgType(wgType);
                    qpList.add(qp);
                }
                if (qpList.size() > 0) {
                    QuotedPrice totalqp = qpList.get(0);
                    totalqp.setProcardStyle("总成");
                    if (totalqp.getMarkId() != null && quotedPrice.getMarkId() != null
                            && !totalqp.getMarkId().equals(quotedPrice.getMarkId())) {
                        return "总成件号不一样,请核实文件";
                    } else {
                        BeanUtils.copyProperties(totalqp, quotedPrice, new String[]{"id", "quotedNumber", "procardLifeCycle",
                                "epId", "quotedPrice", "quotedPriceSet", "qpinfor", "filnalCount", "procardTime", "zhikaren", "barcode", "status"});
                    }
					QuotedPrice same = (QuotedPrice) totalDao
							.getObjectByCondition(
									"from QuotedPrice where markId=? and procardStyle='总成' and productStyle='批产'",
									totalqp.getMarkId());
					if (same != null) {
						throw new RuntimeException("该总成件号已存在,导入失败!");
					}
                    if (jymsg.length() > 0) {
                        return jymsg;
                    }
                    
                    if(quotedPrice.getId()!=null){
                    	totalDao.update(quotedPrice);
                    }else{
                    	totalDao.save(quotedPrice);      
                    }
                    quotedPrice.setRootId(quotedPrice.getId());
                	totalDao.update(quotedPrice);
                    String msg = "";
                    if (qpList.size() > 1) {
                        if (ywMarkId != null && ywMarkId.length() > 0) {
                            msg += addQuotedPriceTree(quotedPrice, qpList,
                                    quotedPrice.getId(), 0, quotedPrice.getMarkId(),
                                    ywMarkId, base, 2, user);
                        } else {
                            msg += addQuotedPriceTree(quotedPrice, qpList,
                                    quotedPrice.getId(), 0, quotedPrice.getMarkId(),
                                    ywMarkId, base, 2, user);
                        }
                    }
                    if (msg.length() > 0) {
                        throw new RuntimeException(msg);
                    } else {
                        for (QuotedPrice hasHistory : qpList) {
                            if (hasHistory.getHistoryId() != null) {
                                List<QuotedPrice> historySonList = (List<QuotedPrice>) totalDao
                                        .query(
                                                "from QuotedPrice where fatherId=?",
                                                hasHistory.getHistoryId());
                                if (historySonList != null
                                        && historySonList.size() > 0) {
                                    for (QuotedPrice historySon : historySonList) {
                                        saveCopyQuotedPrice(hasHistory, historySon);
                                    }
                                }
                            }
                        }
                        return "导入成功!";

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
        return "导入成功";
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

	public String addQuotedPriceTree(QuotedPrice fatherqp,
                                     List<QuotedPrice> qpList, Integer rootId, Integer i,
                                     String zcMarkId, String ywmarkId, int base, int bomtype, Users user) {
        String msg = "";
        i++;
        Set<QuotedPrice> sonSet = new HashSet<QuotedPrice>();
        List<String> markIdList = new ArrayList<String>();
        Float xh = 0f;
        String time = Util.getDateTime();
        for (int j = i; j < qpList.size(); j++) {
            boolean isHistorySon = false;// 已有组合跳过下层，已原有的为准
            QuotedPrice sonPt = qpList.get(j);
            // 判断是否为原材料
            YuanclAndWaigj ycl = null;
            if (sonPt.getSpecification() != null) {
                if (sonPt.getTuhao() == null || sonPt.getTuhao().length() == 0) {
                    ycl = (YuanclAndWaigj) totalDao
                            .getObjectByCondition(
                                    "from YuanclAndWaigj where trademark=? and specification=?  and clClass='原材料'",
                                    sonPt.getMarkId(), sonPt.getSpecification());
                } else {
                    ycl = (YuanclAndWaigj) totalDao
                            .getObjectByCondition(
                                    "from YuanclAndWaigj where trademark=? and specification=? and tuhao =? and clClass='原材料'",
                                    sonPt.getMarkId(),
                                    sonPt.getSpecification(), sonPt.getTuhao());
                    if (ycl == null) {
                        YuanclAndWaigj ycl2 = (YuanclAndWaigj) totalDao
                                .getObjectByCondition(
                                        "from YuanclAndWaigj where tuhao =?  and clClass='原材料'",
                                        sonPt.getTuhao());
                        if (ycl2 != null) {
                            msg += "第" + (j + base + 1) + "行错误,牌号为:"
                                    + ycl2.getMarkId() + "规格为:"
                                    + ycl2.getSpecification() + "的原材料的图号"
                                    + sonPt.getTuhao() + "已被占用!<br/>";
                        }
                        YuanclAndWaigj ycl3 = (YuanclAndWaigj) totalDao
                                .getObjectByCondition(
                                        "from YuanclAndWaigj where trademark=? and specification=?  and clClass='原材料'",
                                        sonPt.getMarkId(), sonPt
                                                .getSpecification());
                        if (ycl3 != null) {
                            msg += "第" + (j + base + 1) + "行错误,牌号为:"
                                    + ycl3.getMarkId() + "规格为:"
                                    + ycl3.getSpecification() + "的原材料的图号"
                                    + sonPt.getTuhao() + "与库中图号:"
                                    + ycl3.getTuhao() + "不同!<br/>";
                        }
                        if (ycl2 != null || ycl3 != null) {
                            continue;
                        }
                    }
                }
            }
            if (sonPt.getBelongLayer().equals(fatherqp.getBelongLayer() + 1)) {
            	sonPt.setProductStyle(fatherqp.getProductStyle());
                if (sonPt.getProcardStyle().equals("原材料")) {// 已标记为原材料
                    if (ycl != null) {
                        if (ycl.getStatus() != null
                                && ycl.getStatus().equals("禁用")) {
                            msg += "原材料" + ycl.getMarkId() + "的"
                                    + ycl.getSpecification() + "规格已禁用!";
                        } else {
                            sonPt.setBili(ycl.getBili());
                            fatherqp.setTrademark(sonPt.getMarkId());
                            fatherqp.setYuanName(sonPt.getProName());
                            fatherqp.setSpecification(sonPt.getSpecification());
                            fatherqp.setYuanUnit(ycl.getUnit());
                            fatherqp.setYtuhao(ycl.getTuhao());
                            fatherqp.setBili(ycl.getBili());
                            fatherqp.setQuanzi1(1f);
                            fatherqp.setQuanzi2(sonPt.getCorrCount());
                            fatherqp.setThisLength(sonPt.getThisLength());
                            fatherqp.setThisWidth(sonPt.getThisWidth());
                            fatherqp.setThisHight(sonPt.getThisHight());
                        }
                    } else {
                        YuanclAndWaigj ycl2 = null;
                        if (sonPt.getTuhao() != null && sonPt.getTuhao().length() > 0) {
                            ycl2 = (YuanclAndWaigj) totalDao
                                    .getObjectByCondition(
                                            "from YuanclAndWaigj where tuhao =? and clClass='原材料'",
                                            sonPt.getTuhao());
                            if (ycl2 != null) {
                                msg += "第" + (j + base + 1) + "行错误,牌号为:"
                                        + sonPt.getMarkId() + "规格为:"
                                        + sonPt.getSpecification() + "的原材料的图号"
                                        + sonPt.getTuhao() + "已被占用!<br/>";
                            }
                        }
                        YuanclAndWaigj ycl3 = (YuanclAndWaigj) totalDao
                                .getObjectByCondition(
                                        "from YuanclAndWaigj where trademark=? and specification=?  and clClass='原材料'",
                                        sonPt.getMarkId(), sonPt
                                                .getSpecification());
                        if (ycl3 != null) {
                            msg += "第" + (j + base + 1) + "行错误,牌号为:"
                                    + ycl3.getMarkId() + "规格为:"
                                    + ycl3.getSpecification() + "的原材料的图号"
                                    + sonPt.getTuhao() + "与库中图号:"
                                    + ycl3.getTuhao() + "不同!<br/>";
                        }
                        if (ycl2 != null || ycl3 != null) {
                            continue;
                        }
//						YuanclAndWaigj yclTemp = new YuanclAndWaigj();
//						yclTemp.setName(sonPt.getProName());// 名称
//						yclTemp.setMarkId(sonPt.getMarkId());// 牌号
//						yclTemp.setSpecification(sonPt.getSpecification());// 规格
//						yclTemp.setTuhao(sonPt.getTuhao());// 图号
//						yclTemp.setUnit(sonPt.getUnit());// BOM用的单位
//						// yclTemp.setckUnit;//仓库用的单位
//						yclTemp.setBanbenhao(sonPt.getBanBenNumber());// 版本号
//						// yclTemp.setzcMarkId;//总成号
//						yclTemp.setClClass("原材料");// 材料类型（外购件,原材料,辅料）
//						yclTemp.setCaizhi(sonPt.getClType());// 材质类型（如：五金、塑胶。。。。）
//						yclTemp.setBili(sonPt.getBili());// 比重（1块材料多少kg）
//						// yclTemp.setremark;//备注
//						yclTemp.setStatus("使用");// 状态（使用,已确认,禁用,默认使用）
//						yclTemp.setProductStyle("报价");
//						yclTemp.setPricestatus("新增");
//						yclTemp.setBanbenStatus("默认");
//						yclTemp.setAddTime(time);
//						yclTemp.setAddUserCode(user.getCode());
//						yclTemp.setAddUserName(user.getName());
//						totalDao.save(yclTemp);
                        fatherqp.setTrademark(sonPt.getMarkId());
                        fatherqp.setYuanName(sonPt.getProName());
                        fatherqp.setSpecification(sonPt.getSpecification());
                        fatherqp.setYuanUnit(sonPt.getUnit());
                        fatherqp.setYtuhao(sonPt.getTuhao());
                        fatherqp.setBili(sonPt.getBili());
                        fatherqp.setQuanzi1(1f);
                        fatherqp.setQuanzi2(sonPt.getCorrCount());
                        fatherqp.setThisLength(sonPt.getThisLength());
                        fatherqp.setThisWidth(sonPt.getThisWidth());
                        fatherqp.setThisHight(sonPt.getThisHight());
                    }
                    if (fatherqp.getCardXiaoHao() != null
                            && sonPt.getCorrCount() != null) {
                        fatherqp.setXiaohaoCount(fatherqp.getCardXiaoHao()
                                * sonPt.getCorrCount());
                    } else {
                        fatherqp.setXiaohaoCount(0f);
                    }
                    continue;
                } else if (ycl != null) {
                    if (ycl.getStatus() != null && ycl.getStatus().equals("禁用")) {
                        msg += "第" + (j + base + 1) + "行错误,原材料"
                                + ycl.getMarkId() + "的"
                                + ycl.getSpecification() + "规格已禁用!<br/>";
                    }
                    fatherqp.setTrademark(sonPt.getMarkId());
                    fatherqp.setYuanName(sonPt.getProName());
                    fatherqp.setYtuhao(ycl.getTuhao());
                    fatherqp.setBili(ycl.getBili());
                    fatherqp.setSpecification(sonPt.getSpecification());
                    fatherqp.setYuanUnit(ycl.getUnit());
                    fatherqp.setQuanzi1(1f);
                    fatherqp.setQuanzi2(sonPt.getCorrCount());
                    fatherqp.setThisLength(sonPt.getThisLength());
                    fatherqp.setThisWidth(sonPt.getThisWidth());
                    fatherqp.setThisHight(sonPt.getThisHight());
                    if (fatherqp.getCardXiaoHao() != null
                            && sonPt.getCorrCount() != null) {
                        fatherqp.setXiaohaoCount(fatherqp.getCardXiaoHao()
                                * sonPt.getCorrCount());
                    } else {
                        fatherqp.setXiaohaoCount(0f);
                    }
                    continue;
                }
                if (fatherqp.getMarkId().equals(sonPt.getMarkId())) {
                    msg += "第" + (i + base) + "行和第" + (j + base)
                            + "行错误,子零件件号不能和父零件件号相同!<br/>";
                }
                if (markIdList.contains(sonPt.getMarkId())) {
                    msg += "第" + (j + base + 1) + "行错误,同一父零件下不能有相同件号的零件!<br/>";
                }
                if (sonPt.getProcardStyle().equals("半成品")) {
                    sonPt.setProcardStyle("外购");
                    sonPt.setNeedProcess("yes");
                }
                ProcardTemplate historyPt = null;// 查看该件号之前是否存在(借用批产)
                ProcardTemplate historyPtother = null;// 查看该件号之前是否存在不同版本
                String sqlhistory = "from ProcardTemplate where markId=? and productStyle='批产' and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or  dataStatus!='删除')";
                String sqlhistoryOther = "from ProcardTemplate where markId=? and productStyle='批产' and (banbenStatus is null or banbenStatus ='默认') and (dataStatus is null or dataStatus!='删除')";
                if (sonPt.getBanBenNumber() == null
                        || sonPt.getBanBenNumber().length() == 0) {// 无版本号
                    sqlhistory += " and (banBenNumber is null or banBenNumber='')";
                    sqlhistoryOther += "and banBenNumber is not null and banBenNumber!=''";
                } else {
                    sqlhistory += " and banBenNumber ='"
                            + sonPt.getBanBenNumber() + "'";
                    sqlhistoryOther += " and (banBenNumber is null or banBenNumber!='"
                            + sonPt.getBanBenNumber() + "')";
                }
                historyPt = (ProcardTemplate) totalDao
                        .getObjectByCondition(sqlhistory, sonPt.getMarkId());
                if (historyPt != null) {
                    isHistorySon = true;// 已有
//					sonPt.set
                    BeanUtils
                            .copyProperties(historyPt, sonPt, new String[]{
                                    "id", "procardTemplate", "procardTSet",
                                    "processTemplate", "zhUsers",
                                    "quanzi1", "quanzi2", "maxCount",
                                    "corrCount", "rootId", "fatherId",
                                    "belongLayer", "cardXiaoHao",
                                    "loadMarkId", "ywMarkId", "historyId",
                                    "kgliao", "thisLength", "thisWidth",
                                    "thisHight"});
                    sonPt.setYwmrkId(ywmarkId);
                    sonPt.setHistoryId(historyPt.getId());
                    if (sonPt.getProcardStyle() != null
                            && !sonPt.getProcardStyle().equals("外购件")) {// 非外购件连带长宽高
                        sonPt.setQuanzi1(historyPt.getQuanzi1());
                        sonPt.setQuanzi2(historyPt.getQuanzi2());
                        sonPt.setThisLength(historyPt.getThisLength());
                        sonPt.setThisWidth(historyPt.getThisWidth());
                        sonPt.setThisHight(historyPt.getThisHight());
                    }
                    // 同步工序
                    saveCopyProcess(sonPt, historyPt);

                } else {
                    historyPtother = (ProcardTemplate) totalDao
                            .getObjectByCondition(sqlhistoryOther, sonPt
                                    .getMarkId());
                    if (historyPtother != null) {
                        isHistorySon = true;// 已有
//						sonPt.set
                        BeanUtils
                                .copyProperties(historyPtother, sonPt, new String[]{
                                        "id", "procardTemplate", "procardTSet",
                                        "processTemplate", "zhUsers",
                                        "quanzi1", "quanzi2", "maxCount",
                                        "corrCount", "rootId", "fatherId",
                                        "belongLayer", "cardXiaoHao",
                                        "loadMarkId", "ywMarkId", "historyId",
                                        "kgliao", "thisLength", "thisWidth",
                                        "thisHight"});
                        sonPt.setYwmrkId(ywmarkId);
                        sonPt.setHistoryId(historyPtother.getId());
                        if (sonPt.getProcardStyle() != null
                                && !sonPt.getProcardStyle().equals("外购件")) {// 非外购件连带长宽高
                            sonPt.setQuanzi1(historyPtother.getQuanzi1());
                            sonPt.setQuanzi2(historyPtother.getQuanzi2());
                            sonPt.setThisLength(historyPtother.getThisLength());
                            sonPt.setThisWidth(historyPtother.getThisWidth());
                            sonPt.setThisHight(historyPtother.getThisHight());
                        }
                        // 同步工序
                        saveCopyProcess(sonPt, historyPtother);
                    } else {
                        sonPt.setYwmrkId(ywmarkId);

                    }
                }

                if (sonPt.getBanci() == null) {
                    sonPt.setBanci(0);
                }
                // 设置总成件号
                sonPt.setRootMarkId(zcMarkId);
                if (sonPt.getProcardStyle() != null
                        && sonPt.getProcardStyle().equals("外购")) {
                    // 如果是外购件就去外购件原材料库里去查原来有没有，如果没有就添加一条记录到外购件原材料库
                    String wgjSql = "from YuanclAndWaigj where markId=?";
                    if (sonPt.getBanBenNumber() == null
                            || sonPt.getBanBenNumber().length() == 0) {
                        wgjSql += " and (banbenhao is null or banbenhao='')";
                    } else {
                        wgjSql += " and banbenhao ='" + sonPt.getBanBenNumber()
                                + "'";
                    }
                    YuanclAndWaigj wgjTem = (YuanclAndWaigj) totalDao
                            .getObjectByCondition(wgjSql, sonPt.getMarkId());
                    if (wgjTem == null) {
//						wgjTem = new YuanclAndWaigj();
//						wgjTem.setMarkId(sonPt.getMarkId());// 件号
//						wgjTem.setName(sonPt.getProName());// 名称
//						wgjTem.setSpecification(sonPt.getSpecification());// 规格
//						wgjTem.setUnit(sonPt.getUnit());// BOM用的单位
//						wgjTem.setCkUnit(sonPt.getUnit());// 仓库用的单位
//						wgjTem.setBanbenhao(sonPt.getBanBenNumber());// 版本号
//						wgjTem.setZcMarkId(sonPt.getLoadMarkId());// 总成号
//						wgjTem.setClClass("外购件");// 材料类型（外购件,原材料,辅料）
//						wgjTem.setCaizhi(sonPt.getClType());// 材质类型
//						wgjTem.setTuhao(sonPt.getTuhao());// 图号
//						wgjTem.setProductStyle("报价");
//						wgjTem.setAddTime(time);
//						wgjTem.setAddUserCode(user.getCode());
//						wgjTem.setAddUserName(user.getName());
//						wgjTem.setPricestatus("新增");
//						wgjTem.setBanbenStatus("默认");
//						if (sonPt.getKgliao() == null
//								|| sonPt.getKgliao().length() == 0) {// 导入时没有以自购为默认
//							wgjTem.setKgliao("自购");// 供料属性
//							sonPt.setKgliao("自购");// 供料属性
//						} else {
//							sonPt.setKgliao(sonPt.getKgliao());// 供料属性
//							wgjTem.setKgliao(sonPt.getKgliao());// 供料属性
//						}
//						totalDao.save(wgjTem);
                    } else if (wgjTem.getStatus() != null
                            && wgjTem.getStatus().equals("禁用")) {
                        msg += "外购件" + wgjTem.getMarkId() + "的"
                                + wgjTem.getSpecification() + "规格的"
                                + wgjTem.getBanbenhao() + "版本已禁用!<br/>";
                    } else {
                        if (sonPt.getKgliao() == null
                                || sonPt.getKgliao().length() == 0) {// 导入时没有以库中为默认
                            if (wgjTem.getKgliao() == null
                                    || wgjTem.getKgliao().length() == 0) {
                                wgjTem.setKgliao("自购");// 供料属性
                            }
                            if (wgjTem.getWgType() == null || wgjTem.getWgType().length() == 0) {
                                wgjTem.setWgType(sonPt.getWgType());
                            }
                            totalDao.update(wgjTem);
                            sonPt.setKgliao(wgjTem.getKgliao());// 供料属性
                        }
                    }
                   sonPt.setProductStyle(fatherqp.getProcardStyle());
                    String toDay = Util.getDateTime("yyyy-MM-dd");
                    String hql = "";
                    if("试制".equals(sonPt.getProductStyle())){
                    	  hql = "select max(hsPrice) from Price where partNumber = ? and pricePeriodStart<=? and pricePeriodEnd>=? order by hsPrice";
                    }else{
                    	  hql = "select min(hsPrice) from Price where partNumber = ? and pricePeriodStart<=? and pricePeriodEnd>=? order by hsPrice";	
                    }
                    Object wgPrice = totalDao.getObjectByCondition(hql, sonPt.getMarkId(), toDay, toDay);
                    if (wgPrice != null) {
                        sonPt.setWaigouPrice((float) Double.parseDouble(wgPrice.toString()));
                    }
                }
                sonPt.setFatherId(fatherqp.getId());
                sonPt.setRootId(rootId);
                sonPt.setQuotedPrice(fatherqp);
                sonPt.setXuhao(xh);
                if (sonPt.getProcardStyle().equals("外购")
                        || sonPt.getProcardStyle().equals("待定")) {
                    sonPt.setQuanzi1(1f);
                    sonPt.setQuanzi2(sonPt.getCorrCount());
                }
                if (fatherqp.getCardXiaoHao() != null
                        && sonPt.getCorrCount() != null) {
                    sonPt.setCardXiaoHao(fatherqp.getCardXiaoHao()
                            * sonPt.getCorrCount());
                } else {
                    sonPt.setCardXiaoHao(0f);
                }
                xh++;
                sonSet.add(sonPt);
                markIdList.add(sonPt.getMarkId());
                totalDao.save(sonPt);
                if (qpList.size() > (j + 1) && !isHistorySon) {
                    msg += addQuotedPriceTree(sonPt, qpList, rootId, j,
                            zcMarkId, ywmarkId, base, bomtype, user);
                }
            } else if (sonPt.getBelongLayer() > (fatherqp.getBelongLayer() + 1)) {
                continue;
            } else {
                break;
            }
        }
        if (sonSet.size() > 0 && !fatherqp.getProcardStyle().equals("总成")) {
            if (fatherqp.getProcardStyle().equals("外购")) {
                msg += "外购件" + fatherqp.getMarkId() + "下不能有零件!<br/>";
            } else {
                fatherqp.setProcardStyle("自制");
            }
        }
        fatherqp.setQuotedPriceSet(sonSet);
        totalDao.update(fatherqp);
        return msg;
    }

    @Override
    public List<Price> findAllOutPrice(String markId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List findQuotedPriceList(String markId, Integer pageNo, Integer pageSize) {
        // TODO Auto-generated method stub
        if (markId == null || "".equals(markId))
            return null;
        if (pageNo == null || pageNo == 0) pageNo = 1;
        if (pageSize == null || pageSize == 0) pageNo = 15;
        List list = totalDao.findAllByPage("from QuotedPrice where markId like '%" + markId + "%' order by id desc", pageNo, pageSize);
        return list;
    }

    @Override
    public List findOutSourcingAppList(String markId, Integer pageNo,
                                       Integer pageSize) {
        // TODO Auto-generated method stub
        if (markId == null || "".equals(markId))
            return null;
        if (pageNo == null || pageNo == 0) pageNo = 1;
        if (pageSize == null || pageSize == 0) pageNo = 15;
        List list = totalDao.findAllByPage("from OutSourcingApp where markId like '%" + markId + "%' order by id desc", pageNo, pageSize);
        return list;
    }

    @Override
    public List findBargainList(String markId, Integer pageNo,
                                Integer pageSize) {
        // TODO Auto-generated method stub
        if (markId == null || "".equals(markId))
            return null;
        if (pageNo == null || pageNo == 0) pageNo = 1;
        if (pageSize == null || pageSize == 0) pageNo = 15;
        List list = totalDao.findAllByPage("from Bargain where id in (select bargain.id from BargainGoods where goods_format like '%" + markId + "%') order by id desc", pageNo, pageSize);
        return list;
    }

    @Override
    public List findPerformsingleList(String markId, Integer pageNo,
                                      Integer pageSize) {
        // TODO Auto-generated method stub
        if (markId == null || "".equals(markId))
            return null;
        if (pageNo == null || pageNo == 0) pageNo = 1;
        if (pageSize == null || pageSize == 0) pageNo = 15;
        List list = totalDao.findAllByPage("from Performsingle where id in (select performsingle.id from PerformsingleDetail where gx_number like '%" + markId + "%') order by id desc", pageNo, pageSize);
        return list;
    }

    @Override
    public List findBarContractList(String markId, Integer pageNo,
                                    Integer pageSize) {
        // TODO Auto-generated method stub
        if (markId == null || "".equals(markId))
            return null;
        if (pageNo == null || pageNo == 0) pageNo = 1;
        if (pageSize == null || pageSize == 0) pageNo = 15;
        List list = totalDao.findAllByPage("from BarContract where id in (select barContract.id from BarContractDetails where gx_number like '%" + markId + "%') order by id desc", pageNo, pageSize);
        return list;
    }

    @Override
    public List findPricesList(String markId, Integer pageNo,
                               Integer pageSize) {
        // TODO Auto-generated method stub
        if (markId == null || "".equals(markId))
            return null;
        if (pageNo == null || pageNo == 0) pageNo = 1;
        if (pageSize == null || pageSize == 0) pageNo = 15;
        List list = totalDao.findAllByPage("from Price where partNumber like '%" + markId + "%' order by id desc", pageNo, pageSize);
        return list;
    }

    @Override
    public List<Object> findQuotedPriceTz(int id) {
        // TODO Auto-generated method stub
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                QuotedPrice.class, id);
        if (qp != null) {
            String banBenNumbersql = null;
            if (qp.getBanBenNumber() == null || qp.getBanBenNumber().length() == 0) {
                banBenNumbersql = " and (banBenNumber is null or banBenNumber='')";
            } else {
                banBenNumbersql = " and banBenNumber is not null and banBenNumber='" + qp.getBanBenNumber() + "'";
            }
            return totalDao
                    .query(
                            "from QuotedPriceFile where processNO is null  and markId=? " + banBenNumbersql, qp
                                    .getMarkId());
        } else {
            return null;
        }
    }

    @Override
    public String saveQuotedPriceFile(QuotedPriceFile quotedPriceFile, int id,
                                      String ytRadio) {
        // TODO Auto-generated method stub
        quotedPriceFile.setStatus("默认");
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(
                QuotedPrice.class, id);
        Users user = Util.getLoginUser();
        if (qp != null) {
            Integer banci = qp.getBanci();
            if (banci == null) {
                banci = 0;
            }
            if (ytRadio != null && ytRadio.equals("yes")) {
                Set<QuotedProcessInfor> processSet = qp.getQpinfor();
                if (processSet != null && processSet.size() > 0) {
                    for (QuotedProcessInfor process : processSet) {
                        QuotedPriceFile newfile = new QuotedPriceFile();
                        BeanUtils.copyProperties(quotedPriceFile, newfile,
                                new String[]{});
                        newfile.setProcessName(process.getProcessName());
                        newfile.setProcessNO(process.getProcessNO());
                        newfile.setMarkId(qp.getMarkId());
                        newfile.setBanBenNumber(qp.getBanBenNumber());
                        newfile.setBanci(banci);
                        newfile.setUserName(user.getName());// 上传人姓名
                        newfile.setUserCode(user.getCode());// 上传人工号
                        newfile.setAddTime(Util.getDateTime());// 上传时间
                        totalDao.save(newfile);
                    }
                    quotedPriceFile.setBanBenNumber(qp.getBanBenNumber());
                    quotedPriceFile.setProcessName(qp.getProName());
                    quotedPriceFile.setProcessNO(null);
                    quotedPriceFile.setMarkId(qp.getMarkId());
                    quotedPriceFile.setBanci(banci);
                    quotedPriceFile.setUserName(user.getName());// 上传人姓名
                    quotedPriceFile.setUserCode(user.getCode());// 上传人工号
                    quotedPriceFile.setAddTime(Util.getDateTime());// 上传时间
                    return totalDao.save(quotedPriceFile) + "";
                } else {
                    return "该零件下没有工序，不能上传原图!";
                }
            } else {
                quotedPriceFile.setBanBenNumber(qp.getBanBenNumber());
                quotedPriceFile.setUserName(user.getName());// 上传人姓名
                quotedPriceFile.setUserCode(user.getCode());// 上传人工号
                quotedPriceFile.setAddTime(Util.getDateTime());// 上传时间
                quotedPriceFile.setProcessName(qp.getProName());
                quotedPriceFile.setProcessNO(null);
                quotedPriceFile.setMarkId(qp.getMarkId());
                quotedPriceFile.setBanci(banci);
                return totalDao.save(quotedPriceFile) + "";
            }
        }
        return "没有找到目标零件!";
    }

    @Override
    public List<Object> getProcessTz(int id) {
        // TODO Auto-generated method stub
        QuotedProcessInfor process = findQpInfor(id);
        if (process != null && process.getQuotedPrice() != null) {
            String banBenNumbersql = null;
            if (process.getQuotedPrice().getBanBenNumber() != null && process.getQuotedPrice().getBanBenNumber().length() > 0) {
                banBenNumbersql = " and banBenNumber is not null and banBenNumber='" + process.getQuotedPrice().getBanBenNumber() + "'";
            } else {
                banBenNumbersql = " and (banBenNumber is null or banBenNumber='')";
            }
            return totalDao
                    .query(
                            "from QuotedPriceFile where processNO=? and processName=? and markId=? "
                                    + banBenNumbersql, process.getProcessNO(), process
                                    .getProcessName(), process
                                    .getQuotedPrice().getMarkId());
        }
        return null;
    }

    @Override
    public String saveQuotedPriceProcessFile(QuotedPriceFile quotedPriceFile,
                                             int id) {
        // TODO Auto-generated method stub
        quotedPriceFile.setStatus("默认");
        QuotedProcessInfor process = findQpInfor(id);
        Users user = Util.getLoginUser();
        if (process != null) {
            QuotedPrice qp = process.getQuotedPrice();
            if (qp != null) {
                Integer banci = qp.getBanci();
                if (banci == null) {
                    banci = 0;
                }
                quotedPriceFile.setProcessNO(process.getProcessNO());
                quotedPriceFile.setProcessName(process.getProcessName());
                quotedPriceFile.setUserName(user.getName());// 上传人姓名
                quotedPriceFile.setUserCode(user.getCode());// 上传人工号
                quotedPriceFile.setAddTime(Util.getDateTime());// 上传时间
                quotedPriceFile.setMarkId(qp.getMarkId());
                quotedPriceFile.setBanBenNumber(qp.getBanBenNumber());
                quotedPriceFile.setBanci(banci);
                return totalDao.save(quotedPriceFile) + "";
            }
        }
        return "没有找到目标零件!";
    }

    @Override
    public QuotedPriceFile findTzById(int id) {
        // TODO Auto-generated method stub
        return (QuotedPriceFile) totalDao.getObjectById(QuotedPriceFile.class, id);
    }

    @Override
    public boolean deleteTz(Integer id) {
        // TODO Auto-generated method stub
        QuotedPriceFile file = (QuotedPriceFile) totalDao
                .getObjectById(QuotedPriceFile.class, id);
        if (file != null) {
            // 同件号公用文件，此文件为下发的原图，原图的删除需要同步到整个零件
            List<QuotedPriceFile> fileList = totalDao.query(
                    "from QuotedPriceFile where markId=? and fileName=?", file.getMarkId(), file.getFileName());
            boolean b = true;
            for (QuotedPriceFile f : fileList) {
                b = b & totalDao.delete(f);
            }
            AttendanceTowServerImpl.updateJilu(7, file, file.getId(), "件号:"
                    + file.getMarkId() + "第" + file.getProcessNO() + "工序"
                    + file.getProcessName() + " " + file.getType() + " 报价图纸名:"
                    + file.getOldfileName() + "(删除)");
            return b;
        }
        return false;
    }

    @Override
    public String getIsbaomiByqpId(Integer id) {
        // TODO Auto-generated method stub
    	if(id==null){
    		return null;
    	}
        return (String) totalDao.getObjectByCondition("select isbaomi from ProjectManage where id =(select proId from QuotedPrice where id=?)", id);
    }

    @Override
    public String daorubmQuotedPrice(File bomFile, String fileName,
                                     QuotedPrice quotedPrice) {
        // TODO Auto-generated mSethod stub
        QuotedPrice oldtotal = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, quotedPrice.getId());
        String jymsg = "";
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录！";
        }
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
            List<QuotedPrice> qpList = new ArrayList<QuotedPrice>();
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
                String ywMarkId = row.getCell(2).toString();
                if (ywMarkId != null && ywMarkId.length() > 0) {
                    ywMarkId = ywMarkId.replaceAll(" ", "");
                    ywMarkId = ywMarkId.replaceAll("：", ":");
                    String[] ywMarkIds = ywMarkId.split(":");
                    if (ywMarkIds != null && ywMarkIds.length == 2) {
                        String repickStr = ywMarkIds[1];
                        String regex = "[\u4e00-\u9fa5]";
                        Pattern pat = Pattern.compile(regex);
                        Matcher mat = pat.matcher(repickStr);
                        ywMarkId = mat.replaceAll("");
                        String proName = repickStr.substring(repickStr.lastIndexOf(ywMarkId), repickStr.length());
                        QuotedPrice qp = new QuotedPrice();
                        qp.setYwmrkId(ywMarkId);
                        qp.setMarkId(ywMarkId);
                        qp.setProName(proName);
                        qp.setBelongLayer(1);
                        qpList.add(qp);
                    } else {
                        return "请先写件号或产品编码!";
                    }
                }
                boolean isbegin = false;
                int base = 0;
                // key 存初始件号+规格；list 0:原材料,外购件；1：前缀；2:编号;3,名称
				Map<String, List<String>> bmmap = new HashMap<String, List<String>>();
				Map<String, String> markIdAndProcardStyleMap = new HashMap<String, String>();
                for (int i = base; i <= excellastIndex; i++) {
                    row = sheet.getRow(i);// 获得每i行的所有单元格（返回的是数组）
                    String number = "";// 序号
                    try {
                        number = row.getCell(0).toString();//
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    number = number.replaceAll(" ", "");
                    number = number.replaceAll("	", "");
                    try {
                        Float num = Float.parseFloat(number);
                        isbegin = true;
                    } catch (Exception e) {
                        // TODO: handle exception
                        if (isbegin) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    QuotedPrice qp = new QuotedPrice();
                    qp.setYwmrkId(ywMarkId);
                    String markId =row.getCell(3).toString();// 件号
                    if (markId != null) {
                        markId = markId.replaceAll(" ", "");
                        markId = markId.replaceAll("	", "");
                        markId = markId.replaceAll("_", "-");
                    }
                    String hwdrMarkId =row.getCell(2).toString();// 件号
                    if (hwdrMarkId != null) {
                        hwdrMarkId = hwdrMarkId.replaceAll(" ", "");
                        hwdrMarkId = hwdrMarkId.replaceAll("	", "");
                        hwdrMarkId = hwdrMarkId.replaceAll("_", "-");
                        hwdrMarkId = hwdrMarkId.replaceAll("dkba", "DKBA");
                    }
                    boolean zouzui = true;
                    if (markId == null || markId.length() == 0) {
                        jymsg += "第" + (i + 1) + "行,件号不能为空!</br>";
                    } else{
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
                            && hwdrMarkId != null && hwdrMarkId.length() > 0) {
                    		zouzui = false;
                    		markId = hwdrMarkId;
                    	}
                    }
                    if (markId.startsWith("DKBA") && markId.indexOf(".") < 0) {
                        markId = markId.substring(0, 5) + "."
                                + markId.substring(5, 8) + "."
                                + markId.substring(8, markId.length());
                    }
                    String banben = "";
                    try {
                        banben =row.getCell(4).toString();// 版本
                    } catch (Exception e1) {
                    }
                    String proName =row.getCell(5).toString();// 名称
                    proName = proName.replaceAll(" ", "");
                    proName = proName.replaceAll("	", "");
                    Integer belongLayer = null;// 层数
                    Float corrCount = null;// 消耗量
                    for (int jc = 6; jc <= 15; jc++) {
                        String xiaohao =row.getCell(jc).toString();
                        if (xiaohao != null && xiaohao.length() > 0) {
                            try {
                                corrCount = Float.parseFloat(xiaohao);
                                belongLayer = jc - 5;
                            } catch (Exception e) {
                                e.printStackTrace();
                                jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
                            }
                        }
                    }
                    if (corrCount == null || corrCount == 0) {
                        jymsg += "第" + (i + 1) + "行,消耗量有异常!</br>";
                    }
                    String source =row.getCell(16).toString();// 来源
                    String unit =row.getCell(18).toString();// 单位
                    unit = unit.replaceAll(" ", "");
                    unit = unit.replaceAll("	", "");
                    String biaochu = "";
                    try {
                        biaochu =row.getCell(19).toString();// 表处
                        if ("A000".equals(biaochu)) {
							biaochu = "";
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String clType = "";
                    try {
                        clType =row.getCell(20).toString();// 材料类别（材质）
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    clType = clType.replaceAll(" ", "");
                    clType = clType.replaceAll("	", "");
                    String thislengthStr =row.getCell(21).toString();
                    String thiswidthStr =row.getCell(22).toString();
                    String thishigthStr =row.getCell(23).toString();
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
                        loadMarkId =row.getCell(24).toString();// 初始总成(备注)
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loadMarkId = loadMarkId.replaceAll(" ", "");
                    loadMarkId = loadMarkId.replaceAll("	", "");
                    loadMarkId = loadMarkId.replaceAll("（", "(");
                    loadMarkId = loadMarkId.replaceAll("）", ")");
                    loadMarkId = loadMarkId.replaceAll("x", "X");
                    String wgType = null;// 外购件类型
                    try {
                        wgType =row.getCell(25).toString(); // 外购件类型
                        wgType = wgType.replaceAll(" ", "");
                        wgType = wgType.replaceAll("	", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String specification = null;// 规格
					try {
						specification =row.getCell(26).toString();// 外购件类型
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
                        banben = banben
                                .replaceAll(String.valueOf((char) n), "");
                        if (specification != null && specification.length() > 0) {
							specification = specification.replaceAll(String
									.valueOf((char) n), "");
						}
                    }
                    qp.setSpecification(specification);
					qp.setProcardStyle("待定");
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
							qp.setSpecification(useguige);
							qp.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%05d", Integer
											.parseInt(sameMax.get(2)));
							qp.setProcardStyle("外购");
							// }

						} else if (sameMax.get(1).length() == 6) {
							// if (sameMax.get(0).equals("原材料")) {
							// markId = sameMax.get(1)
							// + String.format("%04d",
							// Integer.parseInt(sameMax
							// .get(2)) + 1);
							// } else {
							qp.setSpecification(useguige);
							qp.setTuhao(markId + "-" + useguige);
							markId = sameMax.get(1)
									+ String.format("%04d", Integer
											.parseInt(sameMax.get(2)));
							qp.setProcardStyle("外购");
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
								qp.setProcardStyle("外购");
								qp.setTuhao(markId + "-" + useguige);
								qp.setSpecification(useguige);
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
											qp.setProcardStyle("外购");
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
											qp.setProcardStyle("外购");
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
									qp.setSpecification(useguige);
									qp.setTuhao(markId + "-" + useguige);
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

									qp.setShowSize(loadMarkId);
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
                                        "select valueCode,valueName from CodeTranslation where keyCode=? and type='国标'",
                                        markId);
                        if (change != null && change.length == 2
                                && change[0] != null
                                && change[0].toString().length() > 0) {
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
                    if (markId != null && (markId.startsWith("DKBA0")|| markId
							.equals("DKBA3359"))) {
                        jymsg += "第" + (i + 1) + "行,技术规范类物料:" + markId
                                + "需要编号,请前往物料编码添加该技术规范类对照数据!</br>";
                    }
                    qp.setBelongLayer(belongLayer);
                    qp.setProName(proName);
                    qp.setProcardStyle("待定");
                    if (source != null) {
                        if (source.equals("外购") || source.equals("半成品")) {
                            qp.setProcardStyle("外购");
                        } else if (source.equals("生产") || source.equals("自制")) {
                            qp.setProcardStyle("自制");
                        }
                    }
                    if (markIdAndProcardStyleMap.get(markId) == null) {
						markIdAndProcardStyleMap.put(markId, qp
								.getProcardStyle());
					} else {
						String procardStyle0 = markIdAndProcardStyleMap
								.get(markId);
						if (!procardStyle0.equals(qp.getProcardStyle())) {
							jymsg += "第" + (i + 1) + "行错误，件号:" + markId
									+ "同时存在:[" + qp.getProcardStyle()
									+ "]类型,:[" + procardStyle0
									+ "]类型，不符合工艺规范请检查验证。<br/>";
						}
					}
                    qp.setMarkId(markId);
                    qp.setBanBenNumber(banben);
                    qp.setUnit(unit);
                    qp.setCorrCount(corrCount);
                    qp.setClType(clType);
                    qp.setBiaochu(biaochu);
                    qp.setBzStatus("初始");
                    qp.setCardXiaoHao(1f);
                    qp.setLoadMarkId(loadMarkId);
                    qp.setXuhao(0f);
                    qp.setThisLength(thislength);
                    qp.setThisWidth(thiswidth);
                    qp.setThisHight(thishigth);
                    qp.setQuotedNumber(quotedPrice.getQuotedNumber());
                    if ("外购".equals(qp.getProcardStyle())) {
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
                    if (qp.getProcardStyle().equals("外购")
							|| qp.getProcardStyle().equals("半成品")) {
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
                    qp.setWgType(wgType);
                    qpList.add(qp);
                }
                if (qpList.size() > 0) {
                    QuotedPrice totalqp = qpList.get(0);
                    if (totalqp.getMarkId() != null && oldtotal.getMarkId() != null
                            && !totalqp.getMarkId().equals(oldtotal.getMarkId())) {
                        return "总成件号不一样,请核实文件";
//					}else{
//							BeanUtils.copyProperties(totalqp, quotedPrice, new String[]{"id","quotedNumber","procardLifeCycle",
//									"epId","quotedPrice","quotedPriceSet","qpinfor","filnalCount","procardTime","zhikaren","barcode","status"});
                    }
//					QuotedPrice same = (QuotedPrice) totalDao
//							.getObjectByCondition(
//									"from QuotedPrice where markId=? and procardStyle='总成' and productStyle='批产'",
//									totalqp.getMarkId());
//					if (same != null) {
//						throw new RuntimeException("该总成件号已存在,导入失败!");
//					}
                    if (jymsg.length() > 0) {
                        return jymsg;
                    }
                    Set<QuotedPrice> oldSonqpSet = oldtotal.getQuotedPriceSet();
                    QuotedPrice newtotalqp = null;
                    if (oldSonqpSet != null && oldSonqpSet.size() > 0) {//有子项就隐藏之前BOM以此BOM作为新的
                        oldtotal.setDateStatus("历史");
                        setSonHistroy(oldSonqpSet);
                        totalqp.setProId(oldtotal.getProId());
                        BeanUtils.copyProperties(oldtotal, totalqp, new String[]{"id", "quotedNumber", "procardLifeCycle",
                                "epId", "quotedPrice", "quotedPriceSet", "qpinfor", "filnalCount", "procardTime", "zhikaren", "barcode", "status"});
                        newtotalqp = totalqp;
                        totalDao.save(newtotalqp);
                    } else {
                        oldtotal.setYwmrkId(ywMarkId);
                        newtotalqp = oldtotal;
                    }

//					quotedPrice.setRootId(quotedPrice.getId());
                    String msg = "";
                    if (qpList.size() > 1) {
                        if (ywMarkId != null && ywMarkId.length() > 0) {
                            msg += addQuotedPriceTree(newtotalqp, qpList,
                                    quotedPrice.getId(), 0, quotedPrice.getMarkId(),
                                    ywMarkId, base, 2, user);
                        } else {
                            msg += addQuotedPriceTree(newtotalqp, qpList,
                                    quotedPrice.getId(), 0, quotedPrice.getMarkId(),
                                    ywMarkId, base, 2, user);
                        }
                    }
                    if (msg.length() > 0) {
                        throw new RuntimeException(msg);
                    } else {
                        for (QuotedPrice hasHistory : qpList) {
                            if (hasHistory.getHistoryId() != null) {
                                List<QuotedPrice> historySonList = (List<QuotedPrice>) totalDao
                                        .query(
                                                "from QuotedPrice where fatherId=?",
                                                hasHistory.getHistoryId());
                                if (historySonList != null
                                        && historySonList.size() > 0) {
                                    for (QuotedPrice historySon : historySonList) {
                                        saveCopyQuotedPrice(hasHistory, historySon);
                                    }
                                }
                            }
                        }
                        return "导入成功!";

                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "文件异常,导入失败!";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "文件异常,导入失败!";
        }
        return "导入成功";
    }

    private void setSonHistroy(Set<QuotedPrice> oldSonqpSet) {
        // TODO Auto-generated method stub
        for (QuotedPrice qp : oldSonqpSet) {
            qp.setDateStatus("历史");
            totalDao.update(qp);
            Set<QuotedPrice> sonSet = qp.getQuotedPriceSet();
            if (sonSet != null && sonSet.size() > 0) {
                setSonHistroy(sonSet);
            }
        }
    }

    @Override
    public List<QuotedPricejy> findjyListByqpId(int id, String start, String end, String title, String jyContext, String code, String name) {
        // TODO Auto-generated method stub
        String sql = "from QuotedPricejy where qpId=? ";
        if (start != null && start.length() > 0) {
            sql += " and jyTime>='" + start + "'";
        }
        if (end != null && end.length() > 0) {
            sql += " and jyTime<='" + end + "'";
        }
        if (title != null && title.length() > 0 && !title.contains("delete")) {
            sql += "and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status='确认关闭'  and jyContext like '%" + jyContext + "%'";
        } else {
            sql += "and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status='确认关闭'";
        }
        if (code != null && code.length() > 0 && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' and userName like '%" + name + "%')";
        }
        if (code != null && code.length() > 0 && (name == null || name.length() == 0)) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' )";
        }
        if ((code == null || code.length() == 0) && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userName like '%" + name + "%')";
        }

        sql += "))";
        sql += "  order by jyTime desc";
        List<QuotedPricejy> qpList = totalDao.query(sql, id);
        if (qpList != null && qpList.size() > 0) {
            for (QuotedPricejy qpjy : qpList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        if (qpjyDetail.getStatus().equals("确认关闭")) {
                            List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                            StringBuffer sb = new StringBuffer();
                            for (QuotedPricejyUsers jyUser : jyUserList) {
                                if (sb.length() == 0) {
                                    sb.append(jyUser.getUserName());
                                } else {
                                    sb.append(";" + jyUser.getUserName());
                                }
                            }
                            qpjyDetail.setZxUsers(sb.toString());
                            qpjyDetailList.add(qpjyDetail);
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpList;
    }

    @Override
    public List<QuotedPricejy> findjyListnoqpId(String start, String end, String title, String jyContext, String code, String name) {
        // TODO Auto-generated method stub
        String sql = "from QuotedPricejy where qpId is null ";
        if (start != null && start.length() > 0) {
            sql += " and jyTime>='" + start + "'";
        }
        if (end != null && end.length() > 0) {
            sql += " and jyTime<='" + end + "'";
        }
        if (title != null && title.length() > 0 && !title.contains("delete")) {
            sql += "and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status='确认关闭'  and jyContext like '%" + jyContext + "%'";
        } else {
            sql += "and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status='确认关闭'";
        }
        if (code != null && code.length() > 0 && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' and userName like '%" + name + "%')";
        }
        if (code != null && code.length() > 0 && (name == null || name.length() == 0)) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' )";
        }
        if ((code == null || code.length() == 0) && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userName like '%" + name + "%')";
        }

        sql += "))";
        sql += "  order by jyTime desc";
        List<QuotedPricejy> qpList = totalDao.query(sql);
        if (qpList != null && qpList.size() > 0) {
            for (QuotedPricejy qpjy : qpList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        if (qpjyDetail.getStatus().equals("确认关闭")) {
                            List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                            StringBuffer sb = new StringBuffer();
                            for (QuotedPricejyUsers jyUser : jyUserList) {
                                if (sb.length() == 0) {
                                    sb.append(jyUser.getUserName());
                                } else {
                                    sb.append(";" + jyUser.getUserName());
                                }
                            }
                            qpjyDetail.setZxUsers(sb.toString());
                            qpjyDetailList.add(qpjyDetail);
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpList;
    }

    @Override
    public String addjy(QuotedPricejy qpjy, int id) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录!";
        }
        if (id == 0) {
            return "没有好到目标项目!";
        }
        String nowTime = Util.getDateTime();
        qpjy.setQpId(id);
        qpjy.setAddTime(nowTime);
        qpjy.setAddUserId(user.getId());
        qpjy.setAddUserCode(user.getCode());
        qpjy.setAddUserName(user.getName());
        qpjy.setAddUserDept(user.getDept());
        String before = "jy_" + Util.getDateTime("yyyyMMdd");
        String maxnumber = (String) totalDao.getObjectByCondition("select max(jyNumber) from QuotedPricejy where jyNumber like '" + before + "%'");
        if (maxnumber != null && !"".equals(maxnumber)) {
            String now_number = maxnumber.split(before)[1];
            Integer number2 = Integer.parseInt(now_number) + 1;
            DecimalFormat df = new DecimalFormat("000");
            String number3 = df.format(number2);
            qpjy.setJyNumber(before + number3);
        } else {
            qpjy.setJyNumber(before + "001");
        }
        if (qpjy.getQpjyDetailList() != null) {
            Set<QuotedPricejyDetail> qpjyDetailSet = new HashSet<QuotedPricejyDetail>();
            int xuhao = 0;
            for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailList()) {
                if (qpjyDetail.getJyContext() != null && qpjyDetail.getJyContext().length() > 0) {
                    qpjyDetail.setJyNumber(qpjy.getJyNumber());
                    qpjyDetail.setAddTime(nowTime);
                    qpjyDetail.setAddUserId(user.getId());
                    qpjyDetail.setAddUserCode(user.getCode());
                    qpjyDetail.setAddUserName(user.getName());
                    qpjyDetail.setAddUserDept(user.getDept());
                    qpjyDetail.setQpjy(qpjy);
                    qpjyDetail.setStatus("待指派");
                    qpjyDetail.setXuhao(xuhao);
                    qpjyDetailSet.add(qpjyDetail);
                    xuhao++;
                }
            }
            if (qpjyDetailSet == null || qpjyDetailSet.size() == 0) {
                return "请填写会议内容!";
            }
            qpjy.setQpjyDetailSet(qpjyDetailSet);
        } else {
            return "请填写会议内容!";
        }
        totalDao.save(qpjy);
        return "true";
    }

    @Override
    public String addjy(QuotedPricejy qpjy) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录!";
        }
        String nowTime = Util.getDateTime();
        qpjy.setAddTime(nowTime);
        qpjy.setAddUserId(user.getId());
        qpjy.setAddUserCode(user.getCode());
        qpjy.setAddUserName(user.getName());
        qpjy.setAddUserDept(user.getDept());
        String before = null;
        if (qpjy.getQpId() != null) {
            before = "jy_" + Util.getDateTime("yyyyMMdd");
        } else {
            before = "hyjy_" + Util.getDateTime("yyyyMMdd");
        }
        String maxnumber = (String) totalDao.getObjectByCondition("select max(jyNumber) from QuotedPricejy where jyNumber like '" + before + "%'");
        if (maxnumber != null && !"".equals(maxnumber)) {
            String now_number = maxnumber.split(before)[1];
            Integer number2 = Integer.parseInt(now_number) + 1;
            DecimalFormat df = new DecimalFormat("000");
            String number3 = df.format(number2);
            qpjy.setJyNumber(before + number3);
        } else {
            qpjy.setJyNumber(before + "001");
        }
        if (qpjy.getQpjyDetailList() != null) {
            Set<QuotedPricejyDetail> qpjyDetailSet = new HashSet<QuotedPricejyDetail>();
            int xuhao = 0;
            for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailList()) {
                if (qpjyDetail.getJyContext() != null && qpjyDetail.getJyContext().length() > 0) {
                    qpjyDetail.setJyNumber(qpjy.getJyNumber());
                    qpjyDetail.setAddTime(nowTime);
                    qpjyDetail.setAddUserId(user.getId());
                    qpjyDetail.setAddUserCode(user.getCode());
                    qpjyDetail.setAddUserName(user.getName());
                    qpjyDetail.setAddUserDept(user.getDept());
                    qpjyDetail.setQpjy(qpjy);
                    qpjyDetail.setStatus("待指派");
                    qpjyDetail.setXuhao(xuhao);
                    qpjyDetailSet.add(qpjyDetail);
                    xuhao++;
                }
            }
            if (qpjyDetailSet == null || qpjyDetailSet.size() == 0) {
                return "请填写会议内容!";
            }
            qpjy.setQpjyDetailSet(qpjyDetailSet);
        } else {
            return "请填写会议内容!";
        }
        totalDao.save(qpjy);
        return "true";
    }

    @Override
    public String importjy(File jy) {
        QuotedPricejy qpjy = new QuotedPricejy();
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录!";
        }
        String nowTime = Util.getDateTime();
        qpjy.setAddTime(nowTime);
        qpjy.setAddUserId(user.getId());
        qpjy.setAddUserCode(user.getCode());
        qpjy.setAddUserName(user.getName());
        qpjy.setAddUserDept(user.getDept());
        //////
        String msg = "true";
        boolean flag = true;
        String fileName = "hyjy_" + Util.getDateTime("yyyyMMddhhmmss") + ".xls";
        // 上传到服务器
        String fileRealPath = ServletActionContext.getServletContext()
                .getRealPath("/upload/file")
                + "/" + fileName;
        File file = new File(fileRealPath);
        Workbook wk = null;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf2.format(new Date());
        String date = sdf1.format(new Date());
        int i = 1;
        //TODO

        String before = "hyjy_" + Util.getDateTime("yyyyMMdd");
        String maxnumber = (String) totalDao.getObjectByCondition("select max(jyNumber) from QuotedPricejy where jyNumber like '" + before + "%'");
        if (maxnumber != null && !"".equals(maxnumber)) {
            String now_number = maxnumber.split(before)[1];
            Integer number2 = Integer.parseInt(now_number) + 1;
            DecimalFormat df = new DecimalFormat("000");
            String number3 = df.format(number2);
            qpjy.setJyNumber(before + number3);
        } else {
            qpjy.setJyNumber(before + "001");
        }
        try {
            FileCopyUtils.copy(jy, file);
            // 开始读取excle表格
            InputStream is = new FileInputStream(fileRealPath);// 创建文件流;
            if (is != null) {
                wk = Workbook.getWorkbook(is);// 创建workbook
            }
            Sheet st = wk.getSheet(0);// 获得第一张sheet表;
            int exclecolums = st.getRows();// 获得excle总行数
            if (exclecolums > 2) {
                List<Integer> strList = new ArrayList<Integer>();
                StringBuffer sberror = new StringBuffer();
                int successcount = 0;
                int errorcount = 0;
                int error_index = 0;
                String title = "";
                String datatime ="";
                Set<QuotedPricejyDetail> qpjyDetailSet = new HashSet<QuotedPricejyDetail>();
                int xuhao = 0;
                for (i = 2; i < exclecolums; i++) {
                    try {
                        Cell[] cells = st.getRow(i);// 获得每i行的所有单元格（返回的是数组）
                        String a = cells[0].getContents();// 标题
                        String b = cells[1].getContents();// 时间
                        if (i == 2) {
                            if (!a.equals("")) {
                                qpjy.setTitle(a);
                            } else {
                                return "请填写标题";
                            }
                            if (!b.equals("")) {
                                DateCell dc = (DateCell) cells[1];
                                SimpleDateFormat ds = new SimpleDateFormat("yyyy-MM-dd");
                                datatime = ds.format(dc.getDate());
                                qpjy.setJyTime(datatime);
                            } else {
                                return "请填写时间";
                            }
                        }
                        QuotedPricejyDetail quotedPricejyDetail = new QuotedPricejyDetail();
                        if (cells[2].getContents() != null && cells[2].getContents().length() > 0) {
                            {
                                quotedPricejyDetail.setJyContext(cells[2].getContents());
                                quotedPricejyDetail.setJyNumber(qpjy.getJyNumber());
                                quotedPricejyDetail.setAddTime(nowTime);
                                quotedPricejyDetail.setAddUserId(user.getId());
                                quotedPricejyDetail.setAddUserCode(user.getCode());
                                quotedPricejyDetail.setAddUserName(user.getName());
                                quotedPricejyDetail.setAddUserDept(user.getDept());
                                quotedPricejyDetail.setQpjy(qpjy);
                                quotedPricejyDetail.setStatus("待指派");
                                quotedPricejyDetail.setXuhao(xuhao);
                                qpjyDetailSet.add(quotedPricejyDetail);
                                xuhao++;
                            }
                        }
						successcount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        sberror.append("第" + (i + 1) + "行,数据格式错误!异常:"
                                + e.getMessage());
                        errorcount++;
                        if (error_index == 0) {
                            error_index = i + 1;
                        }
                        continue;
                    }
                }

                qpjy.setQpjyDetailSet(qpjyDetailSet);
                totalDao.save(qpjy);

                is.close();// 关闭流
                wk.close();// 关闭工作薄
                String errs = "";
                if (errorcount > 0) {
                    errs = "从第" + error_index + "行开始出现错误，请核对错误后，从第"
                            + error_index + "行开始重新导入(即删除excel中1-"
                            + (error_index - 1) + "行的数据)!\\n";
                }
                //标题两行
                exclecolums=exclecolums-2;
                msg = "总条数:" + exclecolums + "\\n已成功导入" + successcount + "个,失败"
                        + errorcount + "个\\n" + errs + "失败的內容如下:\\n" + sberror;
            } else {
                msg = "没有获取到行数";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "导入失败,出现异常" + e;

        }


        return msg;
    }


    @Override
    public List<QuotedPricejy> findDcljyListByqpId(int id, String start, String end, String title, String jyContext, String code, String name) {
        // TODO Auto-generated method stub
        String sql = "from QuotedPricejy where qpId=? ";
        if (start != null && start.length() > 0) {
            sql += " and jyTime>='" + start + "'";
        }
        if (end != null && end.length() > 0) {
            sql += " and jyTime<='" + end + "'";
        }
        if (title != null && title.length() > 0 && !title.contains("delete")) {
            sql += " and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status!='确认关闭' and jyContext like '%" + jyContext + "%'";
        } else {
            sql += " and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status!='确认关闭'";
        }
        if (code != null && code.length() > 0 && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' and userName like '%" + name + "%')";
        }
        if (code != null && code.length() > 0 && (name == null || name.length() == 0)) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' )";
        }
        if ((code == null || code.length() == 0) && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userName like '%" + name + "%')";
        }

        sql += "))";
        sql += "  order by jyTime desc";

        List<QuotedPricejy> qpjyList = totalDao.query(sql, id);
        if (qpjyList != null && qpjyList.size() > 0) {
            for (QuotedPricejy qpjy : qpjyList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        if (!qpjyDetail.getStatus().equals("确认关闭")) {
                            List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                            StringBuffer sb = new StringBuffer();
                            for (QuotedPricejyUsers jyUser : jyUserList) {
                                if (sb.length() == 0) {
                                    sb.append(jyUser.getUserName());
                                } else {
                                    sb.append(";" + jyUser.getUserName());
                                }
                            }
                            qpjyDetail.setZxUsers(sb.toString());
                            qpjyDetailList.add(qpjyDetail);
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpjyList;
    }

    @Override
    public List<QuotedPricejy> findDcljyListnoqpId(String start, String end, String title, String jyContext, String code, String name) {
        // TODO Auto-generated method stub
        String sql = "from QuotedPricejy where qpId is null ";
        if (start != null && start.length() > 0) {
            sql += " and jyTime>='" + start + "'";
        }
        if (end != null && end.length() > 0) {
            sql += " and jyTime<='" + end + "'";
        }
        if (title != null && title.length() > 0 && !title.contains("delete")) {
            sql += " and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status!='确认关闭' and jyContext like '%" + jyContext + "%'";
        } else {
            sql += " and (id not in (select qpjy.id from QuotedPricejyDetail) or id in(select qpjy.id from QuotedPricejyDetail where status!='确认关闭'";
        }
        if (code != null && code.length() > 0 && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' and userName like '%" + name + "%')";
        }
        if (code != null && code.length() > 0 && (name == null || name.length() == 0)) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userCode like '%" + code + "%' )";
        }
        if ((code == null || code.length() == 0) && name != null && name.length() > 0) {
            sql += " and id in(select jydId from QuotedPricejyUsers where userName like '%" + name + "%')";
        }

        sql += "))";
        sql += "  order by jyTime desc";

        List<QuotedPricejy> qpjyList = totalDao.query(sql);
        if (qpjyList != null && qpjyList.size() > 0) {
            for (QuotedPricejy qpjy : qpjyList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        if (!qpjyDetail.getStatus().equals("确认关闭")) {
                            List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                            StringBuffer sb = new StringBuffer();
                            for (QuotedPricejyUsers jyUser : jyUserList) {
                                if (sb.length() == 0) {
                                    sb.append(jyUser.getUserName());
                                } else {
                                    sb.append(";" + jyUser.getUserName());
                                }
                            }
                            qpjyDetail.setZxUsers(sb.toString());
                            qpjyDetailList.add(qpjyDetail);
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpjyList;
    }


    @Override
    public List<QuotedPricejy> findselfDcljyList(String tag) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user == null) {
            return null;
        }
        String sql = null;
        if (tag.equals("noqpId")) {
            sql = " and qpId is null";
        } else {
            sql = " and qpId is not null";
        }
        List<QuotedPricejy> qpjyList = totalDao.query("from QuotedPricejy where  id in(select qpjy.id from QuotedPricejyDetail where status!='确认关闭' and id in(select jydId from QuotedPricejyUsers where userId=?)) " + sql + " order by jyTime desc", user.getId());
        if (qpjyList != null && qpjyList.size() > 0) {
            for (QuotedPricejy qpjy : qpjyList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        boolean b = false;
                        List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                        StringBuffer sb = new StringBuffer();
                        for (QuotedPricejyUsers jyUser : jyUserList) {
                            if (sb.length() == 0) {
                                sb.append(jyUser.getUserName());
                            } else {
                                sb.append(";" + jyUser.getUserName());
                            }
                            qpjyDetail.setZxUsers(sb.toString());
                            if (jyUser.getUserId().equals(user.getId())) {
                                b = true;
                            }
                        }
                        if (b) {
                            if (!qpjyDetail.getStatus().equals("确认关闭")) {
                                qpjyDetailList.add(qpjyDetail);
                            }
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpjyList;
    }

    @Override
    public List<QuotedPricejy> findselfjyList(String tag) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user == null) {
            return null;
        }
        String sql = null;
        if (tag.equals("noqpId")) {
            sql = " and qpId is null";
        } else {
            sql = " and qpId is not null";
        }
        List<QuotedPricejy> qpjyList = totalDao.query("from QuotedPricejy where  id in(select qpjy.id from QuotedPricejyDetail where  id in(select jydId from QuotedPricejyUsers where userId=?) and  status='确认关闭')) " + sql + " order by jyTime desc", user.getId());
        if (qpjyList != null && qpjyList.size() > 0) {
            for (QuotedPricejy qpjy : qpjyList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        boolean b = false;
                        List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                        StringBuffer sb = new StringBuffer();
                        for (QuotedPricejyUsers jyUser : jyUserList) {
                            if (sb.length() == 0) {
                                sb.append(jyUser.getUserName());
                            } else {
                                sb.append(";" + jyUser.getUserName());
                            }
                            qpjyDetail.setZxUsers(sb.toString());
                            if (jyUser.getUserId().equals(user.getId())) {
                                b = true;
                            }
                        }
                        if (b) {
                            if (qpjyDetail.getStatus().equals("确认关闭")) {
                                qpjyDetailList.add(qpjyDetail);
                            }
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpjyList;
    }

    @Override
    public List<QuotedPricejy> finddspjyList(String tag) {
        Users user = Util.getLoginUser();
        if (user == null) {
            return null;
        }
        String sql = null;
        if (tag.equals("noqpId")) {
            sql = " and qpId is null";
        } else {
            sql = " and qpId is not null";
        }
        List<QuotedPricejy> qpjyList = totalDao.query("from QuotedPricejy where  id in(select qpjy.id from QuotedPricejyDetail where status='待确认') " + sql + " order by jyTime desc");
        if (qpjyList != null && qpjyList.size() > 0) {
            for (QuotedPricejy qpjy : qpjyList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        boolean b = false;
                        List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                        StringBuffer sb = new StringBuffer();
                        if (jyUserList != null && jyUserList.size() > 0) {
                            for (QuotedPricejyUsers jyUser : jyUserList) {
                                if (sb.length() == 0) {
                                    sb.append(jyUser.getUserName());
                                } else {
                                    sb.append(";" + jyUser.getUserName());
                                }
                                qpjyDetail.setZxUsers(sb.toString());
                            }
                        }
                        if (qpjyDetail.getStatus().equals("待确认")) {
                            qpjyDetailList.add(qpjyDetail);
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpjyList;
    }

    @Override
    public List<QuotedPricejy> findAlljyList(String tag) {
        Users user = Util.getLoginUser();
        if (user == null) {
            return null;
        }
        String sql = null;
        if (tag.equals("noqpId")) {
            sql = " and qpId is null";
        } else {
            sql = " and qpId is not null";
        }
        List<QuotedPricejy> qpjyList = totalDao.query("from QuotedPricejy where  id in(select qpjy.id from QuotedPricejyDetail where status='确认关闭') " + sql + " order by jyTime desc");
        if (qpjyList != null && qpjyList.size() > 0) {
            for (QuotedPricejy qpjy : qpjyList) {
                if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
                    List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
                    for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
                        if (qpjyDetail.getStatus().equals("确认关闭")) {
                            List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
                            StringBuffer sb = new StringBuffer();
                            if (jyUserList != null && jyUserList.size() > 0) {
                                for (QuotedPricejyUsers jyUser : jyUserList) {
                                    if (sb.length() == 0) {
                                        sb.append(jyUser.getUserName());
                                    } else {
                                        sb.append(";" + jyUser.getUserName());
                                    }
                                    qpjyDetail.setZxUsers(sb.toString());
                                }
                            }
                            qpjyDetailList.add(qpjyDetail);
                        }
                    }
                    qpjy.setQpjyDetailList(qpjyDetailList);
                }
            }
        }
        return qpjyList;
    }


    @Override
    public List<QuotedPricejyUsers> findJyUsersByJyId(int id) {
        // TODO Auto-generated method stub
        return totalDao.query("from QuotedPricejyUsers where jydId=? order by userType", id);
    }

    @Override
    public Object[] jydispatche(QuotedPricejyUsers qpjyUser) {
        // TODO Auto-generated method stub

        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return new Object[]{"请先登录", 0};
        } else {
            QuotedPricejyDetail detail = (QuotedPricejyDetail) totalDao.getObjectById(QuotedPricejyDetail.class, qpjyUser.getJydId());
            if (detail == null) {
                return new Object[]{"没有找到对应的纪要内容!", 0};
            }
            String nowTime = Util.getDateTime();
            if (detail.getZpTime() == null || detail.getZpTime().length() == 0) {
                detail.setZpTime(nowTime);
            }
            if (qpjyUser.getYqTime() != null
                    && qpjyUser.getYqTime().length() > 0) {
                detail.setYqTime(qpjyUser.getYqTime());
                String date1 = qpjyUser.getYqTime();
                String date2 = nowTime;
                String date3 = date2.substring(0, 10);
                Date d1 = Util.StringToDate(date1, "yyyy-MM-dd");
                Date d2 = Util.StringToDate(date3, "yyyy-MM-dd");
                Long l1 = d1.getTime();
                Long l2 = d2.getTime();
                if (l1 < l2) {
                    return new Object[]{"预完成时间不能在指派时间之前", 0};
                    // System.out.println("");
                    // }else{
                    // System.out.println("ok");
                }
            }
            if (detail.getStatus().equals("待指派")) {
                detail.setStatus("待执行");
            }
            totalDao.update(detail);
            Float count = (Float) totalDao.getObjectByCondition("select count(*) from QuotedPricejyUsers where jydId=? and userId=?", qpjyUser.getJydId(), qpjyUser.getUserId());
            if (count != null && count > 0) {
                return new Object[]{"此人已被指派过,请勿重复指派", 0};
            }
            Users user = (Users) totalDao.getObjectById(Users.class, qpjyUser.getUserId());
            if (user == null) {
                return new Object[]{"没有找到目标人员", 0};
            }
            if ("2".equals(qpjyUser.getUserType())) {
                qpjyUser.setUserType("协作");
            } else {
                qpjyUser.setUserType("执行");
            }
            qpjyUser.setUserName(user.getName());
            qpjyUser.setUserCode(user.getCode());
            qpjyUser.setUserDept(user.getDept());
            qpjyUser.setZpTime(nowTime);
            totalDao.save(qpjyUser);
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", qpjyUser.getJydId());
            if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                    BaomiOperateLog log = new BaomiOperateLog();
                    log.setOperateType("增加");//操作类型
                    log.setOperateObject("进度纪要");//操作对象
                    log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                            + qp.getMarkId() + "的进度纪要进行指派");//
                    log.setOperateTime(Util.getDateTime());//
                    log.setOperateUserId(loginUser.getId());
                    log.setOperateUsername(loginUser.getName());//
                    log.setOperateCode(loginUser.getCode());//
                    log.setOperateDept(loginUser.getDept());//
                    totalDao.save2(log);
                }
            }
            AlertMessagesServerImpl.addAlertMessages("项目进度纪要指派", loginUser.getName() + "指派你填报项目进度纪要(纪要内容:" + detail.getJyContext() + ")!", new Integer[]{user.getId()}, "QuotedPrice_totbjy.action?id=" + qpjyUser.getJydId(), true);
            if (qp != null) {
                List<Integer> uidList = totalDao.query("select u.id from Users u join u.moduleFunction f  where f.functionName in('项目核算填报(所有保密)','核算管理(所有保密)'	)");
                if (uidList != null && uidList.size() > 0) {
                    Integer[] uids = new Integer[uidList.size()];
                    for (int i = 0; i < uidList.size(); i++) {
                        uids[i] = uidList.get(i);
                    }
                    AlertMessagesServerImpl.addAlertMessages("项目进度纪要指派", loginUser.getName() + "指派" + user.getName() + "填报项目进度纪要(纪要内容:" + detail.getJyContext() + ")!", uids, "QuotedPrice_findjyList.action?id=" + qp.getId(), true);
                }
            }
        }
        return new Object[]{"成功", qpjyUser.getId()};
    }

    @Override
    public String jydispatcheremove(int id) {
        // TODO Auto-generated method stub
        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return "请先登录";
        } else {
            QuotedPricejyUsers qpjyUser = (QuotedPricejyUsers) totalDao.getObjectById(QuotedPricejyUsers.class, id);
            if (qpjyUser == null) {
                return "没有找到目标!";
            } else {
                QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where isbaomi='是' and id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", qpjyUser.getJydId());
                if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                    ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                    if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                        BaomiOperateLog log = new BaomiOperateLog();
                        log.setOperateType("删除");//操作类型
                        log.setOperateObject("进度纪要");//操作对象
                        log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                                + qp.getMarkId() + "的进度纪要进行取消指派");//
                        log.setOperateTime(Util.getDateTime());//
                        log.setOperateUserId(loginUser.getId());
                        log.setOperateUsername(loginUser.getName());//
                        log.setOperateCode(loginUser.getCode());//
                        log.setOperateDept(loginUser.getDept());//
                        totalDao.save2(log);
                    }
                }
                totalDao.delete(qpjyUser);
            }
        }
        return "成功";
    }

    @Override
    public QuotedPricejyDetail getQpjydById(int id) {
        // TODO Auto-generated method stub
        return (QuotedPricejyDetail) totalDao.getObjectById(QuotedPricejyDetail.class, id);
    }

    @Override
    public String saveQuotedPricejydFile(QuotedPricejyDetail qpjyd,
                                         List<QuotedPricejyDetailFile> qpjdFileList) {
        // TODO Auto-generated method stub
        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return "请先登录!";
        }
        QuotedPricejyDetail old = (QuotedPricejyDetail) totalDao.getObjectById(QuotedPricejyDetail.class, qpjyd.getId());
        if (old != null) {
            QuotedPricejyUsers jyUser = (QuotedPricejyUsers) totalDao.getObjectByCondition("from QuotedPricejyUsers where jydId=? and userId=?", qpjyd.getId(), loginUser.getId());
            if (jyUser == null) {
                return "您不是执行或者协助人员,不能进行此操作!";
            }
            String nowTime = Util.getDateTime();
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where isbaomi='是' and id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", qpjyd.getId());
            if (!Util.isEquals(old.getZxLog(), qpjyd.getZxLog())) {//方案变更
                old.setZxLog(qpjyd.getZxLog());
                old.setWcTime(nowTime);
                old.setStatus("待确认");
                totalDao.update(old);
                jyUser.setWcTime(nowTime);
                jyUser.setZxLog(qpjyd.getZxLog());
                totalDao.update(jyUser);
                if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                    ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                    if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                        BaomiOperateLog log = new BaomiOperateLog();
                        log.setOperateType("修改");//操作类型
                        log.setOperateObject("进度纪要");//操作对象
                        log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                                + qp.getMarkId() + "的进度纪要填报执行方案和结果");//
                        log.setOperateTime(Util.getDateTime());//
                        log.setOperateUserId(loginUser.getId());
                        log.setOperateUsername(loginUser.getName());//
                        log.setOperateCode(loginUser.getCode());//
                        log.setOperateDept(loginUser.getDept());//
                        totalDao.save2(log);
                    }
                }
            }
            if (qpjdFileList != null && qpjdFileList.size() > 0) {
                for (QuotedPricejyDetailFile qpjydFile : qpjdFileList) {
                    qpjydFile.setJydId(qpjyd.getId());
                    qpjydFile.setUserId(loginUser.getId());
                    qpjydFile.setUserName(loginUser.getName());//
                    qpjydFile.setUserCode(loginUser.getCode());//
                    qpjydFile.setUserDept(loginUser.getDept());//
                    qpjydFile.setAddTime(nowTime);
                    qpjydFile.setType("执行文件");//类型（纪要文件,执行文件）
                    if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                        qpjydFile.setBaomi("保密");
                    }
                    totalDao.save(qpjydFile);
                }
                if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                    ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                    if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                        BaomiOperateLog log = new BaomiOperateLog();
                        log.setOperateType("添加");//操作类型
                        log.setOperateObject("纪要文件");//操作对象
                        log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                                + qp.getMarkId() + "的进度纪要上传文件");//
                        log.setOperateTime(Util.getDateTime());//
                        log.setOperateUserId(loginUser.getId());
                        log.setOperateUsername(loginUser.getName());//
                        log.setOperateCode(loginUser.getCode());//
                        log.setOperateDept(loginUser.getDept());//
                        totalDao.save2(log);
                    }
                }
            }
            if (old.getQpjy().getQpId() != null) {
                List<Integer> uidList = totalDao.query("select u.id from Users u join u.moduleFunction f  where f.functionName in('项目进度纪要审批'	)");
                if (uidList != null && uidList.size() > 0) {
                    Integer[] uids = new Integer[uidList.size()];
                    for (int i = 0; i < uidList.size(); i++) {
                        uids[i] = uidList.get(i);
                    }
                    AlertMessagesServerImpl.addAlertMessages("项目进度纪要审核", loginUser.getName() + "填报了项目进度纪要执行方案(纪要内容:" + old.getJyContext() + ")!", uids, "QuotedPrice_finddspjyList.action", true);
                }
            } else {
                List<Integer> uidList = totalDao.query("select u.id from Users u join u.moduleFunction f  where f.functionName in('会议纪要审批')");
                if (uidList != null && uidList.size() > 0) {
                    Integer[] uids = new Integer[uidList.size()];
                    for (int i = 0; i < uidList.size(); i++) {
                        uids[i] = uidList.get(i);
                    }
                    AlertMessagesServerImpl.addAlertMessages("会议纪要审核", loginUser.getName() + "填报了会议纪要要执行方案(纪要内容:" + old.getJyContext() + ")!", uids, "QuotedPrice_finddspjyListnoqpId.action", true);
                }
            }
        }


        return "true";
    }

    @Override
    public List<QuotedPricejyDetailFile> findjyfilesByjydId(int id) {
        // TODO Auto-generated method stub
        return totalDao.query("from QuotedPricejyDetailFile where jydId=?", id);
    }

    @Override
    public QuotedPricejyDetailFile agetjydFileById(int id) {
        // TODO Auto-generated method stub
        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        QuotedPricejyDetailFile file = (QuotedPricejyDetailFile) totalDao.getObjectById(QuotedPricejyDetailFile.class, id);
        if (file != null) {
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where isbaomi='是' and id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", file.getJydId());
            if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                    BaomiOperateLog log = new BaomiOperateLog();
                    log.setOperateType("下载");//操作类型
                    log.setOperateObject("进度纪要文件");//操作对象
                    log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                            + qp.getMarkId() + "的进度纪要文件进行下载，文件名:" + file.getFileName());//
                    log.setOperateTime(Util.getDateTime());//
                    log.setOperateUserId(loginUser.getId());
                    log.setOperateUsername(loginUser.getName());//
                    log.setOperateCode(loginUser.getCode());//
                    log.setOperateDept(loginUser.getDept());//
                    totalDao.save2(log);
                }
            }
        }
        return file;
    }

    @Override
    public String spjyd(QuotedPricejyDetail qpjyd, String op) {
        // TODO Auto-generated method stub
        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return "请先登录";
        }
        QuotedPricejyDetail old = (QuotedPricejyDetail) totalDao.getObjectById(QuotedPricejyDetail.class, qpjyd.getId());
        if (old == null) {
            return "没有找到目标";
        }
        if (!"待确认".equals(old.getStatus())) {
            return "只有待确认状态才需要审批,当前状态为" + old.getStatus();
        }
        String opString = "";
        old.setSpMsg(qpjyd.getSpMsg());
        if (op.equals("agree")) {
            opString = "同意";
            old.setStatus("确认关闭");
            old.setQrUserId(loginUser.getId());//确认人
            old.setQrUserName(loginUser.getName());//
            old.setQrTime(Util.getDateTime());
        } else if (op.equals("back")) {
            opString = "打回";
            old.setStatus("重新执行");
            old.setQrUserId(loginUser.getId());//确认人
            old.setQrUserName(loginUser.getName());//
            old.setQrTime(Util.getDateTime());
        }
        QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where isbaomi='是' and id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", qpjyd.getId());
        if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
            ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
            if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                BaomiOperateLog log = new BaomiOperateLog();
                log.setOperateType("修改");//操作类型
                log.setOperateObject("进度纪要");//操作对象
                log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                        + qp.getMarkId() + "的进度纪要(纪要内容:" + old.getJyContext() + ")进行了审批,审批结果为:" + opString);//
                log.setOperateTime(Util.getDateTime());//
                log.setOperateUserId(loginUser.getId());
                log.setOperateUsername(loginUser.getName());//
                log.setOperateCode(loginUser.getCode());//
                log.setOperateDept(loginUser.getDept());//
                totalDao.save2(log);
            }
        }
        List<Integer> uidList = totalDao.query("select userId from QuotedPricejyUsers where jydId=?", qpjyd.getId());
        if (uidList != null && uidList.size() > 0) {
            Integer[] uids = new Integer[uidList.size()];
            for (int i = 0; i < uidList.size(); i++) {
                uids[i] = uidList.get(i);
            }
            AlertMessagesServerImpl.addAlertMessages("项目进度纪审批结果", loginUser.getName() + "审批了您执行的项目进度纪要执行方案(纪要内容:" + old.getJyContext() + "),审批意见:" + qpjyd.getSpMsg() + " 审批结果为:" + opString, uids, "QuotedPrice_findselfjyList.action", true);
        }
        totalDao.update(old);
        return "true";
    }

    @Override
    public String saveQuotedPricejymsFile(QuotedPricejyDetailFile qpjydFile) {
        // TODO Auto-generated method stub
        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return "请先登录!";
        }
        QuotedPricejyDetail old = (QuotedPricejyDetail) totalDao.getObjectById(QuotedPricejyDetail.class, qpjydFile.getJydId());
        if (old != null) {
            if (!loginUser.getId().equals(old.getQrUserId())) {
                return "您不是此纪要录入人员,不能进行此操作!";
            }
            String nowTime = Util.getDateTime();
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where isbaomi='是' and id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", qpjydFile.getJydId());
            if (qpjydFile != null) {
                qpjydFile.setUserId(loginUser.getId());
                qpjydFile.setUserName(loginUser.getName());//
                qpjydFile.setUserCode(loginUser.getCode());//
                qpjydFile.setUserDept(loginUser.getDept());//
                qpjydFile.setAddTime(nowTime);
                qpjydFile.setType("描述文件");//类型（描述文件,执行文件）
                if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                    ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                    if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                        qpjydFile.setBaomi("保密");
                        BaomiOperateLog log = new BaomiOperateLog();
                        log.setOperateType("添加");//操作类型
                        log.setOperateObject("纪要文件");//操作对象
                        log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                                + qp.getMarkId() + "的进度纪要上传文件");//
                        log.setOperateTime(Util.getDateTime());//
                        log.setOperateUserId(loginUser.getId());
                        log.setOperateUsername(loginUser.getName());//
                        log.setOperateCode(loginUser.getCode());//
                        log.setOperateDept(loginUser.getDept());//
                        totalDao.save2(log);
                    }
                }
                totalDao.save(qpjydFile);
            }
        }
        return "true";
    }

    @Override
    public Object[] deletejyFile(int id, String path) {
        // TODO Auto-generated method stub
        Users loginUser = Util.getLoginUser();
        if (loginUser == null) {
            return new Object[]{"请先登录", 0};
        }
        QuotedPricejyDetailFile jyFile = (QuotedPricejyDetailFile) totalDao.getObjectById(QuotedPricejyDetailFile.class, id);
        int jydId = 0;
        if (jyFile == null) {
            return new Object[]{"没有找到目标", 0};
        } else {
            if (!loginUser.getId().equals(jyFile.getUserId())) {
                return new Object[]{"对不起，您非此文件上传人,不能删除!", 0};
            }
            jydId = jyFile.getJydId();
            File file = new File(path + "/" + jyFile.getFileName());
            if (file.exists()) {
                file.delete();
            }
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectByCondition("from QuotedPrice where isbaomi='是' and id =(select qpId from QuotedPricejy where id =(select qpjy.id from QuotedPricejyDetail where id=?))", jyFile.getJydId());
            if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                    BaomiOperateLog log = new BaomiOperateLog();
                    log.setOperateType("删除");//操作类型
                    log.setOperateObject("纪要文件");//操作对象
                    log.setOperateRemark("对项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                            + qp.getMarkId() + "的进度纪要文件,文件名称(" + jyFile.getOldFileName() + ")");//
                    log.setOperateTime(Util.getDateTime());//
                    log.setOperateUserId(loginUser.getId());
                    log.setOperateUsername(loginUser.getName());//
                    log.setOperateCode(loginUser.getCode());//
                    log.setOperateDept(loginUser.getDept());//
                    totalDao.save2(log);
                }
            }
            totalDao.delete(jyFile);
        }

        return new Object[]{"true", jydId};
    }

    @Override
    public QuotedPricejy getQpjyAndDetailById(int id) {
        // TODO Auto-generated method stub
        QuotedPricejy qpjy = (QuotedPricejy) totalDao.getObjectById(QuotedPricejy.class, id);
        if (qpjy.getQpjyDetailSet() != null && qpjy.getQpjyDetailSet().size() > 0) {
            List<QuotedPricejyDetail> qpjyDetailList = new ArrayList<QuotedPricejyDetail>();
            for (QuotedPricejyDetail qpjyDetail : qpjy.getQpjyDetailSet()) {
//				 boolean b=false;
//				 List<QuotedPricejyUsers> jyUserList = totalDao.query("from QuotedPricejyUsers where jydId=?", qpjyDetail.getId());
//				StringBuffer sb= new StringBuffer();
//				if(jyUserList!=null&&jyUserList.size()>0){
//					for(QuotedPricejyUsers jyUser:jyUserList){
//						if(sb.length()==0){
//							sb.append(jyUser.getUserName());
//						}else{
//							sb.append(";"+jyUser.getUserName());
//						}
//						qpjyDetail.setZxUsers(sb.toString());
//					}
//				}
                qpjyDetailList.add(qpjyDetail);
            }
            qpjy.setQpjyDetailList(qpjyDetailList);
        }
        return qpjy;
    }

    @Override
    public String editjy(QuotedPricejy qpjy) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录!";
        }
        QuotedPricejy old = (QuotedPricejy) totalDao.getObjectById(QuotedPricejy.class, qpjy.getId());
        if (old == null) {
            return "没有找到目标纪要!";
        } else {
            String nowTime = Util.getDateTime();
            Integer xuhao = -1;
            if (qpjy.getTitle() == null || qpjy.getTitle().length() == 0) {
                return "会议主题不能为空!";
            } else {
                String oldTitle = old.getTitle();
                old.setTitle(qpjy.getTitle());
                old.setJyTime(qpjy.getJyTime());
                Set<QuotedPricejyDetail> oldDetailSet = old.getQpjyDetailSet();
                List<QuotedPricejyDetail> newDetailSet = qpjy.getQpjyDetailList();
                if (oldDetailSet == null) {
                    oldDetailSet = new HashSet<QuotedPricejyDetail>();
                }
                Set<QuotedPricejyDetail> deleteSet = new HashSet<QuotedPricejyDetail>();
                for (QuotedPricejyDetail qpd : oldDetailSet) {
                    if (qpd.getXuhao() > xuhao) {
                        xuhao = qpd.getXuhao();
                    }
                    boolean had = false;
                    if (newDetailSet != null && newDetailSet.size() > 0) {
                        for (QuotedPricejyDetail newd : newDetailSet) {
                            if (newd != null && newd.getId() != null && newd.getId().equals(qpd.getId())) {
                                had = true;
                            }
                        }
                    }
                    if (!had) {
                        deleteSet.add(qpd);
                    }
                }
                oldDetailSet.removeAll(deleteSet);
                xuhao++;
                for (QuotedPricejyDetail delete : deleteSet) {//删除
                    delete.setQpjy(null);
                    totalDao.delete(delete);
                }
                if (newDetailSet != null && newDetailSet.size() > 0) {//清空明细
                    for (QuotedPricejyDetail qpjyDetail : newDetailSet) {
                        if (qpjyDetail == null) {
                            continue;
                        }
                        if (qpjyDetail.getId() == null) {
                            if (qpjyDetail.getJyContext() != null && qpjyDetail.getJyContext().length() > 0) {
                                qpjyDetail.setJyNumber(qpjy.getJyNumber());
                                qpjyDetail.setAddTime(nowTime);
                                qpjyDetail.setAddUserId(user.getId());
                                qpjyDetail.setAddUserCode(user.getCode());
                                qpjyDetail.setAddUserName(user.getName());
                                qpjyDetail.setAddUserDept(user.getDept());
                                qpjyDetail.setQpjy(qpjy);
                                qpjyDetail.setStatus("待指派");
                                qpjyDetail.setXuhao(xuhao);
                                qpjyDetail.setQpjy(old);
                                oldDetailSet.add(qpjyDetail);
                            }
                        } else {
                            for (QuotedPricejyDetail oldqpd : oldDetailSet) {
                                if (oldqpd.getId() != null && oldqpd.getId().equals(qpjyDetail.getId())) {//新的加进来并没有id所以要先判断有没有id
                                    oldqpd.setJyContext(qpjyDetail.getJyContext());
                                    totalDao.update(oldqpd);
                                }
                            }
                        }
                    }
                }
                old.setQpjyDetailSet(oldDetailSet);
                totalDao.update(old);
            }
        }
        return "true";
    }

    @Override
    public String addqpBommatrial(QuotedPrice quotedPrice) {
        // TODO Auto-generated method stub
        Users user = Util.getLoginUser();
        if (user == null) {
            return "请先登录!";
        }
        QuotedPrice old = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, quotedPrice.getId());
        if (old == null) {
            return "没有找到目标BOM";
        } else {
            ProjectMaterialOrder pmo = new ProjectMaterialOrder();
            int maxNo = 0;
            String num = "";
            String isbaomi = "否";
            if (old != null && old.getIsbaomi() != null && old.getIsbaomi().equals("是")) {
                isbaomi = "是";
                num = "BJWl-" + Util.getDateTime("yyyy");
            } else {
                num = "PMO-" + Util.getDateTime("yyyy");
            }
            String hql = "select max(orderNo) from ProjectMaterialOrder where  orderNo like '%"
                    + num + "%'";
            Object obj = totalDao.getObjectByCondition(hql);
            if (obj == null) {
                num += "-001";
            } else {
                String maxNum = (String) obj;
                int number = Integer.parseInt(maxNum.substring(maxNum
                        .lastIndexOf("-") + 1, maxNum.length())) + 1;
                if (number < 10) {
                    num += "-00" + number;
                } else if (number < 100) {
                    num += "-0" + number;
                } else {
                    num += "-" + number;
                }
            }

            pmo.setOrderNo(num);
            ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, old.getProId());
            pmo.setIsbaomi(isbaomi);
            pmo.setQpId(old.getId());
            pmo.setProId(projectManage.getId());
            pmo.setProName(projectManage.getProjectName());
            pmo.setRemark(quotedPrice.getRemark());
            pmo.setUsercode(user.getCode());
            pmo.setUserName(user.getName());
            pmo.setUserId(user.getId());
            pmo.setAddTime(DateUtil.formatDate(new Date(),
                    "yyyy-MM-dd HH:mm:ss"));
            pmo.setReceiveStatus("未领");
            pmo.setAduitStatus("未审批");
            pmo.setPtCount(quotedPrice.getXiaohaoCount());
            totalDao.save(pmo);
            Set<ProjectMaterial> pmSet = new HashSet<ProjectMaterial>();
            pmo.setProjectMaterial(pmSet);
            pmList = new ArrayList<ProjectMaterial>();
            String msg = markqpBomwl(old, quotedPrice.getXiaohaoCount(), pmo, isbaomi);
            if (msg != null && msg.length() > 0) {
                throw new RuntimeException(msg);
            }
            if (pmList != null && pmList.size() > 0) {
                for (ProjectMaterial pm : pmList) {
                    pmSet.add(pm);
                    totalDao.save(pm);
                }
            }
            pmo.setProjectMaterial(pmSet);
        }
        return "成功";
    }

    private String markqpBomwl(QuotedPrice old,
                               Float xiaohaoCount, ProjectMaterialOrder pmo, String isbaomi) {
        // TODO Auto-generated method stub
        String msg = "";
        Set<QuotedPrice> sonSet = old.getQuotedPriceSet();
        if (sonSet == null || sonSet.size() > 0) {
            for (QuotedPrice son : sonSet) {
                if ("外购".equals(son.getProcardStyle())) {
                    if (son.getQuanzi2() == null || son.getQuanzi2() == 0 || son.getQuanzi1() == null || son.getQuanzi1() == 0) {
                        return son.getMarkId() + "用量有误!";
                    }
                    Float xiaohaoCount2 = xiaohaoCount * son.getQuanzi2() / son.getQuanzi1();
                    boolean b = false;
                    for (ProjectMaterial had : pmList) {
                        if (son.getMarkId().equals(had.getMarkId())) {
                            b = true;
                            had.setNeedNumber(had.getNeedNumber() + xiaohaoCount2);
                            break;
                        }
                    }
                    if (!b) {
                        ProjectMaterial pm = new ProjectMaterial();
                        pm.setOrderId(pmo.getId());//订单id
                        pm.setProName(pmo.getProName());//项目名称
                        pm.setMarkId(son.getMarkId());//件号
                        pm.setMaterialName(son.getProName());//材料名称
                        pm.setGuige(son.getSpecification());//规格
                        pm.setPaihao(son.getWgType());//物料类别
                        pm.setKgliao(son.getKgliao());//供料属性
                        pm.setNeedNumber(xiaohaoCount2);//需求数量
                        pm.setUnit(son.getUnit());//单位
//						pm.setsupplier;//供应商
                        pm.setIsbaomi(isbaomi);//是否保密
                        pm.setProjectMaterialOrder(pmo);//研发项目材料清单
                        pmList.add(pm);
                    }
                } else {
                    if (son.getCorrCount() == null || son.getCorrCount() == 0) {
                        return son.getMarkId() + "用量有误!";
                    }
                    Float xiaohaoCount2 = xiaohaoCount * son.getCorrCount();
                    msg += markqpBomwl(son, xiaohaoCount2, pmo, isbaomi);
//					pmList2.addAll(pmList3);
                }
            }
        }
        return msg;
    }

    @Override
    public Object[] deletejy(int id) {
        // TODO Auto-generated method stub
        QuotedPricejy qpjy = (QuotedPricejy) totalDao.getObjectById(QuotedPricejy.class, id);
        if (qpjy == null) {
            return new Object[]{"没有找到目标纪要!", 0};
        } else {
            QuotedPrice qp = (QuotedPrice) totalDao.getObjectById(QuotedPrice.class, qpjy.getQpId());
            if (qp != null && qp.getIsbaomi() != null && qp.getIsbaomi().equals("是")) {
                ProjectManage projectManage = (ProjectManage) totalDao.getObjectById(ProjectManage.class, qp.getProId());
                if (projectManage != null && projectManage.getIsbaomi() != null && projectManage.getIsbaomi().equals("是")) {
                    Users loginUser = Util.getLoginUser();
                    BaomiOperateLog log = new BaomiOperateLog();
                    log.setOperateType("删除");//操作类型
                    log.setOperateObject("进度纪要");//操作对象
                    log.setOperateRemark("删除项目名称:" + projectManage.getProjectName() + ",项目编号：" + projectManage.getProjectNum() + "的项目下的编码为："
                            + qp.getMarkId() + "下挂的进度纪要,纪要主题:" + qpjy.getTitle());//
                    log.setOperateTime(Util.getDateTime());//
                    log.setOperateUserId(loginUser.getId());
                    log.setOperateUsername(loginUser.getName());//
                    log.setOperateCode(loginUser.getCode());//
                    log.setOperateDept(loginUser.getDept());//
                    totalDao.save2(log);
                }
            }
            totalDao.delete(qpjy);
            return new Object[]{"true", qp.getId()};
        }
    }


    @Override
    public Object[] deletehyjy(int id) {
        QuotedPricejy qpjy = (QuotedPricejy) totalDao.getObjectById(QuotedPricejy.class, id);
            totalDao.delete(qpjy);
            return new Object[]{"true"};
        }

    //根据必要的信息查询？？
    @Override
    public List<QuotedPrice> getQuotedPriceByCon(Integer id) {
        String hql = "from QuotedPrice where fatherId is null or fatherId=0";
        if (null != id && 0 != id) {
            hql += "and id<>" + id;
        }
        List<QuotedPrice> list = totalDao.query(hql);
        return list;
    }

	@Override
	public void exportQuotedPrice(Integer rootId) {
		
		try {
			HttpServletResponse response = (HttpServletResponse) ActionContext
					.getContext().get(ServletActionContext.HTTP_RESPONSE);
			QuotedPrice totalCard = (QuotedPrice) totalDao
					.getObjectById(QuotedPrice.class, rootId);
			OutputStream os = response.getOutputStream();
			response.reset();
			String name = totalCard.getYwmrkId();
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
			ws.addCell(new jxl.write.Label(6, 0, "制卡人员", wc));
			ws.addCell(new jxl.write.Label(7, 0, "外购总价格(含税)", wc));
			ws.addCell(new jxl.write.Label(0, 1, totalCard.getYwmrkId(), wc));
			ws.addCell(new jxl.write.Label(1, 1, totalCard.getProName(), wc));
			ws.addCell(new jxl.write.Label(2, 1, totalCard.getSpecification(),
					wc));
			ws.addCell(new jxl.write.Label(3, 1, totalCard.getProcardStyle(),
					wc));
			ws.addCell(new jxl.write.Label(4, 1, totalCard.getUnit(), wc));
			ws.addCell(new jxl.write.Label(5, 1, totalCard.getYwmrkId(), wc));
			ws.addCell(new jxl.write.Label(6, 1, totalCard.getZhikaren(),
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
				ws.addCell(new jxl.write.Label(16, 2, "外购单价(含税)", wc));
				ws.addCell(new jxl.write.Label(17, 2, "外购总额(含税)", wc));
				ws.addCell(new jxl.write.Label(18, 2, "外委单价(含税)", wc));
				ws.addCell(new jxl.write.Label(19, 2, "外委总额(含税)", wc));
				ws.addCell(new jxl.write.Label(20, 2, "人工成本", wc));
				ws.addCell(new jxl.write.Label(21, 2, "长", wc));
				ws.addCell(new jxl.write.Label(22, 2, "宽", wc));
				ws.addCell(new jxl.write.Label(23, 2, "厚", wc));
				ws.addCell(new jxl.write.Label(24, 2, "材质", wc));
				ws.addCell(new jxl.write.Label(25, 2, "编制状态", wc));

			totalCard.setXiaohaoCount(1f);
			daochuBom2(totalCard, 3, ws, wc);
				Util.FomartFloat(sumprice, 4);
				ws.addCell(new jxl.write.Label(7, 1, sumprice + "", wc));

			wwb.write();
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	private int daochuBom2(QuotedPrice pt, int i, WritableSheet ws,
			WritableCellFormat wc) {
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
				if ("外购".equals(pt.getProcardStyle())) {
					Float waigouPrice = pt.getWaigouPrice()==null?0f:pt.getWaigouPrice();
					
						ws.addCell(new Label(16, i, Util.FomartFloat(waigouPrice.floatValue(), 4)
								+ "", wc));// 
						ws.addCell(new Label(17, i, Util.FomartFloat(waigouPrice.floatValue()
								* pt.getXiaohaoCount(), 4)
								+ "", wc));//
						sumprice += waigouPrice.floatValue()
								* pt.getXiaohaoCount();
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
				ws.addCell(new Label(18, i, wwmsg.toString(), wc));// 
				ws.addCell(new Label(19, i, Util.FomartFloat(wwtotal
						.floatValue(), 4)
						+ "", wc));//

				// }
				// 人工成本计算;
				if (!"外购".equals(pt.getProcardStyle())) {
					ws.addCell(new jxl.write.Number(20, i, pt.getRengongFei()==null?0:pt.getRengongFei()));// 人工成本
				} else {
					ws.addCell(new Label(20, i, ""));// 人工成本
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
						+ "", wc));// 编制状态


			i++;
			Set<QuotedPrice> sonSet = pt.getQuotedPriceSet();
			if (sonSet != null && sonSet.size() > 0) {
				for (QuotedPrice son : sonSet) {
					if ((son.getBanbenStatus() == null || !son
							.getBanbenStatus().equals("历史"))
							&& (son.getDateStatus() == null || !son.getDateStatus().equals("删除"))) {
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
						i = daochuBom2(son, i, ws, wc);
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
}