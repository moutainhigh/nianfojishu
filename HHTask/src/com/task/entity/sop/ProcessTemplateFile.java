package com.task.entity.sop;
/**
 * 工序图纸 (表: ta_sop_w_ProcessTemplateFile)
 * @author txb
 *
 */
public class ProcessTemplateFile implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String markId;//件号
    private Integer processNO;//工序号
    private String processName;//工序名称
    private String fileName;//文件名
    private String fileName2;//初始上传时为加章文件，审批完成之后替换fileName
    private String oldfileName;//文件原名称
    private String month;//上传月份(上传文件夹以月份命名（yyyy-MM）)
    private String type;//文件类型(3D文件，工艺规范,SOP文件,视频文件,SIP文件,3D模型,成型图, NC数冲,其它文件)
    private String productStyle;//试制，批产
    private String banBenNumber;//版本号（原图使用）
    private String status;//默认,历史
    private String userName;//上传人姓名
    private String userCode;//上传人工号
    private String addTime;//上传时间
    private Integer banci;// 版次
    
    private String sbStatus;//设变状态
    private String bzStatus;// 工艺编制状态(初始,待编制,已编制,已校对,已审核,已批准)
    
    private String bianzhiName;// 编制人
	private Integer bianzhiId;// 编制ID
	private String bianzhiDate;// 编制时间

	private String jiaoduiName;// 校对人
	private Integer jiaoduiId;// 校对ID
	private String jiaoduiDate;// 校对时间

	private String shenheName;// 审核人
	private Integer shenheId;// 审核ID
	private String shenheDate;// 审核时间

	private String pizhunName;// 批准人
	private Integer pizhunId;// 批准ID
	private String pizhunDate;// 批准时间
	private String canChange;//修改权限(页面显示)
	private Integer glId;//ProcardTemplate Id 或 ProcessTemplate Id
	private Integer processtsbfileId;//设变预编译图纸Id
	
    /**获取客户端参数*/
	private String params;
	
	
	public String getParams() {
		if(params!=null){
			return params.replace("\\t", "").replace("\\r","").replace("\\n","").replace("\\f","").replace("\\","");
		}
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	} 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProcessNO() {
		return processNO;
	}
	public void setProcessNO(Integer processNO) {
		this.processNO = processNO;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOldfileName() {
		return oldfileName;
	}
	public void setOldfileName(String oldfileName) {
		this.oldfileName = oldfileName;
	}
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getBanBenNumber() {
		return banBenNumber;
	}
	public void setBanBenNumber(String banBenNumber) {
		this.banBenNumber = banBenNumber;
	}
	public String getSbStatus() {
		return sbStatus;
	}
	public void setSbStatus(String sbStatus) {
		this.sbStatus = sbStatus;
	}
	public String getBzStatus() {
		return bzStatus;
	}
	public void setBzStatus(String bzStatus) {
		this.bzStatus = bzStatus;
	}
	public String getBianzhiName() {
		return bianzhiName;
	}
	public void setBianzhiName(String bianzhiName) {
		this.bianzhiName = bianzhiName;
	}
	public Integer getBianzhiId() {
		return bianzhiId;
	}
	public void setBianzhiId(Integer bianzhiId) {
		this.bianzhiId = bianzhiId;
	}
	public String getBianzhiDate() {
		return bianzhiDate;
	}
	public void setBianzhiDate(String bianzhiDate) {
		this.bianzhiDate = bianzhiDate;
	}
	public String getJiaoduiName() {
		return jiaoduiName;
	}
	public void setJiaoduiName(String jiaoduiName) {
		this.jiaoduiName = jiaoduiName;
	}
	public Integer getJiaoduiId() {
		return jiaoduiId;
	}
	public void setJiaoduiId(Integer jiaoduiId) {
		this.jiaoduiId = jiaoduiId;
	}
	public String getJiaoduiDate() {
		return jiaoduiDate;
	}
	public void setJiaoduiDate(String jiaoduiDate) {
		this.jiaoduiDate = jiaoduiDate;
	}
	public String getShenheName() {
		return shenheName;
	}
	public void setShenheName(String shenheName) {
		this.shenheName = shenheName;
	}
	public Integer getShenheId() {
		return shenheId;
	}
	public void setShenheId(Integer shenheId) {
		this.shenheId = shenheId;
	}
	public String getShenheDate() {
		return shenheDate;
	}
	public void setShenheDate(String shenheDate) {
		this.shenheDate = shenheDate;
	}
	public String getPizhunName() {
		return pizhunName;
	}
	public void setPizhunName(String pizhunName) {
		this.pizhunName = pizhunName;
	}
	public Integer getPizhunId() {
		return pizhunId;
	}
	public void setPizhunId(Integer pizhunId) {
		this.pizhunId = pizhunId;
	}
	public String getPizhunDate() {
		return pizhunDate;
	}
	public void setPizhunDate(String pizhunDate) {
		this.pizhunDate = pizhunDate;
	}
	public Integer getBanci() {
		return banci;
	}
	public void setBanci(Integer banci) {
		this.banci = banci;
	}
	public String getProductStyle() {
		return productStyle;
	}
	public void setProductStyle(String productStyle) {
		this.productStyle = productStyle;
	}
	public String getFileName2() {
		return fileName2;
	}
	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}
	public Integer getGlId() {
		return glId;
	}
	public void setGlId(Integer glId) {
		this.glId = glId;
	}
	public String getCanChange() {
		return canChange;
	}
	public void setCanChange(String canChange) {
		this.canChange = canChange;
	}
	public Integer getProcesstsbfileId() {
		return processtsbfileId;
	}
	public void setProcesstsbfileId(Integer processtsbfileId) {
		this.processtsbfileId = processtsbfileId;
	}

    
    
}