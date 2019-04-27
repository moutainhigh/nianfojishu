package com.task.entity;

/*
    * @author fy
　　* @date 2018/8/15 10:35
　　* @Description: 消息轮询
　　* @param
　　* @return
　　* @throws
　　*/
public class MessagePolling implements java.io.Serializable{
	private static final long serialVersionUID =1L;
    private Integer id; //id
    private Integer addDate; //添加时间
    private Integer triggerDate;//触发时间
    private String messageType;//消息类型
    private Integer sendUsersId;//发送人
    private String sendUsersName;//发送人名
    private String sendUsersCode;//发送员工号
    private Integer receiveUsersId;//接收人
    private String receiveUsersName;//接收人名
    private String receiveUsersCode;//接收人员工号
    private String message;//消息内容
    private String messageTitle;//消息标题

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAddDate() {
        return addDate;
    }

    public void setAddDate(Integer addDate) {
        this.addDate = addDate;
    }

    public Integer getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(Integer triggerDate) {
        this.triggerDate = triggerDate;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Integer getSendUsersId() {
        return sendUsersId;
    }

    public void setSendUsersId(Integer sendUsersId) {
        this.sendUsersId = sendUsersId;
    }

    public String getSendUsersName() {
        return sendUsersName;
    }

    public void setSendUsersName(String sendUsersName) {
        this.sendUsersName = sendUsersName;
    }

    public Integer getReceiveUsersId() {
        return receiveUsersId;
    }

    public void setReceiveUsersId(Integer receiveUsersId) {
        this.receiveUsersId = receiveUsersId;
    }

    public String getReceiveUsersName() {
        return receiveUsersName;
    }

    public void setReceiveUsersName(String receiveUsersName) {
        this.receiveUsersName = receiveUsersName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getSendUsersCode() {
        return sendUsersCode;
    }

    public void setSendUsersCode(String sendUsersCode) {
        this.sendUsersCode = sendUsersCode;
    }

    public String getReceiveUsersCode() {
        return receiveUsersCode;
    }

    public void setReceiveUsersCode(String receiveUsersCode) {
        this.receiveUsersCode = receiveUsersCode;
    }
}