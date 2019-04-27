package com.tast.entity.zhaobiao;

import java.io.Serializable;



/**
 * ZhLog entity. @author MyEclipse Persistence Tools
 */

public class ZhLog  implements Serializable {
	private static final long serialVersionUID = 1L;


    // Fields    

     private Integer id;
     private String name;
     private String time;
     private String sth;


    // Constructors

    /** default constructor */
    public ZhLog() {
    }

    
    /** full constructor */
    public ZhLog(String name, String time, String sth) {
        this.name = name;
        this.time = time;
        this.sth = sth;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }

    public String getSth() {
        return this.sth;
    }
    
    public void setSth(String sth) {
        this.sth = sth;
    }
   








}