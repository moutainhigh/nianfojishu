package com.tast.entity.zhaobiao;

import java.io.Serializable;

/*
 * 招标--模版表
 */
public class Zhmoban implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //			
	private String name; //	
	private String classe; //
	private String etime; //
	private String xuhao; //	
	private String paihao; //
	private String guige; //		
	private String xuyao; //
	private String changdi; //
	private String jibei; //
	private String status; //
	private String loc; //

	public Zhmoban() {
	};

	public Zhmoban(Integer id, String name, String class_, String etime,
			String xuhao, String paihao, String guige, String xuyao,
			String changdi, String jibei, String status, String loc) {
		this.id = id;
		this.name = name;
		this.classe = classe;
		this.etime = etime;
		this.xuhao = xuhao;
		this.paihao = paihao;
		this.guige = guige;
		this.xuyao = xuyao;
		this.changdi = changdi;
		this.jibei = jibei;
		this.status = status;

	};

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public String getXuhao() {
		return xuhao;
	}

	public void setXuhao(String xuhao) {
		this.xuhao = xuhao;
	}

	public String getPaihao() {
		return paihao;
	}

	public void setPaihao(String paihao) {
		this.paihao = paihao;
	}

	public String getGuige() {
		return guige;
	}

	public void setGuige(String guige) {
		this.guige = guige;
	}

	public String getXuyao() {
		return xuyao;
	}

	public void setXuyao(String xuyao) {
		this.xuyao = xuyao;
	}

	public String getChangdi() {
		return changdi;
	}

	public void setChangdi(String changdi) {
		this.changdi = changdi;
	}

	public String getJibei() {
		return jibei;
	}

	public void setJibei(String jibei) {
		this.jibei = jibei;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

}
