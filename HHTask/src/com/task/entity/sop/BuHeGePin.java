package com.task.entity.sop;

import com.alibaba.fastjson.annotation.JSONField;

/****
 * 质量缺陷代码
 * 
 * @author 王晓飞
 * 
 * @表名 ta_sop_quxianleixing1
 * 
 */
public class BuHeGePin implements java.io.Serializable{
	private static final long serialVersionUID =1L;

	private int id;// id
	private String code;// 缺陷代码
	private String type;// 缺陷描述
	private String writePerson;// 录入人
	private String writeDate;// 录入时间
	private DefectOfType defType;//

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWritePerson() {
		return writePerson;
	}

	public void setWritePerson(String writePerson) {
		this.writePerson = writePerson;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@JSONField(serialize = false)
	public DefectOfType getDefType() {
		return defType;
	}

	public void setDefType(DefectOfType defType) {
		this.defType = defType;
	}

}
