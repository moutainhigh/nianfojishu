<%@ page language="java" import="java.util.*,com.task.entity.*"
	pageEncoding="utf-8" session="false"%>
<%@page import="javax.mail.Session"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<title>PEBS生产力平衡系统用户登录</title>
		<link rel="shortcut icon"
			href="<%=basePath%>/upload/file/sysImages/favicon.ico" />
		<link type="text/css" rel="styleSheet" href="css/login.css">
		<script type="text/javascript"
			src="<%=basePath%>/javascript/jquery-1.8.3.js">
</script>
		<style type="text/css">
.redBorder {
	border: solid 1px red;
}
</style>
		<script type="text/javascript">
//登录检查
function checkLogin() {
	var code = document.getElementById("code");
	var password = document.getElementById("password");
	if (code.value == "") {
		//document.getElementById("codeMessage").innerHTML = "<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* 员工号不能为空</font>";
		alert("请填写员工号!");
		code.focus();
		return false;
	} else {
		$("#code").css(name)
	}

	if (password.value == "") {
		//document.getElementById("passwordMessage").innerHTML = "<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;* 密码不能为空</font>";
		alert("请输入密码!");
		password.focus();
		return false;
	} else {
		//document.getElementById("passwordMessage").innerHTML = "<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;</font>";
	}
	return true;
}


function tijiao() {
	var bool = checkLogin();
	if (bool) {
		$.ajax( {
			type : "POST",
			url : "UsersAction!login.action",
			dataType : "json",
			data : $("#submit").serialize(),
			async : false,
			success : function(data) {
				if ('true' == data) {
					$("#dialog", window.parent.document).css( {
						display : "none"
					});
					$("#fullbg", window.parent.document).css( {
						display : "none"
					});
				} else if ("员工号或密码不对!" == data) {
					document.getElementById("errmessageDiv").innerHTML = data;
				} else {
					document.getElementById("errmessageDiv").innerHTML = data;
				}
			}
		});

	}

}
//自动登录处理
function autoLogin() {
	//history.go(-1);
	document.getElementById("code").select();

	var code = "";// 员工号
	var deptNum = "";// 部门编号
	var password = "";// 密码
	var vcode = "";//验证码
	var strCookie = document.cookie;//获得所有cookie
	var arrCookie = strCookie.split(";");
	for ( var i = 0; i < arrCookie.length; i++) {
		var arr = arrCookie[i].split("=");
		var arr0 = arr[0].replace(/\ /g, "");
		if ("code" == arr0) {
			code = arr[1];
		} else if ("deptNum" == arr0) {
			deptNum = arr[1];
		} else if ("password" == arr0) {
			password = arr[1];
		} else if ("vcode" == arr0) {
			vcode = arr[1];
		}
	}

	//如果存在工号的cookie，为页面赋值
	if (code.length > 0) {
		var pageCode = document.getElementById("code");
		var pageDeptNumber = document.getElementById("deptNumber");
		var pagePassword = document.getElementById("password");
		var pagePutoLogin = document.getElementById("autoLogin");
		pageCode.value = code;
		pageDeptNumber.value = deptNum;
		pagePassword.value = password;
		if (pageCode.value != "") {
			pagePutoLogin.checked = "checked";
		}
	}

	//验证码
	if (vcode == "passwordError") {
		var i = 1;
		for (i; i < 6; i++) {
			document.getElementById("v" + i).style.display = "block";
		}
		document.getElementById("v4").innerHTML = "<img src='util/vcode.jsp'>";
	}

}
</script>
	</head>
	<body onload="autoLogin()">
		<div style="background-color: #ffffff; padding: 35px;">
			<form id="submit" method="post" onsubmit="return tijiao();"
				action="">
				<div class="form-item">
					<label>
						<img src='<%=basePath%>img/user.png' height="19" width="20">
					</label>
					<input type="text" placeholder="员工号" name="user.code" id="code"
						value="${user.code}">
				</div>
				<div class="form-item">
					<label>
						<img src='<%=basePath%>img/number.png' height="19" width="20">
					</label>
					<input type="text" placeholder="部门编号" name="password.deptNumber"
						id="deptNumber" value="${password.deptNumber}">
				</div>
				<div class="form-item">
					<label>
						<img src='<%=basePath%>img/lock.png' height="21" width="15">
					</label>
					<input type="password" placeholder="密码" id="password"
						name="password.password" value="${password.password}">
				</div>
				<div id="errmessageDiv"
					style="color: red; font-size: 12px; font-weight: bolder; margin-top: 5px; height: 15px; padding-left: 45px;">
				</div>
				<div class="form-item last">
					<label class="nobr">
						<input type="checkbox" value="yes" id="autoLogin" name="autoLogin">
					</label>
					<span>一周内自动登录</span>
					<select style="margin-left: 60px" name="pageStatus" id="loginType">
						<option value="">
							简约首页
						</option>
						<option value="qx">
							清新首页
						</option>
						<option value="xk">
							炫酷首页
						</option>
					</select>
				</div>
				<div class="form-item" align="center">
					<input type="hidden" name="password1" value="qita" />
<!--					<input type="button" value="登录" onclick="tijiao()">-->
                     <input type="submit" value="登录" >
				</div>
			</form>
		</div>
		<script type="text/javascript">
var pathname = window.location.pathname;
if (/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
	//if (pathname.indexOf("login.jsp") < 0) {
	//	window.location.href = "login.jsp";
	$("#loginType").val("qx");
	$(".content").removeClass("content");
	$(".loginbox").removeClass("loginbox");
	$(".logo").hide();
	$(".logininfo").css("background-color", "#ffffff");
	//}
}
//else {
//	if (pathname.indexOf("otherLogin.jsp") < 0)
//		window.location.href = "otherLogin.jsp";
//}

</script>
	</body>
</html>
