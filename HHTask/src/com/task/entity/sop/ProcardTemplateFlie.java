package com.task.entity.sop;
/**
 * 卡片图纸
 * @author txb
 *
 */
public class ProcardTemplateFlie  implements java.io.Serializable{
	private static final long serialVersionUID =1L;
	private Integer id;
	private String markId;//件号
	private String procardName;//名称
	private String fileName;//文件名
    private String oldfileName;//文件原名称
    private String fileType;//文件类型(视频文件，工艺规范)
    private String type;//(普通,检验)
    private String status;//默认,历史
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
	public String getMarkId() {
		return markId;
	}
	public void setMarkId(String markId) {
		this.markId = markId;
	}
	public String getProcardName() {
		return procardName;
	}
	public void setProcardName(String procardName) {
		this.procardName = procardName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOldfileName() {
		return oldfileName;
	}
	public void setOldfileName(String oldfileName) {
		this.oldfileName = oldfileName;
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
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	} 
	
}
