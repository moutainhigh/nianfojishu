package com.task.action.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.apache.struts2.ServletActionContext;
import org.springframework.util.FileCopyUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.task.Server.sys.CompanyInfoServer;
import com.task.entity.system.CompanyInfo;
import com.task.entity.system.LicenseMsg;
import com.task.util.DateUtil;
import com.task.util.MKUtil;

/**
 * 公司信息Action层
 * 
 * @author 唐晓斌
 * 
 */
@SuppressWarnings("serial")
public class CompanyInfoAction extends ActionSupport {
	// 公司业务层对象
	private CompanyInfoServer companyInfoServer;
	private List<CompanyInfo> comList;
	// 公司实体对象
	private CompanyInfo companyInfo;
	//licens信息对象
	private LicenseMsg licenseMsg;
	// 上传文件对象,用来上传公司图标
	private File taskfile;
	private File footfile;
	private File logoOKfile;
	private File shhhfile;
	private File shuiyinfile;
	private File faviconfile;
	// 上传文件名
	private String taskfileFileName;
	private String footfileFileName;
	private String logoOKfileFileName;
	private String shhhfileFileName;
	private String shuiyinfileFileName;
	private String faviconfileFileName;
	// 上传文件类型
	private String taskfileContentType;
	private String footfileContentType;
	private String logoOKfileContentType;
	private String shhhfileContentType;
	private String shuiyinfileContentType;
	private String faviconfileContentType;
	// 上传文件对象,用来上传license证书
	private File license;
	// 上传文件名
	private String licenseFileName;
	// 上传文件类型
	private String licenseContentType;

	private String successMessage;// 成功消息
	private String errorMessage;// 错误消息
	// 分页信息
	// 分页
	private String cpage = "1";
	private String total;
	private String url;
	private int pageSize = 15;
	//从菜单栏连接标记
	private String frommenu;
	private String companyUrl=null;//公司网址
	private Integer timeFlag=0;
	
	private File logofile;
	private String logofileContentType;

	/**
	 * 获取分页显示的公司列表信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String showCompanyInfos() {
		if("1".equals(frommenu)){//判斷是否是從左邊的連接進來的，如果是的話就無條件查詢所有的數據
			companyInfo=null;
			frommenu=null;
		}else{//如果不是從左邊的連接進來的就帶上之前的條件進行查詢
			if (companyInfo != null) {
			ActionContext.getContext().getSession().put("companyInfo",
					companyInfo);
		    } else {
			companyInfo = (CompanyInfo) ActionContext.getContext().getSession()
					.get("companyInfo");
		      }
	  }
		
		Map<Integer, Object> map = companyInfoServer.findCompanysByCondition(
				companyInfo, Integer.parseInt(cpage), pageSize);
		comList = new ArrayList<CompanyInfo>();
		if (null != map) {
			comList = (List<CompanyInfo>) map.get(1);// 显示页的公司信息列表
			if (comList != null && comList.size() > 0) {
				int count = (Integer) map.get(2);
				int pageCount = (count + pageSize - 1) / pageSize;
				this.setTotal(pageCount + "");
				this.setUrl("companyInfoAction_showCompanyInfos.action");
			} else {
				errorMessage = "没有找到你要查询的内容,请检查后重试!";
			}
		}
		return "showcompanyinfo";
	}

	/**
	 * 删除公司
	 * 
	 * @return
	 */
	public String delete() {
		boolean b = companyInfoServer.delete(companyInfo.getId());
		if (b) {
			successMessage="删除成功！";
		} else {
			errorMessage="删除失败！";
		}
		companyInfo = null;
		return showCompanyInfos();

	}

	/**
	 * 添加公司
	 * 
	 * @return
	 */
	public String add() {
         boolean taskbool=uploadImg(taskfile, taskfileContentType, "/upload/file/sysImages", "tasklogo.jpg", "公司登陆图标");
         boolean footbool=uploadImg(footfile, footfileContentType, "/upload/file/sysImages", "foot_03.jpg", "公司底部图标");
         boolean logoOkbool=uploadImg(logoOKfile, logoOKfileContentType, "/upload/file/sysImages", "logoOK.jpg", "公司打印图标");
         boolean shhhbool=uploadImg(shhhfile, shhhfileContentType, "/upload/file/sysImages", "shhh.jpg", "公司图表图标");
         boolean shuiyin=uploadImg(shuiyinfile, shuiyinfileContentType, "/upload/file/sysImages", "shuiyin.png", "公司水印图标");
         boolean faviconbool=uploadImg(faviconfile, faviconfileContentType, "/upload/file/sysImages", "favicon.ico", "公司ico图标");
		 if(!taskbool|!footbool|!logoOkbool|!shhhbool|!shuiyin|!faviconbool){
			 //其中任一文件没有添加成功
			 return showCompanyInfos();
		 }
		// 设置图标路径属性
		companyInfo.setTasklogo("/upload/file/sysImages/tasklogo.jpg");
		companyInfo.setBottomlogo("/upload/file/sysImages/foot_03.jpg");
		companyInfo.setLogoOKjpg("/upload/file/sysImages/logoOK.jpg");
		companyInfo.setShhhjpg("/upload/file/sysImages/shhh.jpg");
		companyInfo.setShuiyinpng("/upload/file/sysImages/shuiyin.png");
		companyInfo.setFavicon("/upload/file/sysImages/favicon.ico");
		
		
		boolean b = companyInfoServer.add(companyInfo);
		if (b) {
			successMessage="添加成功！";
		} else {
			errorMessage="添加失败！";
		}
		this.setCpage("1");
		return showCompanyInfos();
	}
	
	/**
	 * 跳往显示公司图标页面
	 * 
	 * @return
	 */
	public String showImg() {
		CompanyInfo companyInfo2 = companyInfoServer.getById(companyInfo
				.getId());
		if (companyInfo2 != null) {
			this.setCompanyInfo(companyInfo2);
			return "showcompanyinfoimg";
		} else {
			errorMessage="显示失败,不存在该公司信息！";
			return this.showCompanyInfos();
		}
	}
	/**
	 * 跳往修改公司信息页面
	 * 
	 * @return
	 */
	public String toupdate() {
		CompanyInfo companyInfo2 = companyInfoServer.getById(companyInfo
				.getId());
		if (companyInfo2 != null) {
			this.setCompanyInfo(companyInfo2);
			return "updatecompanyinfo";
		} else {
			errorMessage="编辑失败,不存在该公司信息！";
			return this.showCompanyInfos();
		}
	}

	public String update() {
		 boolean taskbool=uploadImg(taskfile, taskfileContentType, "/upload/file/sysImages", "tasklogo.jpg", null);
         boolean footbool=uploadImg(footfile, footfileContentType, "/upload/file/sysImages", "foot_03.jpg", null);
         boolean logoOkbool=uploadImg(logoOKfile, logoOKfileContentType, "/upload/file/sysImages", "logoOK.jpg", null);
         boolean shhhbool=uploadImg(shhhfile, shhhfileContentType, "/upload/file/sysImages", "shhh.jpg", null);
         boolean shuiyin=uploadImg(shuiyinfile, shuiyinfileContentType, "/upload/file/sysImages", "shuiyin.png", null);
         boolean logo=uploadImg(logofile, logofileContentType, "/upload/file/sysImages", "pebs-logo.png", null);
         boolean faviconbool=uploadImg(faviconfile, faviconfileContentType, "/upload/file/sysImages", "favicon.ico",null);
		 if(!taskbool|!footbool|!logoOkbool|!shhhbool|!shuiyin|!faviconbool){
			 //其中任一文件没有添加成功
			 return showCompanyInfos();
		 }
         	
		// 设置图标路径属性
		companyInfo.setTasklogo("/upload/file/sysImages/tasklogo.jpg");
		companyInfo.setBottomlogo("/upload/file/sysImages/foot_03.jpg");
		companyInfo.setLogoOKjpg("/upload/file/sysImages/logoOK.jpg");
		companyInfo.setShhhjpg("/upload/file/sysImages/shhh.jpg");
		companyInfo.setShuiyinpng("/upload/file/sysImages/shuiyin.png");
		companyInfo.setFavicon("/upload/file/sysImages/favicon.ico");
		companyInfo.setLogo("/upload/file/sysImages/pebs-logo.png");
		boolean b = companyInfoServer.update(companyInfo);
		if (b) {
			successMessage="编辑成功！";
			return this.showCompanyInfos();
		} else {
			errorMessage="编辑失败！";
			return "updatecompanyinfo";
		}

	}
   /**
    * 添加公司信息时添加图标文件
    * @param file
    * @param fileContentType
    * @param relativePath相对路径
    * @param imgName文件保存时的名字
    * @param logoName文件的中文名字
    * @return
    */
	public boolean uploadImg(File file,String fileContentType,String relativePath,String imgName,String logoName){
		
		if (file != null) {
			// 判断上传文件是否为图片
			if (!fileContentType.endsWith("jpeg")
					& !fileContentType.endsWith("jpg")
					& !fileContentType.endsWith("gif")
					& !fileContentType.endsWith("png")
					& !fileContentType.endsWith("bmp")
					& !fileContentType.endsWith("ico")
					& !fileContentType.endsWith("icon")) {
				errorMessage="上传文件不是图片类型";
				return false;
			}

			// 打开存放上传文件的位置
			String path =  ServletActionContext.getServletContext().getRealPath(relativePath);
			
			File file1 = new File(path);
			if (!file1.exists()) {
				file1.mkdirs();// 如果不存在文件夹就创建
			}
			// 将图片写入文件夹中
			InputStream is = null;
			OutputStream os = null;
			try {
				is = new FileInputStream(file);

				File file2 = new File(path + "/" + imgName);
				if (file2.exists()) {
					file2.delete();// 将原图片删掉
				}
				os = new FileOutputStream(path + "/" + imgName);
				byte[] b = new byte[1024];
				int length = 0;
				while (-1 != (length = is.read(b, 0, b.length))) {
					os.write(b);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				errorMessage="找不到文件！";
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				errorMessage="文件输入出错！";
				return false;
			} finally {
				try {
					os.close();
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}else{
			if(logoName!=null){//不为空时是在添加公司信息的时候使用的
			errorMessage=logoName+"不能为空！";
			return false;
			}else{//为空时是在修改公司信息的时候使用的
				return true;
			}
		}
		
	}
	public String uploadlic() {
		if (license != null) {
			// 判断上传文件是否为证书类型
			if (!licenseFileName.endsWith("lic")) {
				errorMessage="上传文件不是证书类型";
				return "uploadlic";
			}
			// 打开存放上传文件的位置
			String path =  ServletActionContext.getServletContext().getRealPath("/upload/file/myliclib");
			
			File file1 = new File(path);
			if (!file1.exists()) {
				file1.mkdirs();// 如果不存在文件夹就创建
			}
			// 将证书写入文件夹中
			InputStream is = null;
			//OutputStream os = null;
			try {
				is = new FileInputStream(license);
				File file2 = new File(path + "/" + licenseFileName);
				if (file2.exists()) {
					file2.delete();// 将原证书删掉
				}
//				os = new FileOutputStream(path + "/" + licenseFileName);
				// 上传文件到服务器
				String fileRealPath = path
						+ "/" + licenseFileName;
				File uploadFile = new File(fileRealPath);
				try {
					FileCopyUtils.copy(license, uploadFile);
				} catch (Exception e) {
					return "上传文件失败!";
				}
				
//				byte[] b = new byte[1024*10];
//				int length = 0;
//				while (-1 != (length = is.read(b, 0, b.length))) {
//					os.write(b);
//				}
				successMessage="上传成功！";
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				errorMessage="找不到文件！";
				return "uploadlic";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				errorMessage="文件输入出错！";
				return "uploadlic";
			} finally {
				try {
					//os.close();
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return "login";
		}
      return "uploadlic";
	}
	//在线人数
	public void getOnlineCount(){
		int count =companyInfoServer.getOnlineCount(companyUrl);
		MKUtil.writeJSON(count);
	}
	//一体机
	public void getOneMackCount(){
		int count =companyInfoServer.getOneMackCount(companyUrl);
		MKUtil.writeJSON(count);
	}
	//电子看板
	public void getoneScreenCount(){
		int count =companyInfoServer.getoneScreenConut(companyUrl);
		MKUtil.writeJSON(count);
	}
	//LED数量
	public void getOnLEDCount(){
		int count =companyInfoServer.getOnLEDCount(companyUrl);
		MKUtil.writeJSON(count);
	}
	public String getLicensemsg(){
		licenseMsg=companyInfoServer.getLicensemsg(companyUrl);
		if(licenseMsg==null){
			timeFlag=0;//表示没有注册
		}else{
			String end=licenseMsg.getNotAfter();
			if(end ==null||end==""){
				timeFlag=2;//表示证书过期
			}
			String now=DateUtil.formatDate(new Date(), "yyyy-MM-dd");
			long endTime=DateUtil.parseDate(end, "yyyy-MM-dd").getTime();
			long nowTime=DateUtil.parseDate(now, "yyyy-MM-dd").getTime();
			if(nowTime>endTime){
				timeFlag=2;//表示证书过期
			}else{
				timeFlag=1;//表示证书还没有上传
			}
		}
		return "licmsg";
	}
	// get和set方法
	public CompanyInfoServer getCompanyInfoServer() {
		return companyInfoServer;
	}

	public void setCompanyInfoServer(CompanyInfoServer companyInfoServer) {
		this.companyInfoServer = companyInfoServer;
	}

	public CompanyInfo getCompanyInfo() {
		return companyInfo;
	}

	public void setCompanyInfo(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
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

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public File getTaskfile() {
		return taskfile;
	}

	public void setTaskfile(File taskfile) {
		this.taskfile = taskfile;
	}

	public File getFootfile() {
		return footfile;
	}

	public void setFootfile(File footfile) {
		this.footfile = footfile;
	}

	public String getTaskfileFileName() {
		return taskfileFileName;
	}

	public void setTaskfileFileName(String taskfileFileName) {
		this.taskfileFileName = taskfileFileName;
	}

	public String getFootfileFileName() {
		return footfileFileName;
	}

	public void setFootfileFileName(String footfileFileName) {
		this.footfileFileName = footfileFileName;
	}

	public String getTaskfileContentType() {
		return taskfileContentType;
	}

	public void setTaskfileContentType(String taskfileContentType) {
		this.taskfileContentType = taskfileContentType;
	}

	public String getFootfileContentType() {
		return footfileContentType;
	}

	public void setFootfileContentType(String footfileContentType) {
		this.footfileContentType = footfileContentType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<CompanyInfo> getComList() {
		return comList;
	}

	public void setComList(List<CompanyInfo> comList) {
		this.comList = comList;
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

	public String getFrommenu() {
		return frommenu;
	}

	public void setFrommenu(String frommenu) {
		this.frommenu = frommenu;
	}

	public File getLogoOKfile() {
		return logoOKfile;
	}

	public void setLogoOKfile(File logoOKfile) {
		this.logoOKfile = logoOKfile;
	}

	public File getShhhfile() {
		return shhhfile;
	}

	public void setShhhfile(File shhhfile) {
		this.shhhfile = shhhfile;
	}

	public File getShuiyinfile() {
		return shuiyinfile;
	}

	public void setShuiyinfile(File shuiyinfile) {
		this.shuiyinfile = shuiyinfile;
	}

	public File getFaviconfile() {
		return faviconfile;
	}

	public void setFaviconfile(File faviconfile) {
		this.faviconfile = faviconfile;
	}

	public String getLogoOKfileFileName() {
		return logoOKfileFileName;
	}

	public void setLogoOKfileFileName(String logoOKfileFileName) {
		this.logoOKfileFileName = logoOKfileFileName;
	}

	public String getShhhfileFileName() {
		return shhhfileFileName;
	}

	public void setShhhfileFileName(String shhhfileFileName) {
		this.shhhfileFileName = shhhfileFileName;
	}

	public String getShuiyinfileFileName() {
		return shuiyinfileFileName;
	}

	public void setShuiyinfileFileName(String shuiyinfileFileName) {
		this.shuiyinfileFileName = shuiyinfileFileName;
	}

	public String getFaviconfileFileName() {
		return faviconfileFileName;
	}

	public void setFaviconfileFileName(String faviconfileFileName) {
		this.faviconfileFileName = faviconfileFileName;
	}

	public String getLogoOKfileContentType() {
		return logoOKfileContentType;
	}

	public void setLogoOKfileContentType(String logoOKfileContentType) {
		this.logoOKfileContentType = logoOKfileContentType;
	}

	public String getShhhfileContentType() {
		return shhhfileContentType;
	}

	public void setShhhfileContentType(String shhhfileContentType) {
		this.shhhfileContentType = shhhfileContentType;
	}

	public String getShuiyinfileContentType() {
		return shuiyinfileContentType;
	}

	public void setShuiyinfileContentType(String shuiyinfileContentType) {
		this.shuiyinfileContentType = shuiyinfileContentType;
	}

	public String getFaviconfileContentType() {
		return faviconfileContentType;
	}

	public void setFaviconfileContentType(String faviconfileContentType) {
		this.faviconfileContentType = faviconfileContentType;
	}

	public File getLicense() {
		return license;
	}

	public void setLicense(File license) {
		this.license = license;
	}


	public String getLicenseFileName() {
		return licenseFileName;
	}

	public void setLicenseFileName(String licenseFileName) {
		this.licenseFileName = licenseFileName;
	}

	public String getLicenseContentType() {
		return licenseContentType;
	}

	public void setLicenseContentType(String licenseContentType) {
		this.licenseContentType = licenseContentType;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public LicenseMsg getLicenseMsg() {
		return licenseMsg;
	}

	public void setLicenseMsg(LicenseMsg licenseMsg) {
		this.licenseMsg = licenseMsg;
	}

	public Integer getTimeFlag() {
		return timeFlag;
	}

	public void setTimeFlag(Integer timeFlag) {
		this.timeFlag = timeFlag;
	}

	public File getLogofile() {
		return logofile;
	}

	public void setLogofile(File logofile) {
		this.logofile = logofile;
	}

	public String getLogofileContentType() {
		return logofileContentType;
	}

	public void setLogofileContentType(String logofileContentType) {
		this.logofileContentType = logofileContentType;
	}

}