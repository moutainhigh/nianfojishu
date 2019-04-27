package com.task.entity.system;

import java.io.Serializable;

/**
 * 短链接
 * ta_sys_shortLink
 * @author WCY
 *
 */
public class ShortLink implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	
	private String shortUrl;  //短链接
	private String val;//特征值
	private String longUrl;  //长链接
	private String remark;	//备注
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public String getLongUrl() {
		return longUrl;
	}
	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	
	
}