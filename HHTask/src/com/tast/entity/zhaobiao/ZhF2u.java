package com.tast.entity.zhaobiao;

import java.io.Serializable;



/**
 * ZhF2u entity. @author MyEclipse Persistence Tools
 */

public class ZhF2u  implements Serializable {
	private static final long serialVersionUID = 1L;


    // Fields    

     private Integer id;
     private String fid;
     private String uid;
     private String statu;
     private String sfbj;


    // Constructors

    /** default constructor */
    public ZhF2u() {
    }

    
    /** full constructor */
    public ZhF2u(String fid, String uid, String statu, String sfbj) {
        this.fid = fid;
        this.uid = uid;
        this.statu = statu;
        this.sfbj = sfbj;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFid() {
        return this.fid;
    }
    
    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getUid() {
        return this.uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatu() {
        return this.statu;
    }
    
    public void setStatu(String statu) {
        this.statu = statu;
    }

    public String getSfbj() {
        return this.sfbj;
    }
    
    public void setSfbj(String sfbj) {
        this.sfbj = sfbj;
    }
   








}