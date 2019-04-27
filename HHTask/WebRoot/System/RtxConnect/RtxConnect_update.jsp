<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="/WEB-INF/fenye.tld" prefix="fenye"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<%@include file="/util/sonHead.jsp"%>
	</head>
	<body>
		<%@include file="/util/sonTop.jsp"%>
		<div id="gongneng" style="width: 100%;">
			<div align="center">
			<h2>修改RTX连接配置信息</h2>
			 <font color="red" size="5" id="msg_font">${errorMessage}</font>
				<form action="rtxMsgAction_updateRtxConnect.action" method="post" onsubmit="check()">
					<table class="table" width="85%">
						<tr>
							<th align="right">
								rtx服务器地址
							</th>
							<td>
								<input type="text" value="${rtxConnect.rtxIp}" name="rtxConnect.rtxIp" id="rtxIp">
							</td>
							<th align="right">
								rtx端口
							</th>
							<td>
								<input type="text" value="${rtxConnect.rtxPort}" name="rtxConnect.rtxPort" id="rtxPort">
							</td>
						</tr>
						<tr>
							<th align="right">
								rtx发送统一账号
							</th>
							<td>
								<input type="text" value="${rtxConnect.sender}" name="rtxConnect.sender" id="sender">
							</td>
							<th align="right">
								rtx登录密码
							</th>
							<td>
								<input type="text" value="${rtxConnect.pwd}" name="rtxConnect.pwd" id="pwd">
							</td>
						</tr>
						<tr>
							<th align="right">
								数据库用户名
							</th>
							<td>
								<input type="text" value="${rtxConnect.userName}" name="rtxConnect.userName" id="userName">
							</td>
							<th align="right">
								数据库密码
							</th>
							<td>
								<input type="text" value="${rtxConnect.userPwd}" name="rtxConnect.userPwd" id="userPwd">
							</td>
						</tr>
						<tr>
							<th align="right">
								代理编号
							</th>
							<td>
								<input type="text" value="${rtxConnect.agentId}" name="rtxConnect.agentId"
									id="agentId" />
							</td>
							<th align="right">
								企业号信息
							</th>
							<td>
								<input type="text" value="${rtxConnect.corpId}" name="rtxConnect.corpId" id="corpId" />
							</td>
						</tr>
						<tr>
							<th align="right">应用id</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.secret}" name="rtxConnect.secret" id="secret" style="width: 500px; height:  45px;"/>
							</td>
						</tr>
						<tr>
							<th align="right">
								JDBC驱动
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.driverName}"  style="width: 500px; height:  45px;"
								name="rtxConnect.driverName" id="driverName">
							</td>
						</tr>
						<tr>
							<th align="right">
								连接服务器和数据库rtxdb地址
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.dbURL}" style="width: 500px; height:  45px;"
								name="rtxConnect.dbURL" id="dbURL">
							</td>
						</tr>
						<tr>
							<th align="right">
								pebsURL
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.pebsURL}" style="width: 500px; height:  45px;"
								name="rtxConnect.pebsURL" id="pebsURL">
							</td>
						</tr>
						<tr>
							<th align="right">
								ledURL
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.ledURL}" style="width: 500px; height:  45px;"
								name="rtxConnect.ledURL" id="ledURL">
							</td>
						</tr>
						<tr>
							<th align="right">
								发送邮件邮箱
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.spareMail}" style="width: 500px; height:  45px;"
								name="rtxConnect.spareMail" id="spareMail">
							</td>
						</tr>
							<tr>
							<th align="right">
								jdp用户名
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.jdpUserName}" style="width: 500px; height: 45px;"
									name="rtxConnect.jdpUserName" id="jdpUserName">
							</td>
						</tr>
						<tr>
							<th align="right">
								jdp密码
							</th>
							<td colspan="3">
								<input type="text" value="${rtxConnect.jdpPassWord}" style="width: 500px; height: 45px;"
									name="rtxConnect.jdpPassWord" id="jdpPassWord">
							</td>
						</tr>
						<tr>
							<th colspan="8">
								<input type="hidden" value="${rtxConnect.id}" name="rtxConnect.id">
								<input type="submit"  value="提交" class="input" id="sub" onclick="todisabled(this)"> 
							</th>
						</tr>
					</table>
				</form>
			</div>
		</div>
		<%@include file="/util/foot.jsp"%>
		<!-- JAVASCRIPT脚本写在下面 (这样页面加载速度会快一些)-->
<SCRIPT type="text/javascript">
$(function(){
	if ('${errorMessage}' == "true") {
		alert('修改成功!');
		parent.chageDiv('none');
		parent.window.location.reload();
	}
})

function check(){
	var rtxIp = $("#rtxIp").val();
	var rtxPort = $("#rtxPort").val();
	var sender = $("#sender").val();
	var pwd = $("#pwd").val();
	var driverName = $("#driverName").val();
	var dbURL = $("#dbURL").val();
	var userName = $("#userName").val();
	var userPwd = $("#userPwd").val();
	if(rtxIp==""){
		$("#msg_font").html("请填写rtxIp");
		$("#rtxIp").focus();
		return false;
	} else if(rtxPort==""){
		$("#msg_font").html("请填写rtx端口号");
		$("#rtxPort").focus();
		return false;
	}else if(sender==""){
		$("#msg_font").html("请填写rtx发送统一账号");
		$("#sender").focus();
		return false;
	}else if(pwd==""){
		$("#msg_font").html("请填写rtx登录密码");
		$("#sender").focus();
		return false;
	}else if(driverName==""){
		$("#msg_font").html("请填写JDBC驱动");
		$("#sender").focus();
		return false;
	}else if(dbURL==""){
		$("#msg_font").html("请填写连接服务器和数据库rtxdb地址");
		$("#sender").focus();
		return false;
	}else if(userName==""){
		$("#msg_font").html("请填写rtx默认用户名");
		$("#sender").focus();
		return false;
	}else if(userPwd==""){
		$("#msg_font").html("请填写数据库登录密码");
		$("#sender").focus();
		return false;
	}
	return true;
}

</SCRIPT>
	</body>
</html>

