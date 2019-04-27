package com.task.action.caiwu;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.InsuranceGoldServer;
import com.task.Server.caiwu.AccountCheckService;
import com.task.Server.caiwu.noncore.NoncoreReceServer;
import com.task.Server.caiwu.receivePayment.ReceiptServer;
import com.task.entity.InsuranceGold;
import com.task.entity.caiwu.AccountCheck;
import com.task.entity.caiwu.noncore.NonCoreReceivables;
import com.task.entity.caiwu.receivePayment.Receipt;
import com.task.entity.caiwu.receivePayment.ReceiptLog;
import com.task.util.MKUtil;
import com.task.util.Util;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class AccountCheckAction extends ActionSupport {

    private AccountCheckService accountCheckService;
    private AccountCheck accountCheck;
    private List<AccountCheck> accountCheckList;

    private String successMessage;// 成功消息
    private String errorMessage;// 错误消息
    private int id;// id
    private String pageStatus;// 页面状态

    // 分页
    private String cpage = "1";
    private String total;
    private String url;
    private int pageSize = 15;


    private ReceiptLog receiptLog;
    private List<ReceiptLog> receiptLogList;// 集合
    private List<ReceiptLog> receiptLogListAudit;// 集合
    private List<ReceiptLog> receiptLogListSkqr;// 集合
    private List<ReceiptLog> receiptLogListUpload;// 集合

    //    private Receipt receipt;
    private ReceiptServer receiptServer;
    private String qrTag;
    private List<Receipt> receiptList;
    private String filename;


    private NonCoreReceivables nonCoreReceivables;
    private NoncoreReceServer noncoreReceServer;
    private String startDate;// 开始时间
    private String endDate;// 截止时间
    private String tag;
    private List<NonCoreReceivables> nonCoreReceivablesList;








    //page
    public String accountCheckAddPage() {
        return "accountCheckAdd";
    }

    public String accountCheckAdd() {
        if (accountCheck==null){
            accountCheck=new AccountCheck();
        }
        accountCheck.setAccountDate(Util.getDateTime());
        accountCheck.setRegistrationStatus("已登记");
        errorMessage=accountCheckService.AddAccountCheck(accountCheck);
        return "success";
    }

    public String accountCheckDelete(){
        errorMessage=accountCheckService.DeleteAccountCheck(accountCheck);
        return "success";
    }

    public String accountCheckUpdate(){
        errorMessage=accountCheckService.UpdateAccountCheck(accountCheck);
        return "success";
    }


    public String accountCheckList() {
        if(accountCheck==null){
            accountCheck= new AccountCheck();
        }
        Object[] object=accountCheckService.findAllAccountCheck(accountCheck,Integer.parseInt(cpage), pageSize);

        if (object != null && object.length > 0) {
            accountCheckList = (List<AccountCheck>) object[0];
            if (object != null && object.length > 0) {
                accountCheckList = (List) object[0];
                if (accountCheckList != null && accountCheckList.size() > 0) {
                    int count = (Integer) object[1];
                    int pageCount = (count + pageSize - 1) / pageSize;
                    this.setTotal(pageCount + "");
                    this.setUrl("AccountCheckAction_accountCheckList.action");
                    errorMessage = null;
                } else {
                    errorMessage = "没有找到你要查询的内容,请检查后重试!";
                }
            }
        }
        return "accountCheckList";
    }


    //对账单对资金使用申请表
    public String findFundApplyByAccountCheck(){
        id=Integer.parseInt(accountCheckService.findFundApplyByAccountCheck(accountCheck.getReceiptLogid()));
        return "accountCheck_FundApply";
    }

//    public String findReceiptByAccountCheck(){
//        id= Integer.parseInt(accountCheckService.findReceiptByAccountCheck(accountCheck.getReceiptLogid()));
//        return "accountCheck_Receipt";
//    }

    public String findReceiptLogByAccountCheck(){
        id= Integer.parseInt(accountCheckService.findReceiptByAccountCheck(accountCheck.getReceiptLogid()));
        return "accountCheck_ReceiptLog";
    }

    public String findReceiptLogFileByAccountCheck(){
        filename=accountCheckService.findReceiptLogFileByAccountCheck((accountCheck.getReceiptLogid()));
        return "accountCheck_ReceiptLogFile";
    }

    public String findcwVouchersByAccountCheck(){
        filename=accountCheckService.findcwVouchersByAccountCheck(accountCheck.getReceiptLogid());
        return "accountCheck_CwVouchers";
    }


    /*
        * @author fy
    　　* @date 2018/8/9 16:24
    　　* @Description:查询所有付款申请单JSON (未使用）
    　　* @param
    　　* @return
    　　* @throws
    　　*/
    public String findAllPayment(){
        try {
            List<ReceiptLog> list=accountCheckService.findAllPayment();
            MKUtil.writeJSON(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String sendAlertMessages(){
        accountCheck=accountCheckService.findAccountCheck(id);
        errorMessage=accountCheckService.SendAlertMessages(accountCheck);
        return  "success";
    }


//    /****
//     * 查询付款账单
//     *
//     * @return
//     */
//    public String findReceiptList() {
//        pageStatus="对账单";
//        if (receipt != null) {
//            ActionContext.getContext().getSession().put("receipt", receipt);
//        } else {
//            receipt = (Receipt) ActionContext.getContext().getSession().get(
//                    "receipt");
//        }
//        Object[] object = receiptServer.findReceiptList(receipt, Integer
//                .parseInt(cpage), pageSize, pageStatus);
//        if (receipt != null && receipt.getFukuanMonth() != null
//                && receipt.getFukuanMonth().length() > 0) {
//            qrTag = Util.getDateTime("yyyy-MM")
//                    .equals(receipt.getFukuanMonth())
//                    + "";
//        }
//        if (object != null && object.length > 0) {
//            receiptList = (List<Receipt>) object[0];
//            if (receiptList != null && receiptList.size() > 0) {
//                int count = (Integer) object[1];
//                int pageCount = (count + pageSize - 1) / pageSize;
//                this.setTotal(pageCount + "");
//                if (pageStatus == null) {
//                    pageStatus = "";
//                }
//
//                this.setUrl("AccountCheckAction_findReceiptList.action?pageStatus="
//                        + pageStatus);
//                errorMessage = null;
//            } else {
//                errorMessage = "没有找到你要查询的内容,请检查后重试!";
//            }
//        } else {
//            errorMessage = "没有找到你要查询的内容,请检查后重试!";
//        }
//        return "AccountCheck_Receipt_showList";
//    }



    /****
     * 查询付款记录
     *
     * @return
     */
    public String findReceiptLogListforAccountCheck() {
        pageStatus="all";
        Boolean b_accountCheck=true;
        if (receiptLog != null) {
            ActionContext.getContext().getSession().put("receiptLog",
                    receiptLog);
        } else {
            receiptLog = (ReceiptLog) ActionContext.getContext().getSession()
                    .get("receiptLog");
        }
        if ("all".equals(pageStatus)) {
            // 待审批记录
            Object[] object = receiptServer.findReceiptLogList(receiptLog, 0,
                    0, "audit",b_accountCheck);
            if (object != null && object.length > 0) {
                receiptLogListAudit = (List<ReceiptLog>) object[0];
            }
            // 待付款&待确认记录
            object = receiptServer.findReceiptLogList(receiptLog, 0, 0, "skqr",b_accountCheck);
            if (object != null && object.length > 0) {
                receiptLogListSkqr = (List<ReceiptLog>) object[0];
            }
            // 待上传凭证
            object = receiptServer.findReceiptLogList(receiptLog, Integer
                    .parseInt(cpage), pageSize, "wtfk",b_accountCheck);
            if (object != null && object.length > 0) {
                receiptLogListUpload = (List<ReceiptLog>) object[0];
            }
        }
        Object[] object = receiptServer.findReceiptLogList(receiptLog, Integer
                .parseInt(cpage), pageSize, pageStatus,b_accountCheck);
        if (object != null && object.length > 0) {
            receiptLogList = (List<ReceiptLog>) object[0];
            if (receiptLogList != null && receiptLogList.size() > 0) {
                int count = (Integer) object[1];
                int pageCount = (count + pageSize - 1) / pageSize;
                this.setTotal(pageCount + "");
                if (pageStatus == null) {
                    pageStatus = "";
                }
                this
                        .setUrl("AccountCheckAction_findReceiptLogListforAccountCheck.action?pageStatus="
                                + pageStatus);
                errorMessage = null;
            } else {
                errorMessage = "没有找到你要查询的内容,请检查后重试!";
            }
        } else {
            errorMessage = "没有找到你要查询的内容,请检查后重试!";
        }
        if ("audit".equals(pageStatus)) {
        return "ReceiptLog_auditList";
    }
//        return "ReceiptLog_showList";
        return "AccountCheck_ReceiptLog_showList";

    }



    public String findNoncoreReceiveList() {
        if (nonCoreReceivables != null) {
            ActionContext.getContext().getSession().put("NonCoreReceivables",
                    nonCoreReceivables);
        } else {
            nonCoreReceivables = (NonCoreReceivables) ActionContext
                    .getContext().getSession().get("NonCoreReceivables");
        }
        Object[] object = noncoreReceServer.findNoncoreReceiveList(
                nonCoreReceivables, startDate, endDate,
                Integer.parseInt(cpage), pageSize, tag);
        if (object != null && object.length > 0) {
            nonCoreReceivablesList = (List<NonCoreReceivables>) object[0];
            if (nonCoreReceivablesList != null
                    && nonCoreReceivablesList.size() > 0) {
                int count = (Integer) object[1];
                int pageCount = (count + pageSize - 1) / pageSize;
                this.setTotal(pageCount + "");
                this.setUrl("NoncoreReceAction!showList.action?tag=" + tag);
                errorMessage = null;

            } else {
                errorMessage = "没有找到你要查询的内容,请检查后重试!";
            }
        } else {
            errorMessage = "没有找到你要查询的内容,请检查后重试!";
        }
        return "AccountCheck_NonCoreReceivablesDetail_showList";
    }

    /*
     * @author fy
     * @date 2018/8/9 16:24
     * @Description:查询所有收款申请单
     * @param
     * @return
     * @throws
     */
//    public String findall（）{
//
//    }

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

    public AccountCheckService getAccountCheckService() {
        return accountCheckService;
    }

    public void setAccountCheckService(AccountCheckService accountCheckService) {
        this.accountCheckService = accountCheckService;
    }

    public ReceiptServer getReceiptServer() {
        return receiptServer;
    }

    public void setReceiptServer(ReceiptServer receiptServer) {
        this.receiptServer = receiptServer;
    }

    public String getQrTag() {
        return qrTag;
    }

    public void setQrTag(String qrTag) {
        this.qrTag = qrTag;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    public AccountCheck getAccountCheck() {
        return accountCheck;
    }

    public void setAccountCheck(AccountCheck accountCheck) {
        this.accountCheck = accountCheck;
    }

    public List<AccountCheck> getAccountCheckList() {
        return accountCheckList;
    }

    public void setAccountCheckList(List<AccountCheck> accountCheckList) {
        this.accountCheckList = accountCheckList;
    }

    public ReceiptLog getReceiptLog() {
        return receiptLog;
    }

    public void setReceiptLog(ReceiptLog receiptLog) {
        this.receiptLog = receiptLog;
    }

    public List<ReceiptLog> getReceiptLogList() {
        return receiptLogList;
    }

    public void setReceiptLogList(List<ReceiptLog> receiptLogList) {
        this.receiptLogList = receiptLogList;
    }

    public List<ReceiptLog> getReceiptLogListAudit() {
        return receiptLogListAudit;
    }

    public void setReceiptLogListAudit(List<ReceiptLog> receiptLogListAudit) {
        this.receiptLogListAudit = receiptLogListAudit;
    }

    public List<ReceiptLog> getReceiptLogListSkqr() {
        return receiptLogListSkqr;
    }

    public void setReceiptLogListSkqr(List<ReceiptLog> receiptLogListSkqr) {
        this.receiptLogListSkqr = receiptLogListSkqr;
    }

    public List<ReceiptLog> getReceiptLogListUpload() {
        return receiptLogListUpload;
    }

    public void setReceiptLogListUpload(List<ReceiptLog> receiptLogListUpload) {
        this.receiptLogListUpload = receiptLogListUpload;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public NonCoreReceivables getNonCoreReceivables() {
        return nonCoreReceivables;
    }

    public void setNonCoreReceivables(NonCoreReceivables nonCoreReceivables) {
        this.nonCoreReceivables = nonCoreReceivables;
    }

    public NoncoreReceServer getNoncoreReceServer() {
        return noncoreReceServer;
    }

    public void setNoncoreReceServer(NoncoreReceServer noncoreReceServer) {
        this.noncoreReceServer = noncoreReceServer;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<NonCoreReceivables> getNonCoreReceivablesList() {
        return nonCoreReceivablesList;
    }

    public void setNonCoreReceivablesList(List<NonCoreReceivables> nonCoreReceivablesList) {
        this.nonCoreReceivablesList = nonCoreReceivablesList;
    }
}
